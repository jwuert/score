package org.wuerthner.cwn.score;

import java.util.TreeSet;

import org.wuerthner.cwn.api.Metric;

public class CharacterGroup implements Comparable<CharacterGroup> {
	private final double relativePosition;
	private final int character;
	private final String fullCharacter;
	private final int ppq;
	private double relativeDuration;
	
	public CharacterGroup(Metric metric, double relativePosition, TreeSet<ScoreObject> scoreObjectSet, int ppq) {
		this.relativePosition = relativePosition;
		this.relativeDuration = metric.duration();
		this.character = metric.getDurationType().getCharacter();
		this.fullCharacter = metric.getDurationType().getPresentation();
		this.ppq = ppq;
	}
	
	public void updateGroup(double duration, TreeSet<ScoreObject> scoreObjectSet) {
		this.relativeDuration += duration;
	}
	
	public int getCharacter() {
		return character;
	}
	
	public String getFullCharacter() {
		return fullCharacter;
	}
	
	public double getRelativePosition() {
		return relativePosition;
	}
	
	public double getRelativeDuration() {
		return relativeDuration;
	}
	
	public String toString() {
		return "CharacterGroup {relativePosition=" + relativePosition + ", relativeDuration=" + relativeDuration + ", character=" + fullCharacter + "}";
	}
	
	@Override
	public int compareTo(CharacterGroup characterGroup) {
		return (int) (ppq * (this.relativePosition - characterGroup.relativePosition));
	}
}
