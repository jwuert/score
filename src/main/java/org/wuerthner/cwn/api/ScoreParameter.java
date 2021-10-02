package org.wuerthner.cwn.api;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.wuerthner.cwn.score.Score;

public class ScoreParameter {
	public final long startPosition = 0;
	public final long endPosition;
	public final int resolutionInTicks;
	public final int ppq;
	public final int flags;
	public final int metricLevel;
	public final int stretchFactor;
	public final List<DurationType> durationTypeList;
	public boolean markup;
	public int barOffset;
	
	public ScoreParameter(int ppq, int resolutionInTicks, int metricLevel, int stretchFactor, int flags, int barOffset) {
		this(ppq, resolutionInTicks, metricLevel, stretchFactor, flags,
				Arrays.asList(new DurationType[] { DurationType.REGULAR, DurationType.DOTTED, DurationType.BIDOTTED, DurationType.TRIPLET, DurationType.QUINTUPLET }),
				false, barOffset);
		// Arrays.asList(new DurationType[] { DurationType.REGULAR, DurationType.DOTTED }));
	}
	
	public ScoreParameter(int ppq, int resolutionInTicks, int metricLevel, int stretchFactor, int flags, List<DurationType> durationTypeList,
						  boolean markup, int barOffset) {
		// this.startPosition = startPosition;
		this.endPosition = ppq * 500 * 5; // 500 4/4 bars
		this.ppq = ppq;
		this.resolutionInTicks = resolutionInTicks;
		this.metricLevel = metricLevel;
		this.stretchFactor = stretchFactor;
		this.flags = flags;
		this.durationTypeList = durationTypeList;
		this.markup = markup;
		this.barOffset = barOffset;
	}
	
	public int getResolutionInTicks() {
		return resolutionInTicks;
	}
	
	public boolean allowDottedRests() {
		return (flags & Score.ALLOW_DOTTED_RESTS) != 0;
	}
	
	public boolean splitRests() {
		return (flags & Score.SPLIT_RESTS) != 0;
	}
	
	public List<DurationType> getSupportedDurationTypes() {
		return Collections.unmodifiableList(durationTypeList);
	}
	
	public int getDisplayStretchFactor() {
		return stretchFactor;
	}

	public int getBarOffset() { return barOffset; }

	public void setBarOffset(int barOffset) { this.barOffset = barOffset; }
}
