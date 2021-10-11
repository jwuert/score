package org.wuerthner.cwn.score;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.Ignore;
import org.junit.Test;
import org.wuerthner.cwn.api.CwnFactory;
import org.wuerthner.cwn.api.CwnNoteEvent;
import org.wuerthner.cwn.api.CwnTimeSignatureEvent;
import org.wuerthner.cwn.api.CwnTrack;
import org.wuerthner.cwn.api.DurationType;
import org.wuerthner.cwn.api.Metric;
import org.wuerthner.cwn.api.ScoreParameter;
import org.wuerthner.cwn.metric.MetricTools;
import org.wuerthner.cwn.sample.SampleFactory;
import org.wuerthner.cwn.sample.SampleScoreLayout;
import org.wuerthner.cwn.timesignature.SimpleTimeSignature;

public class BarTest {
	public static final int PPQ = 384;
	int D1 = 4 * PPQ;
	int D2 = 2 * PPQ;
	int D4 = PPQ;
	int D8 = (int) (0.5 * PPQ);
	int D8T = (int) (PPQ * 1.0 / 3);
	int D16T = (int) (PPQ * 0.5 / 3);
	
	@Test
	@Ignore
	public void testVoices() {
		CwnFactory factory = new SampleFactory();
		SimpleTimeSignature ts1 = new SimpleTimeSignature("4/4");
		CwnTimeSignatureEvent timeSignatureEvent1 = factory.createTimeSignatureEvent(0, ts1);
		List<CwnTrack> trackList = new ArrayList<>();
		CwnTrack track = factory.createTrack(PPQ);
		track.addEvent(timeSignatureEvent1);
		track.addEvent(factory.createNoteEvent(4 * D4 + D4, D8, 78, 0, 0, 0));
		track.addEvent(factory.createNoteEvent(4 * D4 + D4 + D8, D8, 78, 0, 0, 0));
		track.addEvent(factory.createNoteEvent(4 * D4 + D4, D4, 78, 0, 0, 1));
		trackList.add(track);
		
		ScoreParameter scoreParameter = new ScoreParameter(PPQ, D1 / 32, 1,4, Score.NONE,
				Arrays.asList(new DurationType[] { DurationType.REGULAR, DurationType.DOTTED, DurationType.BIDOTTED, DurationType.TRIPLET, DurationType.QUINTUPLET }),
				false, 0); // 4 bars
		ScoreBuilder scoreBuilder = new ScoreBuilder(new TrackContainer(trackList, 0), scoreParameter, new SampleScoreLayout());
		for (ScoreSystem sys : scoreBuilder) {
			for (ScoreStaff staff : sys) {
				for (ScoreBar bar : staff) {
					System.out.println(bar);
				}
			}
		}
		System.out.println("---");
		int notePosValue = 0;
		int barPosValue = 0;
		int stemDirection = 0;
		boolean isUngrouped = false;
		for (int durValue = 0; durValue < 1536; durValue += 24) {
			ScoreBar bar = new ScoreBar(barPosValue, track, scoreParameter);
			QuantizedPosition position = new QuantizedPosition(bar, notePosValue, bar.getTimeSignature().getMetric());
			QuantizedDuration duration = new QuantizedDuration(scoreParameter, durValue);
			CwnNoteEvent event = factory.createNoteEvent(notePosValue, duration.getSnappedDuration(), 80, 0, 80, 0);
			
			ScoreNote note = new ScoreNote(bar, event, position, duration, stemDirection, isUngrouped);
			System.out.println(durValue + ", note: " + note.duration + "[" + note.getDurationType() + "]" + "   //   " + note.getDurationBase());
		}
	}
	
	@Test
	public void testDurations() {
		// TODO: make scripted test from this!!!
		CwnFactory factory = new SampleFactory();
		SimpleTimeSignature ts1 = new SimpleTimeSignature("4/4");
		CwnTimeSignatureEvent timeSignatureEvent1 = factory.createTimeSignatureEvent(0, ts1);
		List<CwnTrack> trackList = new ArrayList<>();
		CwnTrack track = factory.createTrack(PPQ);
		track.addEvent(timeSignatureEvent1);
		track.addEvent(factory.createKeyEvent(0, 0));
		track.addEvent(factory.createClefEvent(0, 0));
		// track.addEvent(factory.createNoteEvent(D4, D8T + D16T, 78, 0, 0, 0));
		track.addEvent(factory.createNoteEvent(D4 + D8T + D16T, D16T, 78, 0, 0, 0));
		// track.addEvent(factory.createNoteEvent(D4 + 2 * D8T, D8T, 78, 0, 0, 0));
		trackList.add(track);
		
		ScoreParameter scoreParameter = new ScoreParameter(PPQ, D1 / 32, 1,4, Score.SPLIT_RESTS,
				Arrays.asList(new DurationType[] { DurationType.REGULAR, DurationType.DOTTED, DurationType.BIDOTTED, DurationType.TRIPLET, DurationType.QUINTUPLET }),
				false, 0);// 4, Score.SPLIT_RESTS, 0); // 1 bar
		ScoreBuilder scoreBuilder = new ScoreBuilder(new TrackContainer(trackList, 0), scoreParameter, new SampleScoreLayout());
		for (ScoreSystem sys : scoreBuilder) {
			for (ScoreStaff staff : sys) {
				for (ScoreBar bar : staff) {
					// System.out.println(bar);
				}
			}
		}
	}
	
	@Test
	@Ignore
	public void testSubstitution() {
		int PPQ = 384;
		int RESOLUTION = D1 / 16;
		int METRIC_LEVEL = 1;
		int GROUP_LEVEL = 1;
		int STRETCH_FACTOR = 4;
		int flags = 0;
		ScoreParameter scoreParameter = new ScoreParameter(PPQ, RESOLUTION, METRIC_LEVEL, STRETCH_FACTOR, flags,
				Arrays.asList(new DurationType[] { DurationType.REGULAR, DurationType.DOTTED, DurationType.BIDOTTED, DurationType.TRIPLET, DurationType.QUINTUPLET }),
				false, 0);
				// STRETCH_FACTOR, flags, 0);
		Metric metric = MetricTools.createRegularMetrics("4/4+3/8");
		metric = MetricTools.cloneMetrics(metric, 5, DurationType.TRIPLET);
		System.out.println("Metric: " + metric + "; res as ticks: " + scoreParameter.getResolutionInTicks());
		// 1536 | 1278 . 1920 . 2112
		CwnFactory factory = new SampleFactory();
		SimpleTimeSignature ts1 = new SimpleTimeSignature("4/4");
		CwnTrack track = factory.createTrack(PPQ);
		track.addEvent(factory.createTimeSignatureEvent(0, ts1));
		ScoreBar scoreBar = new ScoreBar(0, track, scoreParameter);
		for (int i = 0; i < 2200; i++) {
			QuantizedPosition pos = new QuantizedPosition(scoreBar, i, metric);
			System.out.println(i + "\t" + pos);
		}
	}
}
