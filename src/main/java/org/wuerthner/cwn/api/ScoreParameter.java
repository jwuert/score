package org.wuerthner.cwn.api;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.wuerthner.cwn.score.Score;

public class ScoreParameter {
	public final long startPosition = 0;
	public final long endPosition;
	public int resolutionInTicks;
	public int ppq;
	public final int flags;
	public int metricLevel;
	public int stretchFactor;
	public final List<DurationType> durationTypeList;
	public boolean markup;
	public int barOffset;
	public String filename;
	
	private ScoreParameter(int ppq, int resolutionInTicks, int metricLevel, int stretchFactor, int flags, int barOffset) {
		this(ppq, resolutionInTicks, metricLevel, stretchFactor, flags,
				Arrays.asList(new DurationType[] { DurationType.REGULAR, DurationType.DOTTED, DurationType.BIDOTTED, DurationType.TRIPLET, DurationType.QUINTUPLET }),
				false, barOffset);
		// Arrays.asList(new DurationType[] { DurationType.REGULAR, DurationType.DOTTED }));
	}
	
	public ScoreParameter(int ppq, int resolutionInTicks, int metricLevel, int stretchFactor, int flags, List<DurationType> durationTypeList,
						  boolean markup, int barOffset) {
		// this.startPosition = startPosition;
		this.endPosition = ppq * 500 * 4; // 500 4/4 bars
		this.ppq = ppq;
		this.resolutionInTicks = resolutionInTicks;
		this.metricLevel = metricLevel;
		this.stretchFactor = stretchFactor;
		this.flags = flags;
		this.durationTypeList = durationTypeList;
		this.markup = markup;
		this.barOffset = barOffset;
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

	public void setDisplayStretchFactor(int stretchFactor) { this.stretchFactor = stretchFactor; }

	public int getMetricLevel() { return metricLevel; }

	public void setMetricLevel(int metricLevel) { this.metricLevel = metricLevel; }

	public int getResolutionInTicks() { return resolutionInTicks; }

	public void setResolutionInTicks(int resolutionInTicks) { this.resolutionInTicks = resolutionInTicks; }

	public int getBarOffset() { return barOffset; }

	public void setBarOffset(int barOffset) { this.barOffset = barOffset; }

	public int getPPQ() { return ppq; }

	public void setPPQ(int ppq) { this.ppq = ppq; }

	public String getFilename() { return filename; }

	public void setFilename(String filename) { this.filename = filename; }

	public void setTuplet(boolean t2, boolean t3, boolean t4, boolean t5, boolean t6) {
		if (t2 && !durationTypeList.contains(DurationType.DUPLET)) {
			durationTypeList.add(DurationType.DUPLET);
		} else if (!t2 && durationTypeList.contains(DurationType.DUPLET)) {
			durationTypeList.remove(DurationType.DUPLET);
		}
		if (t3 && !durationTypeList.contains(DurationType.TRIPLET)) {
			durationTypeList.add(DurationType.TRIPLET);
		} else if (!t3 && durationTypeList.contains(DurationType.TRIPLET)) {
			durationTypeList.remove(DurationType.TRIPLET);
		}
		if (t4 && !durationTypeList.contains(DurationType.QUADRUPLET)) {
			durationTypeList.add(DurationType.QUADRUPLET);
		} else if (!t4 && durationTypeList.contains(DurationType.QUADRUPLET)) {
			durationTypeList.remove(DurationType.QUADRUPLET);
		}
		if (t5 && !durationTypeList.contains(DurationType.QUINTUPLET)) {
			durationTypeList.add(DurationType.QUINTUPLET);
		} else if (!t5 && durationTypeList.contains(DurationType.QUINTUPLET)) {
			durationTypeList.remove(DurationType.QUINTUPLET);
		}
		if (t6 && !durationTypeList.contains(DurationType.SEXTUPLET)) {
			durationTypeList.add(DurationType.SEXTUPLET);
		} else if (!t6 && durationTypeList.contains(DurationType.SEXTUPLET)) {
			durationTypeList.remove(DurationType.SEXTUPLET);
		}
//		if (t7 && !durationTypeList.contains(DurationType.SEPTUPLET)) {
//			durationTypeList.add(DurationType.SEPTUPLET);
//		} else if (!t7 && durationTypeList.contains(DurationType.SEPTUPLET)) {
//			durationTypeList.remove(DurationType.SEPTUPLET);
//		}
	}
}
