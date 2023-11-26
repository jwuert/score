package org.wuerthner.cwn.score;

import java.util.*;
import java.util.stream.Collectors;

import org.wuerthner.cwn.api.*;
import org.wuerthner.cwn.markup.*;
import org.wuerthner.cwn.position.PositionTools;

public class ScoreBuilder implements Iterable<ScoreSystem> {
	private final List<ScoreSystem> systemList = new ArrayList<>();
	private ScoreParameter scoreParameter;
	private final ScoreLayout scoreLayout;
	private ScoreSystem totalSystem;
	private int numberOfShownBars = 0;
	private Iterator<ScoreBar>[] barIteratorArray;
	private ScoreBar[] barArray;
	private int maxSystemNo;
	private CwnContainer container;
	private boolean marks = false;
	private final List<Markup> markupList = Arrays.asList(
			new IntervalsMarkup(),
			new CrossingsMarkup(),
			new ParallelsMarkup(),
			new RiemannHarmonyMarkup(),
			new RiemannFunctionMarkup()
	);

	public ScoreBuilder(CwnContainer container, ScoreParameter scoreParameter, ScoreLayout scoreLayout) {
		this(container, scoreParameter, scoreLayout, 9999);
	}
	
	public ScoreBuilder(CwnContainer container, ScoreParameter scoreParameter, ScoreLayout scoreLayout, int maxSystemNo) {
		this.container = container;
		this.scoreLayout = scoreLayout;
		this.maxSystemNo = maxSystemNo;
		this.scoreParameter = scoreParameter;
		if (!container.isEmpty()) {
			update(new ScoreUpdate(ScoreUpdate.Type.REBUILD));
		}
	}

	public void update(ScoreUpdate update) {
		scoreLayout.setShowVelocity(scoreParameter.markup.contains(Markup.Type.VELOCITY));
		List<CwnTrack> trackList = container.getTrackList();
		// if (!update.redraw()) System.out.println("ScoreBuilder.update: " + update + " - # of tracks: " + trackList.size());
		if (!update.redraw() && !trackList.isEmpty()) {
			if (update.rebuild()) {
				clearMarks(trackList);
				if (!scoreParameter.markup.isEmpty()) {
					marks = true;
					List<CwnTrack> activeTrackList = trackList.stream().filter(track -> !track.isInfoTrack()).collect(Collectors.toList());
					Optional<CwnTrack> infoTrackOpt = trackList.stream().filter(track -> track.isInfoTrack()).findFirst();
					// checkPairs(activeTrackList);
					checkInvervals(activeTrackList, infoTrackOpt);
				}
			}
			if (update.full()) {
				totalSystem = new ScoreSystem(trackList, scoreParameter);
			} else if (update.rebuild()) {
				totalSystem.update(scoreParameter, update);
			}
			if (maxSystemNo == 1) {
				systemList.clear();
				systemList.add(totalSystem);
			} else {
				systemList.clear();
				splitSystems(trackList);
			}
		}
	}

	private void checkInvervals(List<CwnTrack> trackList, Optional<CwnTrack> infoTrackOpt) {
		List<Long> positions = trackList.stream()
				.flatMap(track -> track.getList(CwnNoteEvent.class).stream())
				.map(note -> note.getPosition())
				// .filter(position -> position%scoreParameter.ppq==0) // this may be used to apply plugins only to beats, not to all positions!
				.sorted()
				.distinct()
				.collect(Collectors.toList());
		if (positions.isEmpty()) {
			return;
		}
		CwnKeyEvent key = trackList.get(0).getKey(0);
		List<int[]> pitchList = new ArrayList<>();
		for (CwnTrack track : trackList) {
			int numberOfVoices = getMaxVoice(track);
			for (int v=0; v<numberOfVoices; v++) {
				int[] pitch = getPitchArray(positions, track, v);
				pitchList.add(pitch);
			}
		}
		int bassIndex = getBassIndex(pitchList);

//			System.out.println("-----------------");
//			System.out.println("** bass: " + bassIndex);
//			for (int i=0; i< pitchList.size(); i++) {
//				Arrays.stream(pitchList.get(i)).forEach(x -> System.out.print(x + " "));
//				System.out.println();
//			}
//			System.out.println("-----------------");

		for (Markup markup : markupList) {
			if (scoreParameter.markup.contains(markup.getType())) {
				markup.mark(positions, pitchList, bassIndex, key, scoreParameter, infoTrackOpt);
			}
		}
	}

	// returns the position in the list containing the lowest pitch-average
	private int getBassIndex(List<int[]> pitchList) {
		int bassIndex = 0;
		double lowestAverage = 255.0;
		int numberOfVoices = pitchList.size();
		for (int v=0; v<numberOfVoices; v++) {
			double ave = Arrays.stream(pitchList.get(v)).filter(pi -> pi > 0).average().getAsDouble();
			if (ave < lowestAverage) {
				lowestAverage = ave;
				bassIndex = v;
			}
		}
		return bassIndex;
	}

	// returns an array of pitches according to the list of positions - for a given track and voice
	// when no event occurs on a specific position, the previous pitch is used
	// TODO: this does not take the note duration into account: if eventPosition+duration < position, the pitch should NOT be written into the array, but 0 instead!
	private int[] getPitchArray(List<Long> positions, CwnTrack track, int voice) {
		int[] pitchArray = new int[positions.size()];
		int pointer = 0;
		Iterator<Long> positionIterator = positions.iterator();
		long position = positionIterator.next();
		int previousEventPitch = 0;
		for (CwnEvent event : track.getEvents()) {
			if (event instanceof CwnNoteEvent && ((CwnNoteEvent)event).getVoice() == voice) {
				long eventPosition = event.getPosition();
				int eventPitch = ((CwnNoteEvent) event).getPitch();
				while (position < eventPosition && positionIterator.hasNext()) {
					pitchArray[pointer++] = previousEventPitch;
					position = positionIterator.next();
				}
				if (position == eventPosition) {
					pitchArray[pointer++] = eventPitch;
					if (positionIterator.hasNext()) {
						position = positionIterator.next();
					} else {
						position = -1;
						break;
					}
				}
				previousEventPitch = eventPitch;
			}
		}
		if (position >= 0) {
			pitchArray[pointer++] = previousEventPitch;
			if (positionIterator.hasNext()) {
				position = positionIterator.next();
			} else {
				position = -1;
			}
		}
		return pitchArray;
	}

	private int getMaxVoice(CwnTrack track) {
		return (int) track.getEvents().stream().filter(ev -> ev instanceof CwnNoteEvent).map(ev -> ((CwnNoteEvent) ev).getVoice()).distinct().count();
	}

	private void clearMarks(List<CwnTrack> trackList) {
		if (marks) {
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
			marks = false;
		}
		scoreParameter.markupMap.clear();
	}

//	private void checkPairs(List<CwnTrack> trackList) {
//		int numberOfTracks = trackList.size();
//		for (int trackNo1 = 0; trackNo1 < numberOfTracks; trackNo1++) {
//			//
//			// TRACK 1
//			//
//			CwnTrack cwnTrack1 = trackList.get(trackNo1);
//			List<CwnNoteEvent> noteList1 = cwnTrack1.getList(CwnNoteEvent.class);
//			int numberOfNotes1 = noteList1.size();
//			CwnNoteEvent preNoteEvent1 = null;
//			for (int noteNo1 = 0; noteNo1 < numberOfNotes1; noteNo1++) {
//				//
//				// NOTE EVENT 1
//				//
//				CwnNoteEvent noteEvent1 = noteList1.get(noteNo1);
//				long startPosition1 = noteEvent1.getPosition();
//				for (int trackNo2 = trackNo1 + 1; trackNo2 < numberOfTracks; trackNo2++) {
//					//
//					// TRACK 2
//					//
//					CwnTrack cwnTrack2 = trackList.get(trackNo2);
//					List<CwnNoteEvent> noteList2 = cwnTrack2.getList(CwnNoteEvent.class);
//					int numberOfNotes2 = noteList2.size();
//					CwnNoteEvent preNoteEvent2 = null;
//					boolean matchPos = false;
//					for (int noteNo2 = 0; noteNo2 < numberOfNotes2; noteNo2++) {
//						//
//						// NOTE EVENT 2
//						//
//						CwnNoteEvent noteEvent2 = noteList2.get(noteNo2);
//						long startPosition2 = noteEvent2.getPosition();
//						if (startPosition2 == startPosition1) {
//							matchPos = true;
//							if (preNoteEvent1 != null && preNoteEvent2 != null) {
//								if (scoreParameter.markup.contains(Markup.Type.PARALLELS)) {
//									verifyParallels(preNoteEvent1, noteEvent1, preNoteEvent2, noteEvent2);
//								}
//							}
//						}
//						preNoteEvent2 = noteEvent2;
//					}
//				}
//				preNoteEvent1 = noteEvent1;
//			}
//		}
//	}
//
//	private void verifyParallels(CwnNoteEvent preNoteEvent1, CwnNoteEvent noteEvent1, CwnNoteEvent preNoteEvent2, CwnNoteEvent noteEvent2) {
//		long startPosition1 = noteEvent1.getPosition();
//		long startPosition2 = noteEvent2.getPosition();
//		int deltaNote = Math.abs(noteEvent1.getPitch() - noteEvent2.getPitch());
//		if (deltaNote == 0 || deltaNote == 7 || deltaNote == 12 || deltaNote == 19 || deltaNote == 24) {
//			int slope1 = noteEvent1.getPitch() - preNoteEvent1.getPitch();
//			int slope2 = noteEvent2.getPitch() - preNoteEvent2.getPitch();
//			boolean brk1 = (startPosition1 > preNoteEvent1.getPosition() + preNoteEvent1.getDuration() + 12);
//			boolean brk2 = (startPosition2 > preNoteEvent2.getPosition() + preNoteEvent2.getDuration() + 12);
//			if (slope1 * slope2 > 0 && !brk1 && !brk2) {
//				int delta_prev = Math.abs(preNoteEvent1.getPitch() - preNoteEvent2.getPitch());
//				if (delta_prev == deltaNote) {
//					noteEvent1.addMark("P");
//					noteEvent2.addMark("P");
//				} else {
//					noteEvent1.addMark("(P)");
//					noteEvent2.addMark("(P)");
//				}
//			}
//		}
//	}
	
	@SuppressWarnings("unchecked")
	private void splitSystems(List<CwnTrack> trackList) {
		long startPosition = PositionTools.getPosition(trackList.get(0), new Trias(scoreParameter.barOffset, 0, 0));
		long endPosition = container.findLastPosition();
		boolean isIndented = scoreParameter.barOffset == 0;
		int numberOfBars = 0;
		for (ScoreStaff staff : totalSystem) {
			numberOfBars = Math.max(numberOfBars, staff.size());
		}
		// numberOfBars += 12; // 12;
		List<Iterator<ScoreBar>> barIteratorList = new ArrayList<Iterator<ScoreBar>>();
		for (ScoreStaff scoreStaff : totalSystem) {
			barIteratorList.add(scoreStaff.getBarListWithOffset().iterator());
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
		ScoreSystem system = new ScoreSystem(scoreParameter, totalSystem);
		systemList.add(system);
		for (int bar = 0; bar < numberOfBars; bar++) {
			// isIndented = systemIndex == 0 && scoreParameter.startPosition == 0;
			isIndented = systemIndex == 0 && scoreParameter.barOffset == 0;
			incrementBar();
			// if (barArray[0].getStartPosition() > endPosition) break;
			int minShortestValue = getMinShortestValue();
			setShortestValue(minShortestValue);
			double barDuration = barArray[0].getDurationAsPixel(scoreLayout.getPixelPerTick());
			// int barOffset = barArray[0].getOffset(scoreLayout.getPixelPerTick(), bar==0, scoreParameter.startPosition==0 && bar==0); // bar==0
			int barOffset = barArray[0].getOffset(scoreLayout.getPixelPerTick(), bar==0, startPosition==0 && bar==0); // bar==0
			double barWidth = barDuration+barOffset;
			lastX = currentX;
			currentX = currentX + barWidth;
			if (currentX > maxX) {
				double leftX = scoreLayout.getBorder() + (isIndented ? scoreLayout.getSystemIndent() : 0);
				// double stretchFactor = (maxX - leftX - sumBarOffset) / (lastX - leftX - sumBarOffset);
				double stretchFactor = (maxX - leftX - sumBarOffset) / sumBarDuration;
				system.setStretchFactor(stretchFactor);
				currentX = scoreLayout.getBorder();
				sumBarDuration = 0;
				sumBarOffset = 0;

				barDuration = barArray[0].getDurationAsPixel(scoreLayout.getPixelPerTick());
				barOffset = barArray[0].getOffset(scoreLayout.getPixelPerTick(), true, false);
				barWidth = barDuration+barOffset;
				currentX = currentX + barWidth;
				systemIndex++;
				if (systemIndex > maxSystemNo) {
					break;
				}
				system = new ScoreSystem(scoreParameter, totalSystem);
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
	
	public Location findPosition(int x, int y, int resolutionInTicks, long startPosition) {
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

			// int xPos = x - (scoreLayout.getBorder() + (systemIndex == 0 && scoreParameter.startPosition == 0 ? scoreLayout.getSystemIndent() : 0));
			int xPos = x - (scoreLayout.getBorder() + (systemIndex == 0 && startPosition == 0 ? scoreLayout.getSystemIndent() : 0));
			if (systemIndex >= 0 && systemIndex < systemList.size()) {
				ScoreSystem system = systemList.get(systemIndex);
				ScoreStaff staff = system.get(staffIndex);
				location = staff.findPosition(x, y, xPos, scoreLayout.getPixelPerTick(), yWithinStaff, systemIndex, staffIndex, resolutionInTicks);
			}
		}
		return location;
	}

	public Coordinates findCoordinates(long position, int staffIndex) {
		int resultX = -1;
		int systemIndex = 0;
		int yPosition = 0;
		boolean fbis = false;
		boolean fbit = false;
		for (ScoreSystem system : systemList) {
			int x = 0;
			ScoreStaff staff = system.get(staffIndex);
			for (int b=0; b<staff.getBarListSize(); b++) {
				ScoreBar bar = staff.getBar(b);
				Metric metric = bar.getTimeSignature().getMetric();
				double barWidthInPixel = bar.getStretchedDurationAsPixel(scoreLayout.getPixelPerTick());
				fbis = b==0;
				fbit = fbis && scoreParameter.getBarOffset()==0 && systemIndex==0;
				x += bar.getOffset(scoreLayout.getPixelPerTick(), fbis, fbit);
				if (position >= bar.getStartPosition() && position < bar.getEndPosition()) {
					int positionInBar = (int) (position - bar.getStartPosition());
					double relPositionInBar = positionInBar *1.0/ bar.getDuration();
					resultX = x + (int)(barWidthInPixel*relPositionInBar);
					break;
				}
				x += barWidthInPixel;
			}
			if (resultX >= 0) {
				break;
			}
			systemIndex++;
		}
		boolean indent = scoreParameter.getBarOffset()==0 && systemIndex==0;
		int xPosition = scoreLayout.getBorder() + (indent ? scoreLayout.getSystemIndent() : 0);
		xPosition += resultX - 10;
		int yTop = scoreLayout.getBorder() + scoreLayout.getTitleHeight() + (staffIndex + systemIndex * getNumberOfTracks()) * scoreLayout.getStaffHeight();
		yPosition = yTop + scoreLayout.getStaffHeight()-4;
		return new Coordinates(xPosition, yPosition);
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
		return container.getTrackList();
	}

	public ScoreParameter getScoreParameter() { return scoreParameter; }

	public ScoreSystem getSystem(int i) { return systemList.get(i); }

	public void setContainer(CwnContainer container) {
		this.container = container;
	}

	public void setNumberOfSystems(int numberOfSystems) {
		this.maxSystemNo = numberOfSystems;
	}

	public class Coordinates {
		public final int X;
		public final int Y;
		public Coordinates(int x, int y) {
			this.X = x;
			this.Y = y;
		}
	}
}
