package org.wuerthner.cwn.score;

import org.wuerthner.cwn.api.DurationType;

public abstract class AbstractScoreObject implements ScoreObject {
	
	protected final long startPosition;
	protected final long duration;
	protected final double relativeStartPosition;
	protected final double relativeDuration;
	protected final DurationType durationType;
	// protected final int power;
	
	protected final int ppq;
	
	public AbstractScoreObject(ScoreBar scoreBar, long startPosition, long duration, DurationType durationType) {
		this.startPosition = startPosition;
		this.duration = duration;
		this.durationType = durationType; // DurationType.REGULAR;
		this.relativeStartPosition = (startPosition - scoreBar.getStartPosition()) * 1.0 / scoreBar.getDuration();
		this.relativeDuration = duration * 1.0 / scoreBar.getDuration();
		this.ppq = scoreBar.getScoreParameter().ppq;
		// this.power = 0; // TODO!
	}
	
	public AbstractScoreObject(ScoreBar scoreBar, QuantizedPosition quantizedPosition, QuantizedDuration quantizedDuration) {
		startPosition = scoreBar.getStartPosition() + quantizedPosition.getSnappedPosition();
		duration = quantizedDuration.getSnappedDuration();
		durationType = quantizedDuration.getType();
		relativeStartPosition = quantizedPosition.getSnappedPosition() * 1.0 / scoreBar.getDuration();
		relativeDuration = quantizedDuration.getSnappedDuration() * 1.0 / scoreBar.getDuration();
		ppq = scoreBar.getScoreParameter().ppq;
		// power = quantizedDuration.getPower();
	}
	
	@Override
	public long getStartPosition() {
		return startPosition;
	}
	
	@Override
	public long getEndPosition() {
		return startPosition + duration;
	}
	
	@Override
	public long getDuration() {
		return duration;
	}
	
	@Override
	/**
	 * Examples: 1, 2, 4, 8, 16, 32, etc
	 */
	public int getDurationBase() {
		return (int) (ppq * 4.0 / (durationType.getFactor() * duration));
	}
	
	@Override
	public int getY(int pitch, int enharmonicShift, int clef) {
		int ypos = 0;
		int step = pitch % 12;
		ypos = Score.invPitch[pitch];
		if (enharmonicShift != 0) {
			ypos += Score.enhF[enharmonicShift + 2][step];
		}
		ypos += Score.yClef[clef];
		
		if (ypos < 1) {
			ypos = 1;
		}
		return ypos;
	}
	
	@Override
	public double getRelativePosition() {
		return relativeStartPosition;
	}
	
	@Override
	public double getRelativeDuration() {
		return relativeDuration;
	}
	
	@Override
	public int compareTo(ScoreObject scoreObject) {
		return (int) (this.startPosition - scoreObject.getStartPosition());
	}
	
	public abstract boolean isRest();
	
	public abstract boolean isChord();
	
	public abstract boolean isNote();

	@Override
	public boolean isSplit() { return false; }
	
	@Override
	public int getNumberOfDots() {
		return durationType.getDots();
	}
	
	@Override
	public DurationType getDurationType() {
		return durationType;
	}
}
