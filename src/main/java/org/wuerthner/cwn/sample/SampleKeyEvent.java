package org.wuerthner.cwn.sample;

import org.wuerthner.cwn.api.CwnEvent;
import org.wuerthner.cwn.api.CwnKeyEvent;

public class SampleKeyEvent implements CwnKeyEvent, Comparable<CwnEvent> {
	final private long position;
	final private int key;
	
	SampleKeyEvent(long position, int key) {
		this.position = position;
		this.key = key;
	}
	
	@Override
	public long getPosition() {
		return position;
	}
	
	@Override
	public long getDuration() {
		return 0L;
	}
	
	@Override
	public int getKey() {
		return key;
	}
	
	@Override
	public String toString() {
		return "SampleKeyEvent={position: " + position + ", key: " + key + "}";
	}
	
	@Override
	public int compareTo(CwnEvent event) {
		long thisPosition = getPosition();
		long thatPosition = event.getPosition();
		if (thisPosition != thatPosition) {
			return (int) (thisPosition - thatPosition);
		}
		return 1;
	}
}
