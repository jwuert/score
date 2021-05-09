package org.wuerthner.cwn.api;

public interface CwnEvent {
	public static final Integer DEFAULT_PULSE_PER_QUARTER = 960; // 384;
	
	long getPosition();
	
	long getDuration();
	
}
