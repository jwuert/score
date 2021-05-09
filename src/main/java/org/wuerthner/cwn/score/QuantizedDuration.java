package org.wuerthner.cwn.score;

import java.util.List;

import org.wuerthner.cwn.api.DurationType;
import org.wuerthner.cwn.api.ScoreParameter;

public class QuantizedDuration {
	
	private final DurationType durationType;
	private final int snappedDuration;
	private final int snappedPower;
	
	/**
	 * This QuantizedDuration constructor quantizes based on the supported duration-types as given by the scoreParameter object
	 * 
	 * @param scoreParameter
	 * @param duration
	 */
	public QuantizedDuration(ScoreParameter scoreParameter, long duration) {
		this(scoreParameter, duration, scoreParameter.getSupportedDurationTypes());
	}
	
	/**
	 * This constructor quantizes based on the duration-types specified by the durationTypeCharacter: when the character is "triplet", the supported types are all triplet variants with zero to three dots!
	 * 
	 * @param scoreParameter
	 * @param duration
	 * @param durationTypeCharacter
	 */
	public QuantizedDuration(ScoreParameter scoreParameter, long duration, DurationType durationTypeCharacter) {
		this(scoreParameter, duration, DurationType.getSupportedTypesForCharacter(durationTypeCharacter));
	}
	
	private QuantizedDuration(ScoreParameter scoreParameter, long duration, List<DurationType> durationTypeList) {
		int durationTypeSize = durationTypeList.size();
		int[][] minMatrix = new int[durationTypeSize][];
		int totalMinimum = Integer.MAX_VALUE;
		DurationType durationType = durationTypeList.get(0);
		int snappedDuration = 0;
		int snappedPower = 0;
		for (int n = 0; n < durationTypeSize; n++) {
			DurationType type = durationTypeList.get(n);
			minMatrix[n] = getMinDelta(type, scoreParameter, duration);
			totalMinimum = Math.min(totalMinimum, minMatrix[n][0]);
		}
		for (int n = 0; n < durationTypeSize; n++) {
			DurationType type = durationTypeList.get(n);
			if (totalMinimum == minMatrix[n][0]) {
				durationType = type;
				snappedDuration = minMatrix[n][1];
				snappedPower = minMatrix[n][2];
				break;
			}
		}
		this.durationType = durationType;
		this.snappedDuration = snappedDuration;
		this.snappedPower = snappedPower;
	}
	
	public DurationType getType() {
		return durationType;
	}
	
	public int getSnappedDuration() {
		return snappedDuration;
	}
	
	public int getPower() {
		return snappedPower;
	}
	
	@Override
	public String toString() {
		return "duration: " + snappedDuration + ", type: " + durationType;
	}
	
	private int[] getMinDelta(DurationType type, ScoreParameter scoreParameter, long duration) {
		int resolutionTicks = scoreParameter.getResolutionInTicks();
		double value = (resolutionTicks / type.getFactor());
		int minDeltaTicks = Integer.MAX_VALUE;
		int snappedDuration = 0;
		int snappedPower = 0;
		for (int i = 0; i < 10; i++) {
			double valueDuration = value * Math.pow(2, i);
			int deltaTicks = (int) Math.abs(duration - valueDuration);
			if (deltaTicks < minDeltaTicks) {
				minDeltaTicks = deltaTicks;
				snappedDuration = (int) valueDuration;
				snappedPower = i;
			}
		}
		return new int[] { minDeltaTicks, snappedDuration, snappedPower };
	}
}
