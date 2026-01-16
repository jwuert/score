package org.wuerthner.cwn.score;

import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import org.junit.Ignore;
import org.junit.Test;
import org.wuerthner.cwn.api.*;
import org.wuerthner.cwn.position.PositionTools;
import org.wuerthner.cwn.sample.SampleFactory;
import org.wuerthner.cwn.sample.SampleScoreLayout;
import org.wuerthner.cwn.timesignature.SimpleTimeSignature;

public class RelativePositionAndDurationTest {
	private static final int PPQ = 384;
	private static final int D4 = 4 * PPQ;
	
	@Test
	@Ignore
	public void testRelativePositionWithinBar() {
		
		CwnFactory factory = new SampleFactory();
		SimpleTimeSignature ts1 = new SimpleTimeSignature("5/4");
		CwnTimeSignatureEvent timeSignatureEvent1 = factory.createTimeSignatureEvent(0, ts1);
		List<CwnTrack> trackList = new ArrayList<>();
		CwnTrack track = factory.createTrack(PPQ);
		track.addEvent(timeSignatureEvent1);
		track.addEvent(factory.createNoteEvent(PositionTools.getPosition(track, new Trias("3.3.0")), PPQ, 78, 0, 0, 0));
		// track.addEvent(factory.createNoteEvent(D8T, D8T, 78, 0, 0, 0));
		// track.addEvent(factory.createNoteEvent(2 * D8T, D8T, 78, 0, 0, 0));
		trackList.add(track);
		ScoreParameter scoreParameter = new ScoreParameter(PPQ, D4 / 16, 1, 4,Score.SPLIT_RESTS | Score.ALLOW_DOTTED_RESTS,
				Arrays.asList(new DurationType[] { DurationType.REGULAR, DurationType.DOTTED, DurationType.BIDOTTED, DurationType.TRIPLET, DurationType.QUINTUPLET }),
				new ArrayList<>(), 0, 0, new HashMap<>()); // 4,Score.SPLIT_RESTS | Score.ALLOW_DOTTED_RESTS, 0); // 4 bars
		ScoreBuilder scoreBuilder = new ScoreBuilder(new TrackContainer(trackList, 0), scoreParameter, new SampleScoreLayout());
		for (ScoreSystem sys : scoreBuilder) {
			for (ScoreStaff staff : sys) {
				for (ScoreBar bar : staff) {
					System.out.println(bar);
				}
			}
		}
	}
	
	@Test
	public void testQuantizedNote() {
		int stemDirection = 0;
		boolean isUngrouped = false;
		CwnFactory factory = new SampleFactory();
		SimpleTimeSignature ts1 = new SimpleTimeSignature("5/4");
		List<CwnTrack> trackList = new ArrayList<>();
		CwnTrack track = factory.createTrack(PPQ);
		track.addEvent(factory.createTimeSignatureEvent(0, ts1));
		track.addEvent(factory.createKeyEvent(0, 0));
		track.addEvent(factory.createClefEvent(0, 0));
		CwnNoteEvent cwnNoteEvent = factory.createNoteEvent(PositionTools.getPosition(track, new Trias("3.3.44")), PPQ + 15, 78, 0, 0, 0);
		track.addEvent(cwnNoteEvent);
		trackList.add(track);
		int resolutionInTicks = D4 / 8;
		ScoreParameter scoreParameter = new ScoreParameter(PPQ, resolutionInTicks, 1, 4, Score.SPLIT_RESTS | Score.ALLOW_DOTTED_RESTS,
				Arrays.asList(new DurationType[] { DurationType.REGULAR, DurationType.DOTTED, DurationType.BIDOTTED, DurationType.TRIPLET, DurationType.QUINTUPLET }),
				new ArrayList<>(), 0, 0, new HashMap<>()); // 4, Score.SPLIT_RESTS | Score.ALLOW_DOTTED_RESTS, 0); // 4 bars
		long barStartPosition = PositionTools.getPosition(track, new Trias("3.1.0"));
		ScoreBar scoreBar = new ScoreBar(barStartPosition, track, scoreParameter);
		QuantizedPosition positionInBar = new QuantizedPosition(scoreBar, cwnNoteEvent.getPosition(), ts1.getMetric());
		QuantizedDuration duration = new QuantizedDuration(scoreParameter, cwnNoteEvent.getDuration());
		ScoreNote scoreNote = new ScoreNote(scoreBar, cwnNoteEvent, positionInBar, duration, stemDirection, isUngrouped);
		assertTrue("note start should be: " + 12 * PPQ + ", but is: " + scoreNote.getStartPosition(), scoreNote.getStartPosition() == 12 * PPQ);
		assertTrue("note duration should be: " + PPQ + ", but is: " + scoreNote.getDuration(), scoreNote.getDuration() == PPQ);
	}
}
