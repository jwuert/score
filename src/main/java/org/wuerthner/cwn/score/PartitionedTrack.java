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
		partition(scoreParameter);
		fill();
	}
	
	private final void partition(ScoreParameter scoreParameter) {
		currentBar = new ScoreBar(scoreParameter.startPosition, track, scoreParameter);
		// for (CwnNoteEvent noteEvent : track.getList(CwnNoteEvent.class)) {
		for (CwnEvent event : track.getList(CwnEvent.class)) {
			
			if (event.getPosition() >= scoreParameter.startPosition) {
				while (event.getPosition() >= currentBar.getEndPosition()) {
					incrementBar();
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
						incrementBar();
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
			if (event.getPosition() > scoreParameter.endPosition) {
				break;
			}
		}
		incrementBar();
	}
	
	private void incrementBar() {
		currentBar.fillWithRests();
		barList.add(currentBar);
		currentBar.group();
		currentBar = new ScoreBar(currentBar.getEndPosition(), track, scoreParameter);
	}
	
	private void fill() {
		ScoreBar bar = getLastBar();
		
		while (bar.getEndPosition() < scoreParameter.endPosition) {
			bar = new ScoreBar(bar.getEndPosition(), track, scoreParameter);
			bar.fillWithRests();
			barList.add(bar);
		}
		
	}
	
	@Override
	public Iterator<ScoreBar> iterator() {
		return barList.iterator();
	}
	
	public List<ScoreBar> getBarList() {
		return barList;
	}
	
	public ScoreBar getLastBar() {
		return barList.get(barList.size() - 1);
	}
}
