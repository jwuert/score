package org.wuerthner.cwn.score;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.wuerthner.cwn.api.CwnBarEvent;
import org.wuerthner.cwn.api.CwnEvent;
import org.wuerthner.cwn.api.CwnNoteEvent;
import org.wuerthner.cwn.api.CwnSymbolEvent;
import org.wuerthner.cwn.api.CwnTempoEvent;
import org.wuerthner.cwn.api.CwnTrack;
import org.wuerthner.cwn.api.ScoreParameter;

public class PartitionedTrack implements Iterable<ScoreBar> {
	private final List<ScoreBar> barList = new ArrayList<>();
	private final CwnTrack track;
	private final ScoreParameter scoreParameter;
	private ScoreBar currentBar;
	
	public PartitionedTrack(CwnTrack track, ScoreParameter scoreParameter) {
		this.track = track;
		this.scoreParameter = scoreParameter;
	}

	public void run() {
		partition(barList, scoreParameter.startPosition, scoreParameter.endPosition);
		fill(barList, scoreParameter.endPosition);
	}

	public void update(ScoreUpdate update) {
		long start = update.getStart();
		long end = update.getEnd();
		// System.out.println(" --- " + start + "-" + end + " ---");
		ScoreBar startBar = null;
		for (ScoreBar bar : barList) {
			if (bar.getStartPosition() <= start && start < bar.getEndPosition()) {
				startBar = bar;
				break;
			}
		}
		if (startBar == null) {
			System.err.println("Error, StartBar not found!");
		} else {
			startBar = rewindIfOverlap(startBar);
			start = startBar.getStartPosition();
			List<ScoreBar> newList = new ArrayList<>();
			partition(newList, start, end);
			fill(newList, end);
			List<String> debugBarList = new ArrayList<>();
			int index = barList.indexOf(startBar);
			// Now exchange bars in barList:
			for (int i=0; i<newList.size(); i++) {
				if (barList.size()<=index+i) {
					barList.add(newList.get(i));
					debugBarList.add("+" + barList.size());
				} else {
					barList.set(index + i, newList.get(i));
					debugBarList.add("~" + (index+i));
				}
			}
			System.out.println(" [staff] update bar(s): " + debugBarList);
		}
	}

	private ScoreBar rewindIfOverlap(ScoreBar scoreBar) {
		ScoreBar bar = scoreBar;
		while (bar.startsWithOverlap()) {
			int i = barList.indexOf(bar);
			if (i<0) throw new RuntimeException("PartinionedTrack: bar not found in barList!");
			if (i>0) {
				bar = barList.get(i-1);
			}
		}
		return bar;
	}

	private final void partition(List<ScoreBar> barList, long startPosition, long endPosition) {
		currentBar = new ScoreBar(startPosition, track, scoreParameter);
		boolean stopped = false;
		for (CwnEvent event : track.getList(CwnEvent.class)) {
			if (event.getPosition() >= startPosition) {
				while (event.getPosition() >= currentBar.getEndPosition()) {
					incrementBar(barList);
				}
				long eventStart = event.getPosition();
				
				if (event instanceof CwnNoteEvent) {
					//
					// Note
					//
					CwnNoteEvent noteEvent = (CwnNoteEvent) event;
					long eventEnd = event.getPosition() + event.getDuration();
					long remainingNoteDuration = eventEnd - eventStart;
					long splitDuration = 0;
					
					while (eventEnd > currentBar.getEndPosition()) {
						splitDuration = currentBar.getEndPosition() - eventStart;
						currentBar.addNote(noteEvent, eventStart, splitDuration);
						incrementBar(barList);
						// handle remainder:
						eventStart = track.nextBar(eventStart);
						remainingNoteDuration = remainingNoteDuration - splitDuration;
						// continue
					}
					currentBar.addNote(noteEvent, eventStart, remainingNoteDuration);
				} else if (event instanceof CwnTempoEvent) {
					//
					// Tempo
					//
					currentBar.addTempo((CwnTempoEvent) event);
				} else if (event instanceof CwnBarEvent) {
					//
					// Bar
					//
					currentBar.addBar((CwnBarEvent) event);
				} else if (event instanceof CwnSymbolEvent) {
					//
					// Symbol
					//
					currentBar.addSymbol((CwnSymbolEvent) event);
				}
			}
			if (event.getPosition() > endPosition) {
				stopped = true;
				break;
			}
		}
		if (!stopped) {
			incrementBar(barList);
		}
	}
	
	private void incrementBar(List<ScoreBar> list) {
		currentBar.fillWithRests();
		list.add(currentBar);
		currentBar.group();
		currentBar = new ScoreBar(currentBar.getEndPosition(), track, scoreParameter);
	}
	
	private void fill(List<ScoreBar> list, long endPosition) {
		ScoreBar bar = getLastBar(list);
		while (bar.getEndPosition() < endPosition) {
			bar = new ScoreBar(bar.getEndPosition(), track, scoreParameter);
			bar.fillWithRests();
			bar.group();
			list.add(bar);
			// break; // TODO: decide...
		}
	}
	
	@Override
	public Iterator<ScoreBar> iterator() {
		return barList.iterator();
	}
	
	public List<ScoreBar> getBarList() {
		return barList;
	}
	
	public ScoreBar getLastBar(List<ScoreBar> list) {
		return list.get(list.size() - 1);
	}
}
