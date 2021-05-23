package org.wuerthner.cwn.score;

import java.util.List;

import org.wuerthner.cwn.api.DurationType;
import org.wuerthner.cwn.api.Metric;
import org.wuerthner.cwn.api.ScoreParameter;

public class QuantizedPosition {
	
	private final MetricGeometry metricGeometry;
	private final long snappedPosition;
	private final int resolutionInTicks;
	
	public QuantizedPosition(ScoreBar scoreBar, long noteStartPosition, final Metric barMetric) {
		resolutionInTicks = scoreBar.getScoreParameter().getResolutionInTicks();
		long positionWithinBar = noteStartPosition - scoreBar.getStartPosition();
		metricGeometry = getDurationType(scoreBar.getScoreParameter(), positionWithinBar, barMetric);
		snappedPosition = metricGeometry.beatStartPosition + getSnappedPosition(metricGeometry.durationType, scoreBar.getScoreParameter(), positionWithinBar - metricGeometry.beatStartPosition);
	}
	
	public QuantizedPosition(ScoreBar scoreBar, long noteStartPosition, int resolutionInTicks) {
		this.resolutionInTicks = resolutionInTicks;
		long positionWithinBar = noteStartPosition - scoreBar.getStartPosition();
		if (positionWithinBar<0) {
			positionWithinBar = 0;
		}
		metricGeometry = getDurationType(scoreBar.getScoreParameter(), positionWithinBar, scoreBar.getTimeSignature().getMetric());
		snappedPosition = metricGeometry.beatStartPosition + getSnappedPosition(metricGeometry.durationType, scoreBar.getScoreParameter(), positionWithinBar - metricGeometry.beatStartPosition);
	}
	
	private MetricGeometry getDurationType(ScoreParameter scoreParameter, long relativePosition, final Metric metric) {
		DurationType durationType = DurationType.REGULAR;
		List<Metric> metricList = metric.getFlatMetricList();
		long currentPosition = 0;
		int beat = 0;
		for (Metric subMetric : metricList) {
			long nextPosition = currentPosition + (long) (subMetric.duration() * 4 * scoreParameter.ppq);
			if (relativePosition >= currentPosition && relativePosition < nextPosition) {
				durationType = subMetric.getDurationType();
				break;
			}
			currentPosition = nextPosition;
			beat++;
		}
		return new MetricGeometry(currentPosition, beat, durationType);
	}
	
	public DurationType getType() {
		return metricGeometry.durationType;
	}
	
	public long getSnappedPosition() {
		return snappedPosition;
	}
	
	@Override
	public String toString() {
		return "position: " + snappedPosition + ", type: " + metricGeometry.durationType + ", in beat: " + metricGeometry.beat;
	}
	
	private long getSnappedPosition(DurationType type, ScoreParameter scoreParameter, long relativePositionInBeat) {
		// System.out.println(" snap: " + resolutionInTicks + " (" + type.getFactor() + ")");
		int resInTicks = (int) (resolutionInTicks / type.getFactor());
		
		long snappedPosition = relativePositionInBeat;
		
		snappedPosition += (long) (resInTicks * 0.49);
		snappedPosition = snappedPosition - (snappedPosition % resInTicks);
		return snappedPosition;
	}
	
	private class MetricGeometry {
		public final long beatStartPosition;
		public final int beat;
		public final DurationType durationType;
		
		public MetricGeometry(long beatStartPosition, int beat, DurationType durationType) {
			this.beatStartPosition = beatStartPosition;
			this.beat = beat;
			this.durationType = durationType;
		}
	}
}
