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
		this(scoreParameter, duration, false, scoreParameter.getSupportedDurationTypes());
	}
	
	/**
	 * This constructor quantizes based on the duration-types specified by the durationTypeCharacter: when the character is "triplet", the supported types are all triplet variants with zero to three dots!
	 * 
	 * @param scoreParameter
	 * @param duration
	 * @param durationTypeCharacter
	 */
	public QuantizedDuration(ScoreParameter scoreParameter, long duration, DurationType durationTypeCharacter) {
		this(scoreParameter, duration, durationTypeCharacter!=DurationType.REGULAR, DurationType.getSupportedTypesForCharacter(durationTypeCharacter));
	}
	
	 public QuantizedDuration(ScoreParameter scoreParameter, long duration, boolean nonRegularCharacterInMetric, List<DurationType> durationTypeList) {
		int minDelta = getMinDelta(DurationType.REGULAR, scoreParameter, duration)[0];
		if (nonRegularCharacterInMetric && minDelta==0) {
			durationTypeList.add(0, DurationType.REGULAR); // in order to fix a bug, so that a regular duration typed note is not turned into a dotted triplet!
		}
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
	
	int[] getMinDelta(DurationType type, ScoreParameter scoreParameter, long duration) {
		int resolutionTicks = scoreParameter.getResolutionInTicks(); // e.g. 240
		double value = (resolutionTicks / type.getFactor()); // reg: 240 - trip: 160 - quint: 192
		int minDeltaTicks = Integer.MAX_VALUE;
		int snappedDuration = 0;
		int snappedPower = 0;
		for (int i = 0; i < 10; i++) {
			double valueDuration = value * Math.pow(2, i); // reg: 240, 480, 960, 1920 - trip: 160, 320, 640, 1280 - quint: 192, 384, 768, 1536
			for (double factor = 1.0; factor <= 1.25; factor += 0.25) {
				if (factor==1.0 || type==DurationType.REGULAR) { // only if REGULAR, test on factor 1.25 in addition (e.g. quarter plus sixteenth)

					int deltaTicks = (int) Math.abs(duration - valueDuration*factor);
					if (deltaTicks < minDeltaTicks) {
						minDeltaTicks = deltaTicks;
						snappedDuration = (int) (valueDuration);
						snappedPower = i;
					}
				}
			}
		}
		return new int[] { minDeltaTicks, snappedDuration, snappedPower };
	}
}
