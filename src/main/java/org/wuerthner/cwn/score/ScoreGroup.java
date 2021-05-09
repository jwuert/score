package org.wuerthner.cwn.score;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.TreeSet;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.wuerthner.cwn.api.Metric;

/*
 *     _________#_________ _________#_________
 *    |----|----|----|----|----|----|----|----|
 *    |    |    |    |-3- |         |---3---  |
 *    |e SS|SSSs|q   |EEe |E Q    SS|Q  Q  Q  |
 * 
 * G1: --------- --------- --------- --------- separate on 1st level metric
 * G2: - ----- - ---  -- - - ---- -  -- -- --  separate on groupable - to draw 1st beam for groupable
 * G3: - -- -- - ---  -- - - ---- -  -- -- --  separate on 2nd level metric - to draw other beams
 * GD:                ----           --------- Duration != REGULAR
 */
public class ScoreGroup implements Comparable<ScoreGroup> {
	private final double relativeStart;
	private final double masterGroupStart;
	private final Metric metric;
	private final ScoreBar scoreBar;
	private final TreeSet<ScoreObject> scoreObjectSet = new TreeSet<>();
	private final List<ScoreGroup> scoreGroupList = new ArrayList<>();
	private final List<Double> cumulativeDurationList;
	private final int depth;
	
	private ScoreGroup currentGroup = null;
	
	public ScoreGroup(double relativeStart, double masterGroupStart, Metric metric, ScoreBar scoreBar, int depth) {
		this.relativeStart = relativeStart;
		this.masterGroupStart = masterGroupStart;
		this.metric = metric;
		this.scoreBar = scoreBar;
		this.depth = depth;
		
		int groupLevel2 = 2;
		// the division of each duration by the factor is due to the fact the the duration-list does not necessarily add up to 1, e.g. for 5/4, this is 5*0.25 = 1.25,
		// while the relative positions within 0 and 1 are needed!
		double factor = scoreBar.getTimeSignature().getMetric().duration();
		cumulativeDurationList = metric.getCumulativeDurationList(groupLevel2).stream().map(d -> d * 1.0 / factor).collect(Collectors.toList());
		// cumulativeDurationList = metric.getCumulativeDurationList(groupLevel2);
	}
	
	public ScoreGroup(double relativeStart, Metric metric, ScoreBar scoreBar) {
		this(relativeStart, relativeStart, metric, scoreBar, 0);
	}
	
	public List<ScoreGroup> getSubGroups() {
		return scoreGroupList;
	}
	
	public double getRelativeStartPosition() {
		return relativeStart;
	}
	
	// only for debugging level
	public double getRelativeDuration() {
		if (depth == 2) {
			ScoreObject first = scoreObjectSet.first();
			ScoreObject last = scoreObjectSet.last();
			return last.getRelativePosition() + last.getRelativeDuration() - first.getRelativePosition();
		} else if (depth == 1) {
			double dur = 0;
			for (ScoreGroup group : scoreGroupList) {
				ScoreObject first = group.getObjectSet().first();
				ScoreObject last = group.getObjectSet().last();
				dur += last.getRelativePosition() + last.getRelativeDuration() - first.getRelativePosition();
			}
			return dur;
		} else {
			double dur = 0;
			for (ScoreGroup group : scoreGroupList) {
				dur += group.getRelativeDuration();
			}
			return dur;
		}
	}
	
	public void addToGroupLevel1(ScoreObject scoreObject) {
		double relativeObjectStart = scoreObject.getRelativePosition();
		if (scoreObject.groupable()) {
			if (currentGroup == null) {
				currentGroup = new ScoreGroup(relativeObjectStart, masterGroupStart, metric, scoreBar, depth + 1);
				scoreGroupList.add(currentGroup);
			}
			currentGroup.addToGroupLevel2(scoreObject);
		} else {
			currentGroup = new ScoreGroup(relativeObjectStart, masterGroupStart, metric, scoreBar, depth + 1);
			scoreGroupList.add(currentGroup);
			currentGroup.addToGroupLevel2(scoreObject);
			currentGroup = null;
		}
	}
	
	public void addToGroupLevel2(ScoreObject scoreObject) {
		double relativeObjectStart = scoreObject.getRelativePosition();
		// System.out.println("g: " + relativeObjectStart + "-" + masterGroupStart + ":" + (relativeObjectStart - masterGroupStart) + " : " + cumulativeDurationList);
		if (isOnBeat(relativeObjectStart - masterGroupStart)) {
			currentGroup = new ScoreGroup(relativeObjectStart, masterGroupStart, metric, scoreBar, depth + 1);
			scoreGroupList.add(currentGroup);
		}
		if (currentGroup == null) {
			currentGroup = new ScoreGroup(relativeObjectStart, masterGroupStart, metric, scoreBar, depth + 1);
			scoreGroupList.add(currentGroup);
		}
		currentGroup.add(scoreObject);
	}
	
	private boolean isOnBeat(double value) {
		boolean found = false;
		for (double beatValue : cumulativeDurationList) {
			if (Math.abs(beatValue - value) < 0.001) {
				found = true;
				break;
			}
		}
		return found;
	}
	
	public void add(ScoreObject scoreObject) {
		scoreObjectSet.add(scoreObject);
	}
	
	public TreeSet<ScoreObject> getObjectSet() {
		if (depth == 2) {
			return scoreObjectSet;
		} else {
			TreeSet<ScoreObject> totalObjectSet = new TreeSet<>();
			for (ScoreGroup subGroup : scoreGroupList) {
				totalObjectSet.addAll(subGroup.getObjectSet());
			}
			return totalObjectSet;
		}
	}
	
	public ScoreNote getMinimumNote() {
		Stream<ScoreObject> stream = getObjectSet().stream().filter(so -> !so.isRest());
		Optional<ScoreObject> minimumNote = stream.collect(Collectors.minBy((o1, o2) -> o1.getMinimumNote().getPitch() - o2.getMinimumNote().getPitch()));
		return (ScoreNote) minimumNote.orElse(null).getMinimumNote();
	}
	
	public ScoreNote getMaximumNote() {
		Stream<ScoreObject> stream = getObjectSet().stream().filter(so -> !so.isRest());
		Optional<ScoreObject> maximumNote = stream.collect(Collectors.maxBy((o1, o2) -> o1.getMinimumNote().getPitch() - o2.getMinimumNote().getPitch()));
		return (ScoreNote) maximumNote.orElse(null).getMaximumNote();
	}
	
	public int getStemDirection() {
		ScoreObject first = getObjectSet().first();
		int stemDirection = 0;
		if (first != null) {
			if (first.fixStemDirection() != 0) {
				stemDirection = first.fixStemDirection();
			} else {
				Stream<ScoreObject> stream = getObjectSet().stream().filter(so -> !so.isRest());
				double averagePitch = stream.mapToDouble(so -> so.getAveragePitch()).average().orElse(0.0);
				int y = first.getY((int) (averagePitch + 0.49), 0, scoreBar.getClef());
				int yDistance = y - Score.Y_CENTER;
				stemDirection = yDistance > 0 ? 1 : -1;
			}
		} else {
			throw new RuntimeException("Empty Chord!");
		}
		return stemDirection;
	}
	
	public ScoreObject first() {
		return first(depth);
	}
	
	public ScoreObject last() {
		return last(depth);
	}
	
	private ScoreObject first(int depth) {
		if (depth == 2) {
			return scoreObjectSet.first();
		} else {
			return scoreGroupList.get(0).first();
		}
	}
	
	private ScoreObject last(int depth) {
		if (depth == 2) {
			return scoreObjectSet.last();
		} else {
			return scoreGroupList.get(scoreGroupList.size() - 1).last();
		}
	}
	
	public String toString() {
		String sep = System.getProperty("line.separator");
		StringBuilder builder = new StringBuilder();
		if (depth == 0) {
			builder.append("Group - 1 {");
			builder.append("start=" + relativeStart + ", ");
			builder.append("metric=" + metric.toString());
			builder.append(sep);
			for (ScoreGroup group : scoreGroupList) {
				builder.append(group.toString());
			}
			builder.append("}");
		} else if (depth == 1) {
			builder.append("  Group - 2 (first beam){");
			builder.append("start=" + relativeStart + ", ");
			builder.append(sep);
			for (ScoreGroup group : scoreGroupList) {
				builder.append(group.toString());
			}
			builder.append("  }");
			builder.append(sep);
		} else if (depth == 2) {
			builder.append("    Group - 3 (higher beams) {");
			builder.append("start=" + relativeStart + ", ");
			builder.append(sep);
			for (ScoreObject scoreObject : scoreObjectSet) {
				builder.append("      SO: " + scoreObject.toString(scoreBar.getTrack()));
				builder.append(sep);
			}
			builder.append("    }");
			builder.append(sep);
		}
		return builder.toString();
	}
	
	@Override
	public int compareTo(ScoreGroup scoreGroup) {
		int ppq = scoreBar.getTrack().getPPQ();
		return (int) (ppq * (this.relativeStart - scoreGroup.relativeStart));
	}
	
	public int getCharacter() {
		return metric.getDurationType().getCharacter();
	}
	
	public String getCharacterPresentation() {
		return metric.getDurationType().getPresentation();
	}
	
	public int size() {
		if (depth == 2) {
			return scoreObjectSet.size();
		} else {
			return scoreGroupList.stream().mapToInt(g -> g.size()).sum();
		}
	}
}
