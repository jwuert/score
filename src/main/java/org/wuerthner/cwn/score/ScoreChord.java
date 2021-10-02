package org.wuerthner.cwn.score;

import java.util.List;
import java.util.TreeSet;

import org.wuerthner.cwn.api.CwnTrack;

public class ScoreChord extends AbstractScoreObject {
	
	private final ScoreBar scoreBar;
	private final TreeSet<ScoreNote> scoreNoteSet = new TreeSet<>();
	private final boolean ambiguousDuration;
	private final boolean hasShiftedNotes;
	
	public ScoreChord(ScoreBar scoreBar, List<ScoreObject> scoreObjects) {
		super(scoreBar, scoreObjects.get(0).getStartPosition(), getShortestDuration(scoreObjects));
		boolean ambiguousDuration = false;
		for (ScoreObject scoreObject : scoreObjects) {
			scoreNoteSet.add((ScoreNote) scoreObject);
			if (scoreObject.getDuration() != duration) {
				ambiguousDuration = true;
			}
		}
		this.ambiguousDuration = ambiguousDuration;
		this.scoreBar = scoreBar;
		hasShiftedNotes = handleHorizontalShift();
	}
	
	private static long getShortestDuration(List<ScoreObject> scoreObjects) {
		long shortestDuration = Long.MAX_VALUE;
		for (ScoreObject scoreObject : scoreObjects) {
			shortestDuration = Math.min(shortestDuration, scoreObject.getDuration());
		}
		return shortestDuration;
	}
	
	public TreeSet<ScoreNote> getObjectSet() {
		return scoreNoteSet;
	}
	
	@Override
	public boolean isRest() {
		return false;
	}
	
	@Override
	public boolean isChord() {
		return true;
	}
	
	@Override
	public boolean isNote() {
		return false;
	}
	
	public boolean hasFlags() {
		return getDurationBase() >= 8;
	}
	
	public boolean hasStem() {
		return getDurationBase() < 2;
	}
	
	@Override
	public String toString(CwnTrack cwnTrack) {
		StringBuilder builder = new StringBuilder();
		builder.append("ScoreChord {minPitch=" + getMinimumNote().getPitch() + ", maxPitch=" + getMaximumNote().getPitch() + ", duration=" + duration + ", " + (ambiguousDuration ? "ambiguous duration " : "") + "[");
		// String sep = System.getProperty("line.separator") + " ";
		String sep = "";
		for (ScoreNote scoreNote : scoreNoteSet) {
			builder.append(sep);
			builder.append(scoreNote.toString(cwnTrack));
			sep = " & ";
		}
		builder.append("]}");
		return builder.toString();
	}
	
	@Override
	public String toString() {
		return toString(scoreBar.getTrack());
	}
	
	@Override
	public boolean groupable() {
		return scoreNoteSet.first().groupable();
	}
	
	public int size() {
		return scoreNoteSet.size();
	}
	
	@Override
	public ScoreNote getMinimumNote() {
		return scoreNoteSet.first();
	}
	
	@Override
	public ScoreNote getMaximumNote() {
		return scoreNoteSet.last();
	}
	
	public boolean hasAmbiguousDuration() {
		return ambiguousDuration;
	}
	
	@Override
	public double getAveragePitch() {
		return scoreNoteSet.stream().mapToInt(ScoreNote::getPitch).average().orElse(0.0);
	}

	@Override
	public boolean isSplit() { return scoreNoteSet.stream().filter(ScoreNote::isSplit).count()>0; }

	@Override
	public int getStemDirection() {
		ScoreNote first = scoreNoteSet.first();
		if (first != null) {
			int y = getY((int) (getAveragePitch() + 0.49), first.getEnharmonicShift(), first.getClef());
			int yDistance = y - Score.Y_CENTER;
			return yDistance > 0 ? 1 : -1;
		} else {
			throw new RuntimeException("Empty Chord!");
		}
	}
	
	public int fixStemDirection() {
		return (int) Math.signum(scoreNoteSet.stream().mapToInt(n -> n.fixStemDirection()).sum());
	}
	
	@Override
	public int getNumberOfFlags() {
		return scoreNoteSet.first().getNumberOfFlags();
	}
	
	private boolean handleHorizontalShift() {
		int previousY = 0;
		boolean shift = false;
		boolean hasShift = false;
		for (ScoreNote note : scoreNoteSet) {
			int y = note.getY();
			if (previousY >= 0 && Math.abs(y - previousY) < 2) {
				shift = !shift;
			}
			previousY = y;
			if (shift) {
				note.setHorizontalShift();
				hasShift = true;
			}
		}
		return hasShift;
	}
	
	public boolean hasShiftedNotes() {
		return hasShiftedNotes;
	}
}
