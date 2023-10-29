package org.wuerthner.cwn.sample;

import org.wuerthner.cwn.api.CwnClefEvent;
import org.wuerthner.cwn.api.CwnEvent;

public class SampleClefEvent implements CwnClefEvent, Comparable<CwnEvent> {
	final private long position;
	final private int clef;
	
	SampleClefEvent(long position, int clef) {
		this.position = position;
		this.clef = clef;
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
	public int getClef() {
		return clef;
	}
	
	@Override
	public String toString() {
		return "SampleClefEvent={position: " + position + ", clef: " + clef + "}";
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
	@Override
	public boolean equals(Object o) {
		if (o instanceof SampleBarEvent) {
			SampleClefEvent that = (SampleClefEvent) o;
			return (this.getPosition()==that.getPosition());
		} else {
			return false;
		}
	}

	@Override
	public int hashCode() {
		return Long.hashCode(this.getPosition());
	}
}
