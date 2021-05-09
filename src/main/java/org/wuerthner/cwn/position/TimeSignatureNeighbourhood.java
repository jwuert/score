package org.wuerthner.cwn.position;

import java.util.List;

import org.wuerthner.cwn.api.CwnTimeSignatureEvent;
import org.wuerthner.cwn.api.Metric;

public class TimeSignatureNeighbourhood {
	private final CwnTimeSignatureEvent event1;
	private final CwnTimeSignatureEvent event2;
	
	public TimeSignatureNeighbourhood(CwnTimeSignatureEvent tsEvent1, CwnTimeSignatureEvent tsEvent2) {
		event1 = tsEvent1;
		event2 = tsEvent2;
	}
	
	public boolean hasNext() {
		return event1 != event2;
	}
	
	public long getCurrentPosition() {
		return event1.getPosition();
	}
	
	public long getNextPosition() {
		return event2.getPosition();
	}
	
	public int getCurrentBarLength(int ppq) {
		if (event1 == null) {
			return 1;
		}
		return (int) (event1.getTimeSignature().getMetric().duration() * 4.0 * ppq);
		
	}
	
	public int[] getBeat(int tickOffset, int ppq) {
		int beat = 0;
		if (event1 == null) {
			return new int[] { 1, 1 };
		}
		Metric metric = event1.getTimeSignature().getMetric();
		List<Double> durationList = metric.getFlatDurationList();
		int tickCount = 0;
		for (double relativeDuration : durationList) {
			int absoluteDuration = (int) (relativeDuration * 4.0 * ppq);
			if (tickCount + absoluteDuration > tickOffset) {
				break;
			}
			beat++;
			tickCount += absoluteDuration;
		}
		return new int[] { beat, tickCount };
	}
	
	public int getTicks(int beat, int ppq) {
		if (event1 == null) {
			return 1;
		}
		Metric metric = event1.getTimeSignature().getMetric();
		List<Double> durationList = metric.getFlatDurationList();
		int tickCount = 0;
		for (double relativeDuration : durationList) {
			int absoluteDuration = (int) (relativeDuration * 4.0 * ppq);
			if (beat-- <= 0) {
				break;
			}
			tickCount += absoluteDuration;
		}
		return tickCount;
	}
}
