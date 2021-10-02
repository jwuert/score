package org.wuerthner.cwn.position;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import org.wuerthner.cwn.api.CwnTimeSignatureEvent;
import org.wuerthner.cwn.api.CwnTrack;
import org.wuerthner.cwn.api.DurationType;
import org.wuerthner.cwn.api.Metric;
import org.wuerthner.cwn.api.ScoreParameter;
import org.wuerthner.cwn.api.Trias;
import org.wuerthner.cwn.api.exception.InvalidPositionException;
import org.wuerthner.cwn.api.exception.TimeSignatureException;

public class PositionTools {
	
	public final static Trias getTrias(CwnTrack track, long position) throws TimeSignatureException {
		Iterator<TimeSignatureNeighbourhood> eventIterator = getNeighbourhoodIterator(track);
		TimeSignatureNeighbourhood timeSignatureNeighbourhood = eventIterator.next();
		int ppq = track.getPPQ();
		int barLength = timeSignatureNeighbourhood.getCurrentBarLength(ppq);
		
		int bar = 0;
		int beat = 0;
		int tick = 0;
		long tickCursor = 0;
		while (true) {
			if (timeSignatureNeighbourhood.hasNext()) {
				if (tickCursor == timeSignatureNeighbourhood.getNextPosition()) {
					timeSignatureNeighbourhood = eventIterator.next();
					barLength = timeSignatureNeighbourhood.getCurrentBarLength(ppq);
				} else if (tickCursor > timeSignatureNeighbourhood.getNextPosition()) {
					throw new TimeSignatureException("Error: Metric change inside Measure!");
				}
			}
			if (tickCursor + barLength > position) {
				int tickOffset = (int) (position - tickCursor);
				int[] beats = timeSignatureNeighbourhood.getBeat(tickOffset, ppq);
				beat = beats[0];
				tick = tickOffset - beats[1];
				break;
			}
			bar++;
			tickCursor += barLength;
		}
		return new Trias(bar, beat, tick);
	}
	
	public final static long getPosition(CwnTrack track, String position) throws TimeSignatureException {
		Trias trias = makeTrias(track, position);
		return getPosition(track, trias);
	}
	
	public final static String ALT_PATTERN = "(^\\s*\\d+\\s*\\.\\s*\\d+\\s*)\\:\\s*\\d+(\\+\\d+)*(T|Q)?\\s*$";
	
	public final static Trias makeTrias(CwnTrack track, String input) {
		// allows notation like: "1.1:8" meaning "1.1" and on eighth, e.g. for PPQ=384: "1.1.8" => "1.1.192"
		Trias trias = null;
		int colonIndex = input.indexOf(":");
		if (colonIndex > 0) {
			String duration = String.valueOf(transformDuration(track, input.substring(colonIndex + 1)));
			input = input.replaceAll(ALT_PATTERN, "$1." + duration);
		}
		if (input.matches(Trias.PATTERN)) {
			trias = new Trias(input);
		} else {
			try {
				throw new InvalidPositionException("Malformed position: " + input);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return trias;
	}
	
	public final static long transformDuration(CwnTrack cwnTrack, String simpleDuration) {
		// simpleDuration "8", "2+4", "8T", "8+16Q"
		int ppq = cwnTrack.getPPQ();
		DurationType durationType = DurationType.REGULAR;
		if (simpleDuration.endsWith("T")) {
			simpleDuration = simpleDuration.replaceAll("T", "");
			durationType = DurationType.TRIPLET;
		} else if (simpleDuration.endsWith("Q")) {
			simpleDuration = simpleDuration.replaceAll("Q", "");
			durationType = DurationType.QUINTUPLET;
		}
		final double factor = durationType.getFactor();
		long duration = Arrays.stream(simpleDuration.split("\\+")).map(s -> s.trim()).mapToLong(Long::valueOf).map(n -> (int) (ppq * 4.0 / (factor * n))).sum();
		return duration;
	}
	
	public final static long getPosition(CwnTrack track, Trias trias) throws TimeSignatureException {
		long thePosition = 0;
		Iterator<TimeSignatureNeighbourhood> eventIterator = getNeighbourhoodIterator(track);
		TimeSignatureNeighbourhood timeSignatureNeighbourhood = eventIterator.next();
		int ppq = track.getPPQ();
		int barLength = timeSignatureNeighbourhood.getCurrentBarLength(ppq);
		
		int bar = 0;
		
		long tickCursor = 0;
		while (true) {
			if (timeSignatureNeighbourhood.hasNext()) {
				if (tickCursor == timeSignatureNeighbourhood.getNextPosition()) {
					timeSignatureNeighbourhood = eventIterator.next();
					barLength = timeSignatureNeighbourhood.getCurrentBarLength(ppq);
				} else if (tickCursor > timeSignatureNeighbourhood.getNextPosition()) {
					throw new TimeSignatureException("Error: Metric change inside Measure!");
				}
			}
			if (trias.bar == bar) {
				int beatTicks = timeSignatureNeighbourhood.getTicks(trias.beat, ppq);
				thePosition = tickCursor + beatTicks + trias.tick;
				break;
			}
			bar++;
			tickCursor += barLength;
		}
		return thePosition;
	}
	
	public final static int getTick(CwnTrack track, long position) {
		Trias trias = getTrias(track, position);
		return trias.tick;
	}
	
	public final static long nextBeat(CwnTrack track, long position) {
		Trias trias = getTrias(track, position);
		return getPosition(track, trias.nextBeat());
	}
	
	public final static long firstBeat(CwnTrack track, long position) {
		Trias trias = getTrias(track, position);
		return getPosition(track, trias.firstBeat());
	}

	public final static long previousBarFirstBeat(CwnTrack track, long position) {
		Trias trias = getTrias(track, position);
		return getPosition(track, new Trias(Math.max(0, trias.bar-1), 0, 0));
	}
	
	public final static long nextBar(CwnTrack track, long position) {
		Trias trias = getTrias(track, position);
		return getPosition(track, trias.nextBar());
	}
	
	public final static int getBeat(Metric metric, long relativePosition, ScoreParameter scoreParameter) {
		final List<Metric> metricList = metric.getFlatMetricList();
		long currentPosition = 0;
		int beat = 0;
		for (Metric subMetric : metricList) {
			long nextPosition = currentPosition + (long) (subMetric.duration() * 4 * scoreParameter.ppq);
			if (relativePosition >= currentPosition && relativePosition < nextPosition) {
				break;
			}
			currentPosition = nextPosition;
			beat++;
		}
		return beat;
	}
	
	private static Iterator<TimeSignatureNeighbourhood> getNeighbourhoodIterator(CwnTrack track) {
		List<TimeSignatureNeighbourhood> neighbourhoodList = new ArrayList<>();
		CwnTimeSignatureEvent previousEvent = null;
		for (CwnTimeSignatureEvent event : track.getList(CwnTimeSignatureEvent.class)) {
			if (previousEvent != null) {
				TimeSignatureNeighbourhood neighbourhood = new TimeSignatureNeighbourhood(previousEvent, event);
				neighbourhoodList.add(neighbourhood);
			}
			previousEvent = event;
		}
		neighbourhoodList.add(new TimeSignatureNeighbourhood(previousEvent, previousEvent));
		return neighbourhoodList.iterator();
	}
}
