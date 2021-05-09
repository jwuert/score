package org.wuerthner.cwn.score;

import org.wuerthner.cwn.api.CwnTrack;
import org.wuerthner.cwn.position.PositionTools;

public class ScoreRest extends AbstractScoreObject {
	
	public ScoreRest(ScoreBar scoreBar, long startPosition, long duration) {
		super(scoreBar, startPosition, duration);
	}
	
	public ScoreRest(ScoreBar scoreBar, QuantizedPosition quantizedPosition, QuantizedDuration quantizedDuration) {
		super(scoreBar, quantizedPosition, quantizedDuration);
	}
	
	@Override
	public boolean isRest() {
		return true;
	}
	
	@Override
	public boolean isChord() {
		return false;
	}
	
	@Override
	public boolean isNote() {
		return false;
	}
	
	@Override
	public boolean groupable() {
		return false;
	}
	
	@Override
	public String toString() {
		return "ScoreRest={position: " + startPosition + " (" + relativeStartPosition + "), duration: " + duration + " (" + relativeDuration + ")}";
	}
	
	public String toString(CwnTrack track) {
		// return "ScoreRest={position: " + PositionTools.getTrias(track, startPosition) + " (" + relativeStartPosition + "), duration: " + duration + " (" + relativeDuration + ") " + ", dt: " + getDurationType()
		// + ", base: " + getDurationBase() + "}";
		return "ScoreRest={position: " + PositionTools.getTrias(track, startPosition) + ", duration: " + duration + " " + ", dt: " + getDurationType() + ", base: " + getDurationBase() + "}";
	}
	
	@Override
	public int getStemDirection() {
		return 0;
	}
	
	@Override
	public int getNumberOfFlags() {
		return 0;
	}
	
	@Override
	public ScoreNote getMinimumNote() {
		return null;
	}
	
	@Override
	public ScoreNote getMaximumNote() {
		return null;
	}
	
	@Override
	public double getAveragePitch() {
		return 0;
	}
	
	@Override
	public int fixStemDirection() {
		return 0;
	}
}