package org.wuerthner.cwn.sample;

import org.wuerthner.cwn.api.CwnBarEvent;
import org.wuerthner.cwn.api.CwnEvent;

public class SampleBarEvent implements CwnBarEvent, Comparable<CwnEvent> {
	final private long position;
	final private String type;
	
	public SampleBarEvent(long position, String type) {
		this.position = position;
		this.type = type;
	}
	
	@Override
	public long getPosition() {
		return position;
	}
	
	@Override
	public long getDuration() {
		return 0;
	}
	
	@Override
	public String getType() {
		return type;
	}
	
	@Override
	public String toString() {
		return "SimpleBarEvent={position: " + position + ", type: '" + type + "'}";
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
