package org.wuerthner.cwn.score;

import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.junit.Test;
import org.wuerthner.cwn.api.CwnFactory;
import org.wuerthner.cwn.api.CwnNoteEvent;
import org.wuerthner.cwn.api.CwnTimeSignatureEvent;
import org.wuerthner.cwn.api.CwnTrack;
import org.wuerthner.cwn.api.ScoreParameter;
import org.wuerthner.cwn.position.PositionTools;
import org.wuerthner.cwn.sample.SampleFactory;
import org.wuerthner.cwn.sample.SampleScoreLayout;
import org.wuerthner.cwn.timesignature.SimpleTimeSignature;

public class NoteTest {
	int PPQ = 384;
	int D1 = 4 * PPQ;
	int D2 = 2 * PPQ;
	int D4 = PPQ;
	int D8 = (int) (0.5 * PPQ);
	int D16 = (int) (0.25 * PPQ);
	int D2T = (int) (PPQ * 4.0 / 3);
	int D4T = (int) (PPQ * 2.0 / 3);
	int D8T = (int) (0.5 * PPQ * 2.0 / 3);
	
	@Test
	public void testDuration() {
		CwnFactory factory = new SampleFactory();
		CwnNoteEvent noteEvent1 = factory.createNoteEvent(0, D8, 78, 0, 0, 0);
		CwnNoteEvent noteEvent2 = factory.createNoteEvent(D8, D8, 80, 0, 0, 0);
		CwnNoteEvent noteEvent3 = factory.createNoteEvent(D2, D2, 82, 0, 0, 0);
		ScoreParameter scoreParameter = new ScoreParameter(0, 4 * PPQ, PPQ, D1 / 8, 1, 4, 0, 0);
		CwnTrack track = factory.createTrack(PPQ);
		track.addEvent(factory.createTimeSignatureEvent(0, new SimpleTimeSignature("4/4")));
		track.addEvent(factory.createKeyEvent(0, 0));
		track.addEvent(factory.createClefEvent(0, 0));
		ScoreBar scoreBar = new ScoreBar(0, track, scoreParameter);
		ScoreNote note1 = new ScoreNote(scoreBar, noteEvent1);
		ScoreNote note2 = new ScoreNote(scoreBar, noteEvent2);
		ScoreNote note3 = new ScoreNote(scoreBar, noteEvent3);
		assertTrue(note1.getDuration() == D8);
		assertTrue(note2.getDuration() == D8);
		assertTrue(note3.getDuration() == D2);
	}
	
	@Test
	public void testOverlap() {
		CwnFactory factory = new SampleFactory();
		SimpleTimeSignature ts1 = new SimpleTimeSignature("4/4");
		CwnTimeSignatureEvent timeSignatureEvent1 = factory.createTimeSignatureEvent(0, ts1);
		List<CwnTrack> trackList = new ArrayList<>();
		CwnTrack track = factory.createTrack(PPQ);
		track.addEvent(timeSignatureEvent1);
		track.addEvent(factory.createKeyEvent(0, 0));
		track.addEvent(factory.createClefEvent(0, 0));
		track.addEvent(factory.createNoteEvent(PositionTools.getPosition(track, "1.1:8"), D4, 78, 0, 0, 0));
		track.addEvent(factory.createNoteEvent(PositionTools.getPosition(track, "1.1:8+16"), D16, 78, 0, 0, 0));
		trackList.add(track);
		ScoreParameter scoreParameter = new ScoreParameter(0, 5 * PPQ * 4, PPQ, D1 / 16, 1, 4, Score.SPLIT_RESTS | Score.ALLOW_DOTTED_RESTS, 0); // 4 bars
		ScoreBuilder scoreBuilder = new ScoreBuilder(new TrackContainer(trackList, 0), scoreParameter, new SampleScoreLayout(), 1);
		Iterator<ScoreBar> barIterator = scoreBuilder.iterator().next().iterator().next().iterator();
		ScoreVoice voice = barIterator.next().iterator().next();
		// output(voice);
		ScoreObject scoreObject;
		Iterator<ScoreObject> scoreObjectIterator = voice.iterator();
		scoreObject = scoreObjectIterator.next();
		assertTrue(scoreObject.getDuration() == D8);
		scoreObject = scoreObjectIterator.next();
		assertTrue(scoreObject.getDuration() == D16);
		assertTrue(((ScoreNote) ((ScoreChord) scoreObject).getObjectSet().first()).getCwnNoteEvent().getDuration() == D4);
		scoreObject = scoreObjectIterator.next();
		assertTrue(scoreObject.getDuration() == D16);
		
	}
	
	private void output(ScoreVoice voice) {
		for (ScoreGroup group : voice.getGroups()) {
			System.out.println(group.toString());
		}
		System.out.println("--");
		for (ScoreObject scoreObject : voice) {
			System.out.println(scoreObject);
		}
	}
}
