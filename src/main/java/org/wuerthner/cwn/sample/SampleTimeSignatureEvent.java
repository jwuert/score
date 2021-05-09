package org.wuerthner.cwn.sample;

import org.wuerthner.cwn.api.CwnEvent;
import org.wuerthner.cwn.api.CwnTimeSignatureEvent;
import org.wuerthner.cwn.api.TimeSignature;

public class SampleTimeSignatureEvent implements CwnTimeSignatureEvent, Comparable<CwnEvent> {
	final private long position;
	final private TimeSignature timeSignature;
	
	public SampleTimeSignatureEvent(long position, TimeSignature timeSignature) {
		this.position = position;
		this.timeSignature = timeSignature;
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
	public TimeSignature getTimeSignature() {
		return timeSignature;
	}
	
	@Override
	public String toString() {
		return "SampleTimeSignatureEvent={position: " + position + ", timeSignature: " + timeSignature + "}";
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
