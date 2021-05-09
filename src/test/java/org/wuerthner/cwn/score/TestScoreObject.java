package org.wuerthner.cwn.score;

import org.wuerthner.cwn.api.CwnTrack;
import org.wuerthner.cwn.position.PositionTools;

public class TestScoreObject extends ScoreNote {
	private final int voice;
	
	public TestScoreObject(ScoreBar scoreBar, long startPosition, long duration, int voice) {
		super(scoreBar, startPosition, duration);
		this.voice = voice;
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
		return false;
	}
	
	public boolean hasNoFlags() {
		return false;
	}
	
	@Override
	public String toString() {
		return "TestScoreObject={position: " + startPosition + ", duration: " + duration + "}";
	}
	
	@Override
	public String toString(CwnTrack cwnTrack) {
		return "TestScoreObject={position: " + PositionTools.getTrias(cwnTrack, startPosition) + ", duration: " + duration + "}";
	}
	
	@Override
	public int getVoice() {
		return voice;
	}
}
