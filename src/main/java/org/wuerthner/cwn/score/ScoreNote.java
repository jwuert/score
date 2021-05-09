package org.wuerthner.cwn.score;

import java.util.Optional;

import org.wuerthner.cwn.api.CwnNoteEvent;
import org.wuerthner.cwn.api.CwnTrack;
import org.wuerthner.cwn.position.PositionTools;

public class ScoreNote extends AbstractScoreObject {
	private final Optional<CwnNoteEvent> cwnNoteEvent;
	private final boolean isSplitNote;
	private final boolean isUngrouped;
	private final boolean reducedDuration;
	private final int clef;
	private final int stemDirection;
	private boolean horizontalShift;
	private int sign;
	
	public ScoreNote(ScoreBar scoreBar, CwnNoteEvent cwnNoteEvent, QuantizedPosition quantizedPosition, QuantizedDuration quantizedDuration, int stemDirection, boolean isUngrouped) {
		super(scoreBar, quantizedPosition, quantizedDuration);
		this.cwnNoteEvent = Optional.of(cwnNoteEvent);
		this.isSplitNote = (cwnNoteEvent.getDuration() > duration);
		this.clef = scoreBar.getClef();
		this.reducedDuration = false;
		this.stemDirection = (stemDirection != 0 ? stemDirection : determineStemDirection());
		this.horizontalShift = false;
		this.isUngrouped = isUngrouped;
	}
	
	// Constructor for reduced duration!
	public ScoreNote(ScoreNote originalNote, ScoreBar scoreBar, long newDuration) {
		super(scoreBar, originalNote.getStartPosition(), newDuration);
		this.cwnNoteEvent = originalNote.cwnNoteEvent;
		this.isSplitNote = originalNote.isSplitNote;
		this.stemDirection = originalNote.stemDirection;
		this.clef = scoreBar.getClef();
		this.reducedDuration = true;
		this.horizontalShift = false;
		this.isUngrouped = false;
	}
	
	// Constructor for tests without CwnNoteEvent!
	ScoreNote(ScoreBar scoreBar, long start, long duration) {
		super(scoreBar, start, duration);
		this.cwnNoteEvent = Optional.empty();
		this.isSplitNote = cwnNoteEvent.map(ne -> ne.getDuration() > duration).orElse(false);
		this.clef = scoreBar.getClef();
		this.reducedDuration = false;
		this.stemDirection = 0;
		this.horizontalShift = false;
		this.isUngrouped = false;
	}
	
	// Constructor for tests
	ScoreNote(ScoreBar scoreBar, CwnNoteEvent cwnNoteEvent) {
		super(scoreBar, cwnNoteEvent.getPosition(), cwnNoteEvent.getDuration());
		this.cwnNoteEvent = Optional.of(cwnNoteEvent);
		this.isSplitNote = false;
		this.clef = scoreBar.getClef();
		this.reducedDuration = false;
		this.stemDirection = 0;
		this.horizontalShift = false;
		this.isUngrouped = false;
	}
	
	private int determineStemDirection() {
		int noteEventStemDirection = cwnNoteEvent.get().getStemDirection();
		return noteEventStemDirection != 0 ? noteEventStemDirection : getYDistanceFromCenter() > 0 ? 1 : -1;
	}
	
	public int getYDistanceFromCenter() {
		return getY() - Score.Y_CENTER;
	}
	
	public int getStemDirection() {
		return stemDirection;
	}
	
	public int fixStemDirection() {
		return cwnNoteEvent.get().getStemDirection();
	}
	
	public boolean hasReducedDuration() {
		return reducedDuration;
	}
	
	public CwnNoteEvent getCwnNoteEvent() {
		return cwnNoteEvent.orElse(null);
	}
	
	@Override
	public boolean isRest() {
		return false;
	}
	
	@Override
	public boolean isChord() {
		return false;
	}
	
	@Override
	public boolean isNote() {
		return true;
	}
	
	public boolean hasFlags() {
		return getDurationBase() >= 8;
	}
	
	public int getNumberOfFlags() {
		return log2n(getDurationBase()) - 2;
	}
	
	static int log2n(int n) {
		return (n > 1) ? 1 + log2n(n / 2) : 0;
	}
	
	@Override
	public boolean groupable() {
		return hasFlags() && !isUngrouped;
	}
	
	public boolean isSplit() {
		return isSplitNote;
	}
	
	public boolean hasStartTie() {
		return isSplitNote && cwnNoteEvent.map(ne -> ne.getPosition() + ne.getDuration() > getEndPosition()).orElse(false);
	}
	
	public boolean hasEndTie() {
		return isSplitNote && cwnNoteEvent.map(ne -> ne.getPosition() < getStartPosition()).orElse(false);
	}
	
	public int getPitch() {
		return cwnNoteEvent.map(ne -> ne.getPitch()).orElse(0);
	}
	
	public double getAveragePitch() {
		return getPitch();
	}
	
	public int getVoice() {
		return cwnNoteEvent.map(ne -> ne.getVoice()).orElse(0);
	}
	
	public int getEnharmonicShift() {
		return cwnNoteEvent.map(ne -> ne.getEnharmonicShift()).orElse(0);
	}
	
	public int getClef() {
		return clef;
	}
	
	public void setSign(int sign) {
		this.sign = sign;
	}
	
	public int getSign() {
		return sign;
	}
	
	public int getY() {
		return getY(getPitch(), getEnharmonicShift(), getClef());
	}
	
	public int getHorizontalShift() {
		return (horizontalShift ? (int) Score.NOTE_HEAD_WIDTH + 1 : 0);
	}
	
	public void setHorizontalShift() {
		horizontalShift = true;
	}
	
	public String getLyrics() {
		return cwnNoteEvent.map(ne -> ne.getLyrics()).orElse("");
	}
	
	// TODO:
	public boolean hasOrnaments() {
		return false;
	}
	
	@Override
	public int compareTo(ScoreObject element) {
		long thisPosition = startPosition;
		long thatPosition = element.getStartPosition();
		if (thisPosition == thatPosition) {
			return (int) (this.getPitch() - ((ScoreNote) element).getPitch());
		} else {
			return (int) (thisPosition - thatPosition);
		}
	}
	
	@Override
	public String toString() {
		return "ScoreNote={position: " + startPosition + " (" + relativeStartPosition + "), duration: " + duration + " (" + relativeDuration + "), pitch: " + getPitch() + "}";
	}
	
	public String toString(CwnTrack track) {
		// return "ScoreNote={position: " + PositionTools.getTrias(track, startPosition) + " (" + relativeStartPosition + "), duration: " + duration + " (" + relativeDuration + "), pitch: " + getPitch()
		// + (groupable() ? " G" : " -") + ", dt: " + getDurationType() + ", base: " + getDurationBase() + "}";
		return "ScoreNote={position: " + PositionTools.getTrias(track, startPosition) + ", duration: " + duration + ", pitch: " + getPitch() + (groupable() ? " G" : " -") + ", dt: " + getDurationType() + ", base: "
				+ getDurationBase() + "}";
	}
	
	@Override
	public ScoreNote getMinimumNote() {
		return this;
	}
	
	@Override
	public ScoreNote getMaximumNote() {
		// TODO Auto-generated method stub
		return this;
	}
}
