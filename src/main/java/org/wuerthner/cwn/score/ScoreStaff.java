package org.wuerthner.cwn.score;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.wuerthner.cwn.api.CwnTrack;
import org.wuerthner.cwn.api.ScoreParameter;

public class ScoreStaff implements Iterable<ScoreBar> {
	// private final ScoreParameter scoreParameter;
	private final List<ScoreBar> barList;
	private final CwnTrack track;
	
	public ScoreStaff(CwnTrack track) {
		barList = new ArrayList<>();
		this.track = track;
	}
	
	public ScoreStaff(CwnTrack track, ScoreParameter scoreParameter) {
		// this.scoreParameter = scoreParameter;
		// System.out.println("==================");
		// track.getList(CwnNoteEvent.class).stream().forEach(System.out::println);
		PartitionedTrack partitionedTrack = new PartitionedTrack(track, scoreParameter);
		this.track = track;
		// try {
		// Trias start = PositionTools.getTrias(track, scoreParameter.startPosition);
		// Trias end = PositionTools.getTrias(track, scoreParameter.endPosition);
		// } catch (TimeSignatureException e) {
		// e.printStackTrace();
		// }
		
		// List<CwnTimeSignatureEvent> timeSignatureEventList = track.getList(CwnTimeSignatureEvent.class);
		
		// currentBar = new Bar(currentPosition, masterInformationProvider, clef, ++barCount);
		barList = partitionedTrack.getBarList();
	}
	
	public CwnTrack getTrack() {
		return track;
	}
	
	@Override
	public Iterator<ScoreBar> iterator() {
		return barList.iterator();
	}
	
	public int size() {
		return barList.size();
	}
	
	public void setStretchFactor(double stretchFactor) {
		for (ScoreBar bar : barList) {
			bar.setStretchFactor(stretchFactor);
		}
	}
	
	public void addBar(ScoreBar scoreBar) {
		barList.add(scoreBar);
	}
	
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		for (ScoreBar bar : barList) {
			builder.append(bar.toString());
			builder.append(System.getProperty("line.separator"));
		}
		return builder.toString();
	}
	
	public Location findPosition(int x, int y, int xPos, double pixelPerTick, int yWithinStaff, int systemIndex, int staffIndex, int resolutionInTicks) {
		Location location = null;
		double xBar = 0;
		ScoreBar matchedBar = null;
		boolean barConfig = false;
		long tPos = 0;
		double barTickPerPixel = 0;
		if (barList.size() > 0 && xPos >= 0) {// barList.get(0).getOffset(pixelPerTick)) {
			// long staffStartPosition = barList.get(0).getStartPosition();
			int barNumberPerStaff = 0;
			for (ScoreBar bar : barList) {
				int barWidth = bar.getStretchedDurationAsPixel(pixelPerTick);
				boolean firstBarInTotal = (barNumberPerStaff==0) && (systemIndex==0);
				int barOffset = bar.getOffset(pixelPerTick, (barNumberPerStaff == 0), firstBarInTotal);
				barTickPerPixel = bar.getDuration() * 1.0 / barWidth;
				if (xPos >= xBar && xPos < xBar + barOffset) {
					matchedBar = bar;
					barConfig = true;
					tPos = bar.getStartPosition();
					break;
				} else if (xPos >= xBar + barOffset && xPos < xBar + barOffset + barWidth) {
					matchedBar = bar;
					barConfig = false;
					tPos = bar.getStartPosition() + (int) ((xPos - (xBar + barOffset)) * barTickPerPixel);
					break;
				}
				xBar += barWidth+barOffset;
				barNumberPerStaff++;
			}
			if (matchedBar != null) {
				tPos -= 0.5*resolutionInTicks; // because Quantization rounds up and down. We only want to round down!
				QuantizedPosition qPos = new QuantizedPosition(matchedBar, tPos, resolutionInTicks);
				long position = matchedBar.getStartPosition() + qPos.getSnappedPosition();
				// int deltaX = (int)((qPos.getSnappedPosition()-tPos)*1.0/barTickPerPixel);
				int clef = track.getClef(position).getClef();
				int pitch = getPitch(yWithinStaff, clef);
				location = new Location(matchedBar, position, pitch, staffIndex, systemIndex, barConfig, x, y, yWithinStaff);
			}
		}
		return location;
	}
	
	private int getPitch(int yPosition, int clefIndex) {
		int index = (int) (yPosition / 3) + 6;
		if (clefIndex != 0) {
			index -= Score.yClef[clefIndex];
		}
		if (index < 0) {
			index = 0;
		}
		if (index > 44) {
			index = 44;
		}
		return Score.freqTab[index];
		
	}
}
