package org.wuerthner.cwn.score;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;

import org.wuerthner.cwn.api.CwnBarEvent;
import org.wuerthner.cwn.api.CwnEvent;
import org.wuerthner.cwn.api.CwnNoteEvent;
import org.wuerthner.cwn.api.CwnSymbolEvent;
import org.wuerthner.cwn.api.CwnTempoEvent;
import org.wuerthner.cwn.api.CwnTrack;
import org.wuerthner.cwn.api.ScoreParameter;
import org.wuerthner.cwn.position.PositionTools;

public class PartitionedTrack implements Iterable<ScoreBar> {
	private final static int FOLLOWING_BARS = 4;
	private final List<ScoreBar> barList = new ArrayList<>();
	private final CwnTrack track;
	private final ScoreParameter scoreParameter;
	private ScoreBar currentBar;
	private ScoreBar previousBar;

	boolean oldVersion = false;
	
	public PartitionedTrack(CwnTrack track, ScoreParameter scoreParameter) {
		this.track = track;
		this.scoreParameter = scoreParameter;
	}

	private long getLastPosition() {
		CwnEvent lastEvent = track.getList(CwnEvent.class).get(track.getList(CwnEvent.class).size() - 1);
		long pos = lastEvent.getPosition() + lastEvent.getDuration();
		return PositionTools.nextBars(track, pos, 10);
	}

	public void run() {
		partition(barList, scoreParameter.startPosition, getLastPosition()); // scoreParameter.endPosition);
		fill(barList, getLastPosition()); // scoreParameter.endPosition);
	}

	public void update(ScoreUpdate update) {
		long start = update.getStart();
		long end = getLastPosition(); // update.getEnd();
		end = PositionTools.nextBars(track, end, FOLLOWING_BARS);
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
		}
	}

	private ScoreBar rewindIfOverlap(ScoreBar scoreBar) {
		ScoreBar bar = scoreBar;
		int count = 0;
		while (bar.startsWithOverlap() && count++<20) {
			int i = barList.indexOf(bar);
			if (i < 0) throw new RuntimeException("PartinionedTrack: bar not found in barList!");
			if (i > 0) {
				bar = barList.get(i - 1);
			}
		}
		if (count==20) {
			System.err.println("rewind bar problem: " + bar.getStartPosition());
		}
		return bar;
	}

	/***
	 * This method locates and returns the bar within <tt>barList</tt> which contains the position <tt>pos</tt>
	 * @param barList
	 * @param pos
	 * @return bar
	 */
	private final ScoreBar findBar(List<ScoreBar> barList, long pos) {
		ScoreBar theBar = null;
		if (currentBar != null && currentBar.getStartPosition() <= pos && pos < currentBar.getEndPosition()) {
			theBar = currentBar;
		} else {
			int currentIndex = barList.indexOf(currentBar);
			if (barList.size() > currentIndex + 1) {
				ScoreBar nextBar = barList.get(currentIndex+1);
				if (nextBar.getStartPosition() <= pos && pos < nextBar.getEndPosition()) {
					theBar = nextBar;
				}
			}
		}
		if (theBar == null) {
			theBar = barList.stream().filter(b -> pos < b.getEndPosition()).findFirst().orElse(null);
		}
		return theBar;
	}

	private final void partition(List<ScoreBar> barList, long startPosition, long endPosition) {
		if (oldVersion) { partition_old(barList, startPosition, endPosition); return; }
		createBars(barList, startPosition, endPosition);
		if (!barList.isEmpty()) {
			currentBar = barList.get(0);
			boolean stopped = false;
			for (CwnEvent event : track.getList(CwnEvent.class)) {
				currentBar = findBar(barList, event.getPosition());
				if (currentBar == null) continue;
				if (event.getPosition() >= startPosition) {

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
							// in case note exceeds the bar end, split and add remainder to next bar
							splitDuration = currentBar.getEndPosition() - eventStart;
							currentBar.addNote(noteEvent, eventStart, splitDuration);
							if (barList.size() <= barList.indexOf(currentBar) + 1) {
								// if note exceeds the FOLLOWING_BARS number of bars, ignore (refresh UI will be necessary)
								break;
							}
							currentBar = barList.get(barList.indexOf(currentBar) + 1);
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
		}
		fillBars(barList, endPosition);
	}

	private final void partition_old(List<ScoreBar> barList, long startPosition, long endPosition) {
		currentBar = new ScoreBar(startPosition, track, scoreParameter);
		previousBar = null;
		boolean stopped = false;
		for (CwnEvent event : track.getList(CwnEvent.class)) {
			if (event.getPosition() >= startPosition) {
				while (event.getPosition() >= currentBar.getEndPosition()) {
					incrementBar_old(barList);
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
						// in case note exceeds the bar end, split and add remainder to next bar
						splitDuration = currentBar.getEndPosition() - eventStart;
						currentBar.addNote(noteEvent, eventStart, splitDuration);
						incrementBar_old(barList);
						// handle remainder:
						eventStart = track.nextBar(eventStart);
						remainingNoteDuration = remainingNoteDuration - splitDuration;
						// continue
					}
					if (eventStart < currentBar.getStartPosition() && previousBar != null && noteEvent instanceof CwnNoteEvent) {
						previousBar.addNote(noteEvent, eventStart, remainingNoteDuration);
						previousBar.group();
					} else {
						currentBar.addNote(noteEvent, eventStart, remainingNoteDuration);
					}
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
			incrementBar_old(barList);
		}
	}
	
	private void incrementBar_old(List<ScoreBar> list) {
		currentBar.fillWithRests();
		list.add(currentBar);
		currentBar.group();
		previousBar = currentBar;
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

	private void createBars(List<ScoreBar> list, long startPosition, long endPosition) {
		long pos = startPosition;
		while (pos < endPosition) {
			ScoreBar bar = new ScoreBar(pos, track, scoreParameter);
			pos = bar.getEndPosition();
			list.add(bar);
		}
	}

	private void fillBars(List<ScoreBar> list, long endPosition) {
		for (ScoreBar bar : list) {
			bar.fillWithRests();
			bar.group();
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
