package org.wuerthner.cwn;

import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.junit.Ignore;
import org.junit.Test;
import org.wuerthner.cwn.api.CwnFactory;
import org.wuerthner.cwn.api.CwnNoteEvent;
import org.wuerthner.cwn.api.CwnTimeSignatureEvent;
import org.wuerthner.cwn.api.CwnTrack;
import org.wuerthner.cwn.api.ScoreParameter;
import org.wuerthner.cwn.sample.SampleFactory;
import org.wuerthner.cwn.sample.SampleScoreLayout;
import org.wuerthner.cwn.score.Score;
import org.wuerthner.cwn.score.ScoreBar;
import org.wuerthner.cwn.score.ScoreBuilder;
import org.wuerthner.cwn.score.ScoreObject;
import org.wuerthner.cwn.score.ScoreStaff;
import org.wuerthner.cwn.score.ScoreSystem;
import org.wuerthner.cwn.timesignature.SimpleTimeSignature;

public class ScoreBuilderTest {
	int PPQ = 384; // CwnEvent.DEFAULT_PULSE_PER_QUARTER;
	int D1 = 4 * PPQ;
	int D2 = 2 * PPQ;
	int D4 = PPQ;
	int D8 = (int) (0.5 * PPQ);
	int D8T = (int) (PPQ * 1.0 / 3);
	int D16 = (int) (0.25 * PPQ);
	
	@Test
	@Ignore
	public void testDurationType() {
		CwnFactory factory = new SampleFactory();
		SimpleTimeSignature ts1 = new SimpleTimeSignature("4/4");
		CwnTimeSignatureEvent timeSignatureEvent1 = factory.createTimeSignatureEvent(0, ts1);
		List<CwnTrack> trackList = new ArrayList<>();
		CwnTrack track = factory.createTrack(PPQ);
		track.addEvent(timeSignatureEvent1);
		track.addEvent(factory.createNoteEvent(D2 * 2 + D2 + D4 + D8T, D8T, 78, 0, 0, 0));
		// track.addEvent(factory.createNoteEvent(D8T, D8T, 78, 0, 0, 0));
		// track.addEvent(factory.createNoteEvent(2 * D8T, D8T, 78, 0, 0, 0));
		trackList.add(track);
		ScoreParameter scoreParameter = new ScoreParameter(0, 2 * PPQ * 4, PPQ, D1 / 16, 1, 4, Score.SPLIT_RESTS); // 4 bars
		ScoreBuilder scoreBuilder = new ScoreBuilder(trackList, scoreParameter, new SampleScoreLayout());
		for (ScoreSystem sys : scoreBuilder) {
			for (ScoreStaff staff : sys) {
				for (ScoreBar bar : staff) {
					System.out.println(bar);
				}
			}
		}
	}
	
	@Test
	@Ignore
	public void testBuilder() {
		CwnFactory factory = new SampleFactory();
		SimpleTimeSignature ts1 = new SimpleTimeSignature("5/4");
		SimpleTimeSignature ts2 = new SimpleTimeSignature("4/4");
		SimpleTimeSignature ts3 = new SimpleTimeSignature("5/4");
		CwnTimeSignatureEvent timeSignatureEvent1 = factory.createTimeSignatureEvent(0, ts1);
		CwnTimeSignatureEvent timeSignatureEvent2 = factory.createTimeSignatureEvent(2 * 6 * D8, ts2);
		CwnTimeSignatureEvent timeSignatureEvent3 = factory.createTimeSignatureEvent(2 * 6 * D8 + 4 * D4, ts3);
		List<CwnTrack> trackList = new ArrayList<>();
		CwnTrack track = factory.createTrack(PPQ);
		CwnNoteEvent noteEvent1 = factory.createNoteEvent(0, D8, 78, 0, 0, 0);
		CwnNoteEvent noteEvent2 = factory.createNoteEvent(1920 + 3 * D4 + D8, D8, 80, 0, 0, 0);
		CwnNoteEvent noteEvent3 = factory.createNoteEvent(2 * 6 * D8 + 4 * D4 + 3 * D4, D2, 82, 0, 0, 0);
		track.addEvent(timeSignatureEvent1);
		// track.addEvent(timeSignatureEvent2);
		// track.addEvent(timeSignatureEvent3);
		track.addEvent(noteEvent1);
		// track.addEvent(noteEvent2);
		// track.addEvent(noteEvent3);
		trackList.add(track);
		ScoreParameter scoreParameter = new ScoreParameter(0, 2 * PPQ * 4, PPQ, D1 / 16, 1, 4, Score.NONE); // 4 bars
		ScoreBuilder scoreBuilder = new ScoreBuilder(trackList, scoreParameter, new SampleScoreLayout());
		for (ScoreSystem sys : scoreBuilder) {
			for (ScoreStaff staff : sys) {
				for (ScoreBar bar : staff) {
					System.out.println(bar);
				}
			}
		}
	}
	
	@Test
	@Ignore
	public void testPartition() {
		CwnFactory factory = new SampleFactory();
		SimpleTimeSignature ts = new SimpleTimeSignature("4/4");
		CwnTimeSignatureEvent timeSignatureEvent = factory.createTimeSignatureEvent(0, ts);
		List<CwnTrack> trackList = new ArrayList<>();
		CwnTrack track = factory.createTrack(PPQ);
		CwnNoteEvent noteEvent1 = factory.createNoteEvent(0, D8, 78, 0, 0, 0);
		CwnNoteEvent noteEvent2 = factory.createNoteEvent(D4, 9 * D4, 80, 0, 0, 0);
		track.addEvent(timeSignatureEvent);
		track.addEvent(noteEvent1);
		track.addEvent(noteEvent2);
		trackList.add(track);
		ScoreParameter scoreParameter = new ScoreParameter(0, 3 * PPQ * 4, PPQ, D1 / 16, 1, 4, Score.SPLIT_RESTS); // 4 bars
		ScoreBuilder scoreBuilder = new ScoreBuilder(trackList, scoreParameter, new SampleScoreLayout());
		for (ScoreSystem sys : scoreBuilder) {
			for (ScoreStaff staff : sys) {
				for (ScoreBar bar : staff) {
					System.out.println(bar);
				}
			}
		}
	}
	
	@Test
	public void test34() {
		CwnFactory factory = new SampleFactory();
		SimpleTimeSignature ts = new SimpleTimeSignature("3/4");
		CwnTimeSignatureEvent timeSignatureEvent = factory.createTimeSignatureEvent(0, ts);
		List<CwnTrack> trackList = new ArrayList<>();
		CwnTrack track = factory.createTrack(PPQ);
		CwnNoteEvent noteEvent1 = factory.createNoteEvent(0, D8, 78, 0, 0, 0);
		CwnNoteEvent noteEvent2 = factory.createNoteEvent(D2 + D4 + D8, D16, 78, 0, 0, 0);
		track.addEvent(timeSignatureEvent);
		track.addEvent(factory.createKeyEvent(0, 0));
		track.addEvent(factory.createClefEvent(0, 0));
		track.addEvent(noteEvent1);
		track.addEvent(noteEvent2);
		trackList.add(track);
		ScoreParameter scoreParameter = new ScoreParameter(0, 3 * PPQ * 4, PPQ, D1 / 16, 1, 4, Score.ALLOW_DOTTED_RESTS); // 4 bars
		ScoreBuilder scoreBuilder = new ScoreBuilder(trackList, scoreParameter, new SampleScoreLayout(), 1);
		Iterator<ScoreBar> barIterator = scoreBuilder.iterator().next().iterator().next().iterator();
		ScoreBar bar;
		ScoreObject object;
		Iterator<ScoreObject> scoreObjectIterator;
		
		// first bar
		bar = barIterator.next();
		assertTrue(bar.getStartPosition() == 0 && bar.getDuration() == 3 * D4);
		scoreObjectIterator = bar.iterator().next().iterator();
		
		object = scoreObjectIterator.next();
		assertTrue(object.getStartPosition() == 0 && object.getDuration() == D8); // note
		object = scoreObjectIterator.next();
		assertTrue(object.getStartPosition() == D8 && object.getDuration() == D8); // rest
		object = scoreObjectIterator.next();
		assertTrue(object.getStartPosition() == D4 && object.getDuration() == D4); // rest
		object = scoreObjectIterator.next();
		assertTrue(object.getStartPosition() == D2 && object.getDuration() == D4); // rest
		
		// second bar
		bar = barIterator.next();
		assertTrue(bar.getStartPosition() == 3 * D4 && bar.getDuration() == 3 * D4);
		scoreObjectIterator = bar.iterator().next().iterator();
		
		object = scoreObjectIterator.next();
		assertTrue(object.getStartPosition() == 3 * D4 && object.getDuration() == D8); // rest
		object = scoreObjectIterator.next();
		assertTrue(object.getStartPosition() == 3 * D4 + D8 && object.getDuration() == D16); // note
		object = scoreObjectIterator.next();
		assertTrue(object.getStartPosition() == 3 * D4 + D8 + D16 && object.getDuration() == D16); // rest
		object = scoreObjectIterator.next();
		assertTrue(object.getStartPosition() == 3 * D4 + D4 && object.getDuration() == D4); // rest
		object = scoreObjectIterator.next();
		assertTrue(object.getStartPosition() == 3 * D4 + 2 * D4 && object.getDuration() == D4); // rest
		
		// third bar
		bar = barIterator.next();
		assertTrue(bar.getStartPosition() == 6 * D4 && bar.getDuration() == 3 * D4);
		scoreObjectIterator = bar.iterator().next().iterator();
		
		object = scoreObjectIterator.next();
		assertTrue(object.getStartPosition() == 6 * D4 && object.getDuration() == 3 * D4); // rest
		
		// fourth bar
		bar = barIterator.next();
		assertTrue(bar.getStartPosition() == 9 * D4 && bar.getDuration() == 3 * D4);
		scoreObjectIterator = bar.iterator().next().iterator();
		
		object = scoreObjectIterator.next();
		assertTrue(object.getStartPosition() == 9 * D4 && object.getDuration() == 3 * D4); // rest
	}
}
