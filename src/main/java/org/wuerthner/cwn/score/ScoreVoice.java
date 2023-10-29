package org.wuerthner.cwn.score;

import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Collectors;

import org.wuerthner.cwn.api.CwnNoteEvent;
import org.wuerthner.cwn.api.CwnTrack;
import org.wuerthner.cwn.api.DurationType;
import org.wuerthner.cwn.api.Metric;
import org.wuerthner.cwn.api.ScoreParameter;
import org.wuerthner.cwn.position.PositionTools;

public class ScoreVoice implements Iterable<ScoreObject> {
	private final ScoreBar scoreBar;
	private final Metric metric;
	private final TreeSet<ScoreObject> scoreObjectSet = new TreeSet<>();
	private final TreeSet<ScoreObject> scoreChordSet = new TreeSet<>();
	private final TreeSet<ScoreGroup> scoreGroupSet = new TreeSet<>();
	private final TreeSet<CharacterGroup> characterGroupSet = new TreeSet<>();
	private final int stemDirection; // -1=down, 0=auto, 1=up
	private final int voiceIndex;
	private final ScoreParameter scoreParameter;
	
	public ScoreVoice(ScoreBar scoreBar, int voiceIndex, ScoreParameter scoreParameter) {
		this.scoreBar = scoreBar;
		this.voiceIndex = voiceIndex;
		this.stemDirection = (!scoreBar.isMultiVoiceBar() ? 0 : (voiceIndex & 1) == 0 ? -1 : +1);
		this.metric = scoreBar.getTimeSignature().getMetric().cloneMetric();
		this.scoreParameter = scoreParameter;
		initMetric();
	}
	
	private void initMetric() {
		scoreBar.getTrack().getList(CwnNoteEvent.class).stream().filter(n -> n.getPosition() >= scoreBar.getStartPosition() && n.getPosition() < getEndPosition()).forEach(n -> {
			final List<Metric> metricList = metric.getFlatMetricList();
			QuantizedDuration qdur = new QuantizedDuration(scoreBar.getScoreParameter(), n.getDuration());
			if (qdur.getType() == DurationType.TRIPLET) {
				long relativePositionWithinBar = n.getPosition() - scoreBar.getStartPosition();
				int beat = PositionTools.getBeat(metric, relativePositionWithinBar, scoreBar.getScoreParameter());
				metricList.get(beat).setDurationType(qdur.getType());
			} else if (qdur.getType() == DurationType.QUINTUPLET) {
				long relativePositionWithinBar = n.getPosition() - scoreBar.getStartPosition();
				int beat = PositionTools.getBeat(metric, relativePositionWithinBar, scoreBar.getScoreParameter());
				metricList.get(beat).setDurationType(qdur.getType());
			}
		});
	}
	
	public int getStemDirection() {
		return stemDirection;
	}
	
	public void addNote(CwnNoteEvent cwnNoteEvent, long noteStartPosition, long noteDuration) {
		// System.out.println(" SV.addNote: " + noteStartPosition + " [" + noteDuration + "]");
		QuantizedPosition position = new QuantizedPosition(scoreBar, noteStartPosition, metric);
		QuantizedDuration duration = new QuantizedDuration(scoreBar.getScoreParameter(), noteDuration, position.getType());
		boolean isUngrouped = cwnNoteEvent.isUngrouped();
		if (!scoreObjectSet.isEmpty()) {
			ScoreObject previousObject = scoreObjectSet.last();
			// check duration of previous note:
			long newPosition = position.getSnappedPosition() + scoreBar.getStartPosition();
			// System.out.println("  at pos: " + newPosition);
			if (newPosition > previousObject.getStartPosition() && newPosition < previousObject.getEndPosition()) {
				if (ScoreNote.class.isAssignableFrom(previousObject.getClass())) {
					// todo: configurable: show orig length or adjusted length?
					boolean adjustLength = true;
					if (adjustLength) {
						// Substitute previous note by a copy with adjusted duration!
						// This assures that a note (duration) always ends before the next note begins!
						scoreObjectSet.remove(previousObject);
						ScoreNote newPreviousNote = new ScoreNote((ScoreNote) previousObject, scoreBar, newPosition - previousObject.getStartPosition());
						// System.out.println("  prev: " + previousObject + " ex by " + newPreviousNote);
						add(newPreviousNote);
					}
				} else {
					throw new RuntimeException("Error in ScoreVoice.addNote(): " + newPosition + " < " + previousObject.getEndPosition());
				}
			}
			// check pitch delta to previous note:
			if (newPosition > previousObject.getStartPosition() && ScoreNote.class.isAssignableFrom(previousObject.getClass())) {
				// int previousPitch = ((ScoreNote) previousObject).getPitch();
				// System.out.println("----------------------");
				// Trias t = PositionTools.getTrias(scoreBar.getTrack(), newPosition);
				// System.out.println("pos: " + t.toString());
				// System.out.println("pp: " + previousPitch);
				// System.out.println("cp: " + cwnNoteEvent.getPitch());
				// System.out.println(" " + Math.abs(cwnNoteEvent.getPitch() - previousPitch));
				// if (Math.abs(cwnNoteEvent.getPitch() - previousPitch) > 8) {
				// If two subsequent pitches are too far apart (8) then the notes will not be grouped!
				// isUngrouped = true;
				// }
			}
		}
		ScoreNote scoreNote = new ScoreNote(scoreBar, cwnNoteEvent, position, duration, stemDirection, isUngrouped);
		scoreBar.handleSign(scoreNote);
		// System.out.println("  ADD FINALLY: " + scoreNote);
		add(scoreNote);
		long cutoff = noteDuration - duration.getSnappedDuration();
		if (0.5 * scoreParameter.getResolutionInTicks() < cutoff) {
			//
			// add note, when the length which is cut off in quantization is greater than half resolution
			//
			QuantizedPosition positionCutoff = new QuantizedPosition(scoreBar, scoreNote.getEndPosition(), metric);
			QuantizedDuration durationCutoff = new QuantizedDuration(scoreBar.getScoreParameter(), cutoff, positionCutoff.getType());
			ScoreNote scoreNoteCutoff = new ScoreNote(scoreBar, cwnNoteEvent, positionCutoff, durationCutoff, stemDirection, isUngrouped);
			add(scoreNoteCutoff);
		}
	}
	
	public int getVoiceLocation() {
		return stemDirection;
	}
	
	public long getStartPosition() {
		return scoreBar.getStartPosition();
	}
	
	public long getEndPosition() {
		return scoreBar.getEndPosition();
	}
	
	public long getDuration() {
		return scoreBar.getDuration();
	}
	
	@Override
	public Iterator<ScoreObject> iterator() {
		return scoreChordSet.iterator();
	}
	
	public TreeSet<ScoreObject> getScoreObjectSet() {
		return scoreObjectSet;
	}
	
	public int size() {
		return scoreObjectSet.size();
	}
	
	public void group() {
		//System.out.println("-- GROUP --");
		makeChords();
		//System.out.println("-- " + scoreObjectSet.size() + " + " + scoreChordSet.size());
		groupForBeams();
		groupForDurationCharacter();
	}
	
	private void makeChords() {
		Map<Long, List<ScoreObject>> collection = scoreObjectSet.stream().collect(Collectors.groupingBy(n -> n.getStartPosition()));
		scoreChordSet.clear();
		for (Map.Entry<Long, List<ScoreObject>> entry : collection.entrySet()) {
			List<ScoreObject> scoreObjectList = entry.getValue();
			if (scoreObjectList.size() == 1 && scoreObjectList.get(0).isRest()) {
				scoreChordSet.add(scoreObjectList.get(0));
			} else {
				scoreChordSet.add(new ScoreChord(scoreBar, scoreObjectList));
			}
		}
		// scoreObjectSet.stream().forEach(System.out::println);
	}
	
	/**
	 * @formatter:off
	 * Groups are created on three levels:
	 * 1. On metric group level A, each group ranging over (at least) one beat, including notes/chords and rests
	 * 2. Separating notes/chords connected via beam from rests and notes/chords without beams
	 * 3. On metric group level B, dividing beamed notes/chords, still connected by one beam (but not all)
	 * @formatter:on
	 */
	private void groupForBeams() {
		ScoreGroup group1 = null;
		int groupLevel1 = scoreParameter.metricLevel;
		double factor = metric.duration();
		// List<Double> flatDurationList1a = metric.getCumulativeDurationList(groupLevel1);
		List<Double> flatDurationList1 = metric.getCumulativeDurationList(groupLevel1).stream().map(d -> d * 1.0 / factor).collect(Collectors.toList());
		
		List<Metric> flatMetricList1 = metric.getFlatMetricList(groupLevel1);
		// System.out.println("");
		// System.out.println("====> " + this.getStartPosition());
		// System.out.println(" " + flatMetricList1);
		
		int range1 = 0;
		
		// double recentAveragePitch = 0;
		// System.out.println("--------------------------------------------------------");
		for (ScoreObject scoreObject : this) {
			// System.out.println(" " + scoreObject);
			if (flatDurationList1.contains(scoreObject.getRelativePosition())) {
				range1++;
				group1 = new ScoreGroup(flatDurationList1.get(range1 - 1), flatMetricList1.get(range1 - 1), scoreBar);
				// System.out.println(" * new group " + group1.getRelativeStartPosition() + " *");
				scoreGroupSet.add(group1);
			}
			if (group1 != null) {
				group1.addToGroupLevel1(scoreObject);
			} else {
				System.err.println("*** Error: group is null!");
			}
		}
	}
	
	private boolean hasLargePitchDelta(ScoreObject scoreObject, double recentAveragePitch) {
		if (recentAveragePitch == 0 || !(scoreObject instanceof ScoreChord)) {
			return false;
		} else {
			double averagePitch = ((ScoreChord) scoreObject).getAveragePitch();
			return Math.abs(recentAveragePitch - averagePitch) > 8;
		}
	}
	
	/**
	 * This function creates groups on only one level to provide information about triplets, quintuplets, etc. (The brackets include rests)
	 */
	private void groupForDurationCharacter() {
		List<Metric> flatMetricList = metric.getFlatMetricList();
		double factor = metric.duration();
		double position = 0;
		int character = 0;
		int previousCharacter = 0;
		for (Metric metric : flatMetricList) {
			character = metric.getDurationType().getCharacter();
			if (character > 1) {
				if (character != previousCharacter) {
					makeCharacterGroup(position, metric);
				} else {
					// only add new group if a scoreObject exists at that exact start position!
					final double relativePosition = position * 1.0 / factor;
					boolean scoreObjectExists = scoreObjectSet.stream().anyMatch(so -> so.getRelativePosition() == relativePosition);
					if (scoreObjectExists) {
						makeCharacterGroup(position, metric);
					} else {
						characterGroupSet.last().updateGroup(metric.duration(), scoreObjectSet);
					}
				}
			}
			previousCharacter = character;
			position += metric.duration();
		}
	}
	
	private void makeCharacterGroup(double position, Metric metric) {
		CharacterGroup group = new CharacterGroup(metric, position, scoreObjectSet, scoreBar.getScoreParameter().ppq);
		characterGroupSet.add(group);
	}
	
	public Set<ScoreGroup> getGroups() {
		return scoreGroupSet;
	}
	
	public Set<CharacterGroup> getCharacterGroups() {
		return characterGroupSet;
	}
	
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		String sep = "\n      ";
		for (ScoreObject scoreNote : scoreObjectSet) {
			builder.append(sep);
			builder.append(scoreNote.toString(scoreBar.getTrack()));
		}
		return "ScoreVoice={ voiceIndex=" + voiceIndex + ", " + builder.toString() + "}";
	}
	
	public String toString(CwnTrack cwnTrack) {
		return toString();
	}
	
	public void add(ScoreNote scoreObject) {
		scoreObjectSet.add(scoreObject);
	}
	
	public void fillWithRests() {
		if (scoreObjectSet.isEmpty() && isPresentableDuration(getDuration(), DurationType.REGULAR)) {
			scoreObjectSet.add(new ScoreRest(scoreBar, getStartPosition(), getDuration()));
		} else {
			long currentPosition = getStartPosition();
			for (ScoreObject scoreNote : Collections.unmodifiableSet(new TreeSet<>(scoreObjectSet))) {
				handleDelta(currentPosition, scoreNote.getStartPosition());
				currentPosition = scoreNote.getEndPosition();
			}
			handleDelta(currentPosition, getEndPosition());
		}
	}
	
	private void handleDelta(long from, long to) {
		int delta = (int) (to - from);
		if (delta > 0) {
			long firstBeatPosition = PositionTools.firstBeat(scoreBar.getTrack(), from);
			long currentPosition = firstBeatPosition;
			for (double relativeDuration : metric.getFlatDurationList(scoreBar.getScoreParameter().metricLevel)) {
				int absoluteDuration = (int) (relativeDuration * scoreBar.getTrack().getPPQ() * 4);
				if (currentPosition >= to) {
					break;
				}
				long nextPosition = currentPosition + absoluteDuration;
				// System.out.println("from/to: " + from + "/" + to + ", cur: " + currentPosition + ", np: " + nextPosition + " (abs dur: " + absoluteDuration + ") fbeat: " + firstBeatPosition);
				if (from < nextPosition) {
					long restStartPosition = Math.max(from, currentPosition);
					long restEndPosition = Math.min(to, nextPosition);
					long restDuration = restEndPosition - restStartPosition;
					
					QuantizedPosition qpos = new QuantizedPosition(scoreBar, restStartPosition, metric);
					int deltaPosition = (int) (qpos.getSnappedPosition() + scoreBar.getStartPosition() - restStartPosition);
					restStartPosition += deltaPosition;
					restDuration -= deltaPosition;
					
					if (scoreBar.getScoreParameter().splitRests() || !isPresentableDuration(restDuration, qpos.getType())) {
						long nextBarPosition = PositionTools.nextBar(scoreBar.getTrack(), firstBeatPosition);
						split(firstBeatPosition, nextBarPosition, restStartPosition, restDuration, metric, qpos.getType());
					} else {
						// System.out.println("add rest: " + restStartPosition + " : " + restDuration);
						scoreObjectSet.add(new ScoreRest(scoreBar, restStartPosition, restDuration));
					}
				}
				currentPosition = nextPosition;
			}
		}
	}
	
	private void split(long frameLeftPosition, long frameRightPosition, long restStartPosition, long restDuration, Metric metric, DurationType startingDurationType) {
		// System.out.println("split: " + frameLeftPosition + "-" + frameRightPosition + ", b: " + restStartPosition + " : " + restDuration + " # " + metric);
		int ppq = scoreBar.getTrack().getPPQ();
		if (isPresentableDuration(restDuration, startingDurationType)) {
			// System.out.println("+add rest: " + restStartPosition + " : " + restDuration);
			//
			//
			QuantizedPosition position = new QuantizedPosition(scoreBar, restStartPosition, metric);
			QuantizedDuration duration = new QuantizedDuration(scoreBar.getScoreParameter(), restDuration);
			//
			//
			scoreObjectSet.add(new ScoreRest(scoreBar, position, duration));
			// scoreObjectSet.add(new ScoreRest(scoreBar, restStartPosition, restDuration));
		} else {
			int numberOfEvents = metric.numberOfEvents();
			// int eventDuration = (int) metric.duration();// (int) ((frameRightPosition - frameLeftPosition) * 1.0 / numberOfEvents);
			long frameSplitPosition = frameLeftPosition;
			if (numberOfEvents == 1) {
				// the last event cannot be further split!
				QuantizedPosition position = new QuantizedPosition(scoreBar, restStartPosition, metric);
				QuantizedDuration duration = new QuantizedDuration(scoreBar.getScoreParameter(), restDuration, metric.getDurationType());
				scoreObjectSet.add(new ScoreRest(scoreBar, position, duration));
				
			} else {
				for (int eventNo = 0; eventNo < numberOfEvents; eventNo++) {
					Metric subMetric = metric.get(eventNo);
					DurationType subMetricDurationType = subMetric.getDurationType();
					int eventDuration = (int) (ppq * 4.0 * subMetric.duration());
					frameSplitPosition += eventDuration;
					
					if (restStartPosition + restDuration <= frameSplitPosition) {
						split(frameLeftPosition, frameSplitPosition, restStartPosition, restDuration, subMetric, subMetricDurationType);
						break;
					} else if (restStartPosition >= frameSplitPosition) {
						if (eventNo == numberOfEvents - 1) {
							split(frameSplitPosition, frameRightPosition, restStartPosition, restDuration, subMetric, subMetricDurationType);
						} else {
							// nothing to do, move on to next event
						}
					} else {
						// here the rest starts left of the splitPosition and ends right of the splitPosition!
						long restDurationLeft = frameSplitPosition - restStartPosition;
						long restDurationRight = restStartPosition + restDuration - frameSplitPosition;
						split(frameLeftPosition, frameSplitPosition, restStartPosition, restDurationLeft, subMetric, subMetricDurationType);
						restStartPosition = frameSplitPosition;
						restDuration = restDurationRight;
					}
					frameLeftPosition = frameSplitPosition;
				}
			}
		}
	}
	
	private boolean isPresentableDuration(long duration, DurationType durationType) {
		// int resolutionInTicks = scoreBar.getScoreParameter().getResolutionInTicks();
		int ppq = scoreBar.getScoreParameter().ppq;
		int convertedDuration = (int) (duration * 32 * durationType.getFactor() / ppq);
		// System.out.println(" => " + convertedDuration + " : " + durationType);
		return isPowerOf2(convertedDuration) || (scoreBar.getScoreParameter().allowDottedRests() && isPowerOf2((int) (convertedDuration * 2.0 / 3.0)));
	}
	
	private boolean isPowerOf2(int v) {
		boolean isPowerOf2 = false;
		v = v - ((v >>> 1) & 0x55555555); // reuse input as temporary
		v = (v & 0x33333333) + ((v >>> 2) & 0x33333333); // temp
		int num = ((v + (v >>> 4) & 0xF0F0F0F) * 0x1010101) >>> 24; // count
		isPowerOf2 = (num == 1);
		return isPowerOf2;
	}
}
