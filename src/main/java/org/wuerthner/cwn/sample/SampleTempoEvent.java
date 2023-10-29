package org.wuerthner.cwn.sample;

import org.wuerthner.cwn.api.CwnEvent;
import org.wuerthner.cwn.api.CwnKeyEvent;
import org.wuerthner.cwn.api.CwnTempoEvent;

public class SampleTempoEvent implements CwnTempoEvent, Comparable<CwnEvent> {
	final private long position;
	final private int tempo;
	final private String label = "myTempo";

	SampleTempoEvent(long position, int tempo) {
		this.position = position;
		this.tempo = tempo;
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
	public String getLabel() {
		return label;
	}

	@Override
	public int getTempo() {
		return tempo;
	}
	
	@Override
	public String toString() {
		return "SampleKeyEvent={position: " + position + ", key: " + tempo + "}";
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
		if (o instanceof SampleTempoEvent) {
			SampleTempoEvent that = (SampleTempoEvent) o;
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
