package org.wuerthner.cwn.score;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;

import org.wuerthner.cwn.api.CwnNoteEvent;
import org.wuerthner.cwn.api.CwnTrack;
import org.wuerthner.cwn.api.ScoreLayout;
import org.wuerthner.cwn.api.ScoreParameter;

public class ScoreBuilder implements Iterable<ScoreSystem> {
	private final List<ScoreSystem> systemList = new ArrayList<>();
	private final ScoreParameter scoreParameter;
	private final ScoreLayout scoreLayout;
	private ScoreSystem totalSystem;
	private int numberOfShownBars = 0;
	private Iterator<ScoreBar>[] barIteratorArray;
	private ScoreBar[] barArray;
	private final int maxSystemNo;
	private final List<CwnTrack> trackList;
	
	public ScoreBuilder(List<CwnTrack> trackList, ScoreParameter scoreParameter, ScoreLayout scoreLayout) {
		this(trackList, scoreParameter, scoreLayout, 9999);
	}
	
	public ScoreBuilder(List<CwnTrack> trackList, ScoreParameter scoreParameter, ScoreLayout scoreLayout, int maxSystemNo) {
		this.trackList = trackList;
		this.scoreLayout = scoreLayout;
		this.maxSystemNo = maxSystemNo;
		this.scoreParameter = scoreParameter;
		if (scoreParameter!=null && scoreParameter.markup) {
			clearMarks(trackList);
			checkParallels(trackList);
		}
		if (!trackList.isEmpty()) {
			totalSystem = new ScoreSystem(trackList, scoreParameter);
			if (maxSystemNo == 1) {
				systemList.add(totalSystem);
			} else {
				splitSystems(trackList);
			}
		}
	}
	
	private void clearMarks(List<CwnTrack> trackList) {
		int numberOfTracks = trackList.size();
		for (int trackNo = 0; trackNo < numberOfTracks; trackNo++) {
			CwnTrack cwnTrack = trackList.get(trackNo);
			List<CwnNoteEvent> noteList = cwnTrack.getList(CwnNoteEvent.class);
			int numberOfNotes = noteList.size();
			for (int noteNo = 0; noteNo < numberOfNotes; noteNo++) {
				CwnNoteEvent noteEvent = noteList.get(noteNo);
				noteEvent.clearMark();
			}
		}
	}
	
	private void checkParallels(List<CwnTrack> trackList) {
		int numberOfTracks = trackList.size();
		for (int trackNo1 = 0; trackNo1 < numberOfTracks; trackNo1++) {
			//
			// TRACK 1
			//
			CwnTrack cwnTrack1 = trackList.get(trackNo1);
			List<CwnNoteEvent> noteList1 = cwnTrack1.getList(CwnNoteEvent.class);
			int numberOfNotes1 = noteList1.size();
			CwnNoteEvent preNoteEvent1 = null;
			for (int noteNo1 = 0; noteNo1 < numberOfNotes1; noteNo1++) {
				//
				// NOTE EVENT 1
				//
				CwnNoteEvent noteEvent1 = noteList1.get(noteNo1);
				if (preNoteEvent1 != null) {
					long startPosition1 = noteEvent1.getPosition();
					for (int trackNo2 = trackNo1 + 1; trackNo2 < numberOfTracks; trackNo2++) {
						//
						// TRACK 2
						//
						CwnTrack cwnTrack2 = trackList.get(trackNo2);
						List<CwnNoteEvent> noteList2 = cwnTrack2.getList(CwnNoteEvent.class);
						int numberOfNotes2 = noteList2.size();
						CwnNoteEvent preNoteEvent2 = null;
						for (int noteNo2 = 0; noteNo2 < numberOfNotes2; noteNo2++) {
							//
							// NOTE EVENT 2
							//
							CwnNoteEvent noteEvent2 = noteList2.get(noteNo2);
							if (preNoteEvent2 != null) {
								long startPosition2 = noteEvent2.getPosition();
								if (startPosition2 == startPosition1) {
									verifyNotes(preNoteEvent1, noteEvent1, preNoteEvent2, noteEvent2);
								}
							}
							preNoteEvent2 = noteEvent2;
						}
					}
				}
				preNoteEvent1 = noteEvent1;
			}
		}
	}
	
	private void verifyNotes(CwnNoteEvent preNoteEvent1, CwnNoteEvent noteEvent1, CwnNoteEvent preNoteEvent2, CwnNoteEvent noteEvent2) {
		long startPosition1 = noteEvent1.getPosition();
		long startPosition2 = noteEvent2.getPosition();
		int deltaNote = Math.abs(noteEvent1.getPitch() - noteEvent2.getPitch());
		if (deltaNote == 0 || deltaNote == 7 || deltaNote == 12 || deltaNote == 19 || deltaNote == 24) {
			int slope1 = noteEvent1.getPitch() - preNoteEvent1.getPitch();
			int slope2 = noteEvent2.getPitch() - preNoteEvent2.getPitch();
			boolean brk1 = (startPosition1 > preNoteEvent1.getPosition() + preNoteEvent1.getDuration() + 12);
			boolean brk2 = (startPosition2 > preNoteEvent2.getPosition() + preNoteEvent2.getDuration() + 12);
			if (slope1 * slope2 > 0 && !brk1 && !brk2) {
				int delta_prev = Math.abs(preNoteEvent1.getPitch() - preNoteEvent2.getPitch());
				if (delta_prev == deltaNote) {
					noteEvent1.addMark("P");
					noteEvent2.addMark("P");
				} else {
					noteEvent1.addMark("(P)");
					noteEvent2.addMark("(P)");
				}
			}
		}
	}
	
	@SuppressWarnings("unchecked")
	private void splitSystems(List<CwnTrack> trackList) {
		boolean isIndented = scoreParameter.startPosition == 0;
		int numberOfBars = 0;
		for (ScoreStaff staff : totalSystem) {
			numberOfBars = Math.max(numberOfBars, staff.size());
		}
		numberOfBars += 12;
		List<Iterator<ScoreBar>> barIteratorList = new ArrayList<Iterator<ScoreBar>>();
		for (ScoreStaff score : totalSystem) {
			barIteratorList.add(score.iterator());
		}
		barIteratorArray = barIteratorList.toArray(new Iterator[] {});
		int numberOfScores = barIteratorArray.length;

		barArray = new ScoreBar[numberOfScores];
		double currentX = scoreLayout.getBorder() + (isIndented ? scoreLayout.getSystemIndent() : 0); // ?? + geometry.firstIndent;
		double sumBarDuration = 0;
		int sumBarOffset = 0;
		double lastX = 0;
		int systemIndex = 0;
		double maxX = scoreLayout.getWidth() - scoreLayout.getBorder();
		ScoreSystem system = new ScoreSystem(totalSystem);
		systemList.add(system);

		for (int bar = 0; bar < numberOfBars; bar++) {
			isIndented = systemIndex == 0 && scoreParameter.startPosition == 0;
			incrementBar();
			int minShortestValue = getMinShortestValue();
			setShortestValue(minShortestValue);
			double barDuration = barArray[0].getDurationAsPixel(scoreLayout.getPixelPerTick());
			int barOffset = barArray[0].getOffset(scoreLayout.getPixelPerTick(), bar==0, scoreParameter.startPosition==0 && bar==0); // bar==0
			double barWidth = barDuration+barOffset;
			lastX = currentX;
			currentX = currentX + barWidth;
			if (currentX > maxX) {
				double leftX = scoreLayout.getBorder() + (isIndented ? scoreLayout.getSystemIndent() : 0);
				double stretchFactor = (maxX - leftX - sumBarOffset) / (lastX - leftX - sumBarOffset);
				system.setStretchFactor(stretchFactor);

				currentX = scoreLayout.getBorder();
				sumBarDuration = 0;
				sumBarOffset = 0;

				barDuration = barArray[0].getDurationAsPixel(scoreLayout.getPixelPerTick());
				barOffset = barArray[0].getOffset(scoreLayout.getPixelPerTick(), true, bar==0);
				barWidth = barDuration+barOffset;
				currentX = currentX + barWidth;
				systemIndex++;
				if (systemIndex > maxSystemNo) {
					break;
				}
				system = new ScoreSystem(totalSystem);
				addBarsToSystem(system);
				systemList.add(system);
			} else {
				addBarsToSystem(system);
			}
			sumBarDuration += barDuration;
			sumBarOffset += barOffset;
			numberOfShownBars = bar + 1;
		}
	}
	
	private void incrementBar() {
		for (int scoreIndex = 0; scoreIndex < barArray.length; scoreIndex++) {
			try {
				barArray[scoreIndex] = barIteratorArray[scoreIndex].next();
			} catch (NoSuchElementException e) {
				// add rest at the end
				barArray[scoreIndex] = new ScoreBar(barArray[scoreIndex]);
			}
		}
	}
	
	private int getMinShortestValue() {
		int minShortestValue = Integer.MAX_VALUE;
		for (int scoreIndex = 0; scoreIndex < barArray.length; scoreIndex++) {
			minShortestValue = Math.min(minShortestValue, barArray[scoreIndex].getShortestValue());
		}
		return minShortestValue;
	}
	
	private void setShortestValue(int value) {
		for (int scoreIndex = 0; scoreIndex < barArray.length; scoreIndex++) {
			barArray[scoreIndex].setShortestValue(value);
		}
	}
	
	private void addBarsToSystem(ScoreSystem system) {
		for (int staffIndex = 0; staffIndex < barArray.length; staffIndex++) {
			system.addBarToStaff(staffIndex, barArray[staffIndex]);
		}
	}
	
	public int getNumberOfShownBars() {
		return numberOfShownBars;
	}
	/*
	 * @formatter:off
	 
	public MousePosition findPosition(int x, int y) {
		currentMousePosition = null;
		int yPos = y - Geometry.TITLE_HEIGHT - Geometry.BORDER;
		if (yPos >= 0) {
			int systemIndex = (int) (yPos / geometry.systemHeight);
			if (systemIndex < systemList.size()) {
				int xPos = x - geometry.xOffset - geometry.firstIndent - 4;
				if (systemIndex == 0) {
					xPos = xPos - Geometry.SCORE_INDENT;
				}
				// if (xPos > 0) {
				int yWithinSystem = (int) (yPos % geometry.systemHeight);
				int scoreIndex = (int) (yWithinSystem / geometry.scoreHeight);
				int yWithinScore = (int) ((yWithinSystem % geometry.scoreHeight) / geometry.zoomFactor);
				System sys = systemList.get(systemIndex);
				long thePos = getThePosition(Math.max(0, xPos), sys);
				if (thePos >= 0 && xPos > -20) {
					currentMousePosition = new MousePosition(thePos, yWithinScore - 1, barArray[scoreIndex].getClef(), scoreIndex);
				} else {
					currentMousePosition = new MousePosition(-1, yWithinScore - 1, barArray[scoreIndex].getClef(), scoreIndex);
				}
				// }
			}
		}
		return currentMousePosition;
	}
	
	private long getThePosition(int xPos, System sys) {
		long thePos;
		thePos = sys.findPosition((int) (xPos / geometry.pixelPerTick));
		int ppq = masterInformationProvider.getTicksPerQuarter();
		if (thePos < 0 && thePos >= -ppq) {
			thePos = 0;
		}
		return thePos;
	}
	
 	* @formatter:on
	*/
	
	@Override
	public Iterator<ScoreSystem> iterator() {
		return systemList.iterator();
	}
	
	public int size() {
		return systemList.size();
	}
	
	public int getNumberOfTracks() {
		return totalSystem==null ? 0 : totalSystem.size();
	}
	
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		int count = 1;
		for (ScoreSystem system : systemList) {
			builder.append("=== System " + count++ + " ===" + System.getProperty("line.separator"));
			builder.append(system.toString());
			builder.append(System.getProperty("line.separator"));
		}
		return builder.toString();
	}
	
	public Location findPosition(int x, int y, int resolutionInTicks) {
		Location location = null;
		int yPos = y - scoreLayout.getTitleHeight() - scoreLayout.getBorder() - scoreLayout.getSystemSpace();
		if (yPos >= 0 && totalSystem != null) {
			int numberOfStaffs = totalSystem.size();
			int staffHeight = scoreLayout.getStaffHeight();
			int systemHeight = staffHeight * numberOfStaffs;
			int systemIndex = (int) (yPos / systemHeight);
			
			int yWithinSystem = (int) (yPos % systemHeight);
			int staffIndex = (int) (yWithinSystem / staffHeight);
			int yWithinStaff = (int) (yWithinSystem % staffHeight) - scoreLayout.lyricsSpace();
			
			int xPos = x - (scoreLayout.getBorder() + (systemIndex == 0 && scoreParameter.startPosition == 0 ? scoreLayout.getSystemIndent() : 0));
			if (systemIndex >= 0 && systemIndex < systemList.size()) {
				ScoreSystem system = systemList.get(systemIndex);
				ScoreStaff staff = system.get(staffIndex);
				location = staff.findPosition(x, y, xPos, scoreLayout.getPixelPerTick(), yWithinStaff, systemIndex, staffIndex, resolutionInTicks);
			}
		}
		return location;
	}
	
	public int findTrackIndex(int y) {
		int yPos = y - scoreLayout.getTitleHeight() - scoreLayout.getBorder();
		int index = -1;
		if (yPos >= 0 && totalSystem != null) {
			int numberOfStaffs = totalSystem.size();
			int staffHeight = scoreLayout.getStaffHeight();
			int systemHeight = staffHeight * numberOfStaffs;
			int yWithinSystem = (int) (yPos % systemHeight);
			int staffIndex = (int) (yWithinSystem / staffHeight);
			index = staffIndex;
		}
		return index;
	}

	public List<CwnTrack> getTrackList() {
		return trackList;
	}
}
