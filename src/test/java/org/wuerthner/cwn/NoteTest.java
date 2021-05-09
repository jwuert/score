package org.wuerthner.cwn;

import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;
import org.wuerthner.cwn.api.CwnEvent;
import org.wuerthner.cwn.api.CwnFactory;
import org.wuerthner.cwn.api.CwnTrack;
import org.wuerthner.cwn.api.ScoreParameter;
import org.wuerthner.cwn.sample.SampleFactory;
import org.wuerthner.cwn.sample.SampleScoreLayout;
import org.wuerthner.cwn.score.ScoreBuilder;
import org.wuerthner.cwn.score.ScoreChord;
import org.wuerthner.cwn.timesignature.SimpleTimeSignature;

public class NoteTest {
	int PPQ = CwnEvent.DEFAULT_PULSE_PER_QUARTER;
	int D1 = 4 * PPQ;
	int D2 = 2 * PPQ;
	int D4 = PPQ;
	int D8 = (int) (0.5 * PPQ);
	
	@Test
	public void testChord() {
		// res: 8=8th, 16=16th, etc
		// resolutionInTicks = PPQ*4/res = D4/res
		CwnFactory factory = new SampleFactory();
		ScoreParameter scoreParameter = new ScoreParameter(0, 4 * PPQ, PPQ, D1 / 8, 1, 4, 0);
		CwnTrack track = factory.createTrack(PPQ);
		track.addEvent(factory.createTimeSignatureEvent(0, new SimpleTimeSignature("4/4")));
		track.addEvent(factory.createKeyEvent(0, 0));
		track.addEvent(factory.createClefEvent(0, 0));
		track.addEvent(factory.createNoteEvent(0, D8, 65, 0, 0, 0));
		track.addEvent(factory.createNoteEvent(0, D8, 74, 0, 0, 0));
		track.addEvent(factory.createNoteEvent(0, D8, 72, 0, 0, 0));
		track.addEvent(factory.createNoteEvent(0, D8, 62, 0, 0, 0));
		track.addEvent(factory.createNoteEvent(0, D4, 71, 0, 0, 0));
		track.addEvent(factory.createNoteEvent(0, D8, 68, 0, 0, 0));
		List<CwnTrack> trackList = new ArrayList<>();
		trackList.add(track);
		ScoreBuilder builder = new ScoreBuilder(trackList, scoreParameter, new SampleScoreLayout(), 1);
		System.out.println(builder);
		ScoreChord chord = (ScoreChord) builder.iterator().next().iterator().next().iterator().next().iterator().next().iterator().next();
		assertTrue(chord.getMinimumNote().getPitch() == 62);
		assertTrue(chord.getMaximumNote().getPitch() == 74);
	}
	
	@Test
	public void testAmbiguous() {
		CwnFactory factory = new SampleFactory();
		ScoreParameter scoreParameter = new ScoreParameter(0, 4 * PPQ, PPQ, D1 / 8, 1, 4, 0);
		CwnTrack track = factory.createTrack(PPQ);
		track.addEvent(factory.createTimeSignatureEvent(0, new SimpleTimeSignature("4/4")));
		track.addEvent(factory.createKeyEvent(0, 0));
		track.addEvent(factory.createClefEvent(0, 0));
		track.addEvent(factory.createNoteEvent(0, D8, 65, 0, 0, 0));
		track.addEvent(factory.createNoteEvent(0, D8, 71, 0, 0, 0));
		List<CwnTrack> trackList = new ArrayList<>();
		trackList.add(track);
		ScoreBuilder builder = new ScoreBuilder(trackList, scoreParameter, new SampleScoreLayout(), 1);
		ScoreChord chord = (ScoreChord) builder.iterator().next().iterator().next().iterator().next().iterator().next().iterator().next();
		assertTrue(!chord.hasAmbiguousDuration());
		
		track.addEvent(factory.createNoteEvent(0, D4, 72, 0, 0, 0));
		builder = new ScoreBuilder(trackList, scoreParameter, new SampleScoreLayout(), 1);
		chord = (ScoreChord) builder.iterator().next().iterator().next().iterator().next().iterator().next().iterator().next();
		assertTrue(chord.hasAmbiguousDuration());
	}
}
