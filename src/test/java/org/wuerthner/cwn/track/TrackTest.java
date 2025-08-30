package org.wuerthner.cwn.track;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import org.junit.Test;
import org.wuerthner.cwn.api.*;
import org.wuerthner.cwn.api.exception.TimeSignatureException;
import org.wuerthner.cwn.position.PositionTools;
import org.wuerthner.cwn.sample.SampleFactory;
import org.wuerthner.cwn.sample.SampleScoreLayout;
import org.wuerthner.cwn.score.ScoreBuilder;
import org.wuerthner.cwn.score.TrackContainer;
import org.wuerthner.cwn.timesignature.SimpleTimeSignature;

public class TrackTest {
	int PPQ = CwnEvent.DEFAULT_PULSE_PER_QUARTER;
	int D2 = 2 * PPQ;
	int D4 = PPQ;
	int D8 = (int) (0.5 * PPQ);
	
	@Test
	public void testSimpleTimeSignatureTrack() {
		CwnFactory factory = new SampleFactory();
		TimeSignature timeSignature = new SimpleTimeSignature("2*3/8");
		TimeSignature timeSignature2 = new SimpleTimeSignature("4/4");
		CwnTrack tsTrack = factory.createTrack(PPQ);
		tsTrack.addEvent(factory.createTimeSignatureEvent(0, timeSignature));
		tsTrack.addEvent(factory.createTimeSignatureEvent(6 * PPQ, timeSignature2));
		Iterator<CwnTimeSignatureEvent> tsIterator = tsTrack.getList(CwnTimeSignatureEvent.class).iterator();
		CwnTimeSignatureEvent tsEvent;
		tsEvent = tsIterator.next();
		assertTrue(tsEvent.getPosition() == 0);
		assertTrue(tsEvent.getTimeSignature().equals(timeSignature));
		
		tsEvent = tsIterator.next();
		assertTrue(tsEvent.getPosition() == 6 * PPQ);
		assertTrue(tsEvent.getTimeSignature().equals(timeSignature2));
		
		Trias trias;
		try {
			trias = PositionTools.getTrias(tsTrack, 0);
			assertTrue(trias.bar == 0);
			assertTrue(trias.beat == 0);
			assertTrue(trias.tick == 0);
			assertTrue(PositionTools.getPosition(tsTrack, trias) == 0);
			
			trias = PositionTools.getTrias(tsTrack, D8 - 1);
			assertTrue(trias.bar == 0);
			assertTrue(trias.beat == 0);
			assertTrue(trias.tick == D8 - 1);
			assertTrue(PositionTools.getPosition(tsTrack, trias) == D8 - 1);
			
			trias = PositionTools.getTrias(tsTrack, D8);
			assertTrue(trias.bar == 0);
			assertTrue(trias.beat == 1);
			assertTrue(trias.tick == 0);
			assertTrue(PositionTools.getPosition(tsTrack, trias) == D8);
			
			trias = PositionTools.getTrias(tsTrack, D4 + 1);
			assertTrue(trias.bar == 0);
			assertTrue(trias.beat == 2);
			assertTrue(trias.tick == 1);
			assertTrue(PositionTools.getPosition(tsTrack, trias) == D4 + 1);
			
			trias = PositionTools.getTrias(tsTrack, 3 * PPQ - 1);
			assertTrue(trias.bar == 0);
			assertTrue(trias.beat == 5);
			assertTrue(trias.tick == D8 - 1);
			assertTrue(PositionTools.getPosition(tsTrack, trias) == 3 * PPQ - 1);
			
			trias = PositionTools.getTrias(tsTrack, 3 * PPQ);
			assertTrue(trias.bar == 1);
			assertTrue(trias.beat == 0);
			assertTrue(trias.tick == 0);
			assertTrue(PositionTools.getPosition(tsTrack, trias) == 3 * PPQ);
			
			trias = PositionTools.getTrias(tsTrack, 4 * PPQ + 3);
			assertTrue(trias.bar == 1);
			assertTrue(trias.beat == 2);
			assertTrue(trias.tick == 3);
			assertTrue(PositionTools.getPosition(tsTrack, trias) == 4 * PPQ + 3);
			
			trias = PositionTools.getTrias(tsTrack, 6 * PPQ);
			assertTrue(trias.bar == 2);
			assertTrue(trias.beat == 0);
			assertTrue(trias.tick == 0);
			assertTrue(PositionTools.getPosition(tsTrack, trias) == 6 * PPQ);
			
			trias = PositionTools.getTrias(tsTrack, 7 * PPQ);
			assertTrue(trias.bar == 2);
			assertTrue(trias.beat == 1);
			assertTrue(trias.tick == 0);
			assertTrue(PositionTools.getPosition(tsTrack, trias) == 7 * PPQ);
			
			trias = PositionTools.getTrias(tsTrack, 9 * PPQ + 1);
			assertTrue(trias.bar == 2);
			assertTrue(trias.beat == 3);
			assertTrue(trias.tick == 1);
			assertTrue(PositionTools.getPosition(tsTrack, trias) == 9 * PPQ + 1);
			
			trias = PositionTools.getTrias(tsTrack, 14 * PPQ);
			assertTrue(trias.bar == 4);
			assertTrue(trias.beat == 0);
			assertTrue(trias.tick == 0);
			assertTrue(PositionTools.getPosition(tsTrack, trias) == 14 * PPQ);
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	@Test
	public void testTimeSignatureException() {
		CwnFactory factory = new SampleFactory();
		TimeSignature timeSignature = new SimpleTimeSignature("2*3/8");
		TimeSignature timeSignature2 = new SimpleTimeSignature("4/4");
		CwnTrack tsTrack = factory.createTrack(PPQ);
		tsTrack.addEvent(factory.createTimeSignatureEvent(0, timeSignature));
		tsTrack.addEvent(factory.createTimeSignatureEvent(D8, timeSignature2));
		try {
			PositionTools.getTrias(tsTrack, 12 * PPQ);
			assertTrue(false);
		} catch (TimeSignatureException e) {
			assertTrue(e.getMessage().equals("Error: Metric change inside Measure!"));
		}
	}
	
	@Test
	public void testTimeSignatureTrackNextBar() throws TimeSignatureException {
		CwnFactory factory = new SampleFactory();
		TimeSignature timeSignature = new SimpleTimeSignature("2*3/8");
		TimeSignature timeSignature2 = new SimpleTimeSignature("4/4");
		TimeSignature timeSignature3 = new SimpleTimeSignature("2/8");
		CwnTrack tsTrack = factory.createTrack(PPQ);
		tsTrack.addEvent(factory.createTimeSignatureEvent(0, timeSignature));
		tsTrack.addEvent(factory.createTimeSignatureEvent(6 * PPQ, timeSignature2));
		tsTrack.addEvent(factory.createTimeSignatureEvent(22 * PPQ, timeSignature3));
		Trias trias = new Trias(0, 0, 0);
		// System.out.println(trias + " - " + PositionTools.getPosition(tsTrack, trias));
		assertTrue(PositionTools.getPosition(tsTrack, trias) == 0);
		trias = trias.nextBar();
		// System.out.println(trias + " - " + PositionTools.getPosition(tsTrack, trias));
		assertTrue(PositionTools.getPosition(tsTrack, trias) == 3 * PPQ);
		trias = trias.nextBar();
		// System.out.println(trias + " - " + PositionTools.getPosition(tsTrack, trias));
		assertTrue(PositionTools.getPosition(tsTrack, trias) == 6 * PPQ);
		trias = trias.nextBar();
		// System.out.println(trias + " - " + PositionTools.getPosition(tsTrack, trias));
		assertTrue(PositionTools.getPosition(tsTrack, trias) == 10 * PPQ);
		trias = trias.nextBar();
		// System.out.println(trias + " - " + PositionTools.getPosition(tsTrack, trias));
		assertTrue(PositionTools.getPosition(tsTrack, trias) == 14 * PPQ);
		trias = trias.nextBar();
		// System.out.println(trias + " - " + PositionTools.getPosition(tsTrack, trias));
		assertTrue(PositionTools.getPosition(tsTrack, trias) == 18 * PPQ);
		trias = trias.nextBar();
		// System.out.println(trias + " - " + PositionTools.getPosition(tsTrack, trias));
		assertTrue(PositionTools.getPosition(tsTrack, trias) == 22 * PPQ);
		trias = trias.nextBar();
		// System.out.println(trias + " - " + PositionTools.getPosition(tsTrack, trias));
		assertTrue(PositionTools.getPosition(tsTrack, trias) == 23 * PPQ);
		trias = trias.nextBar();
		// System.out.println(trias + " - " + PositionTools.getPosition(tsTrack, trias));
		assertTrue(PositionTools.getPosition(tsTrack, trias) == 24 * PPQ);
	}
	
	@Test
	public void testNextBeat() throws TimeSignatureException {
		CwnFactory factory = new SampleFactory();
		TimeSignature timeSignature = new SimpleTimeSignature("2*3/8");
		TimeSignature timeSignature2 = new SimpleTimeSignature("4/4");
		TimeSignature timeSignature3 = new SimpleTimeSignature("2/8");
		CwnTrack tsTrack = factory.createTrack(PPQ);
		tsTrack.addEvent(factory.createTimeSignatureEvent(0, timeSignature)); // 6/8 from 1.1.0
		tsTrack.addEvent(factory.createTimeSignatureEvent(6 * PPQ, timeSignature2)); // 4/4 from 3.1.0
		tsTrack.addEvent(factory.createTimeSignatureEvent(22 * PPQ, timeSignature3)); // 2/8 from 7.1.0
		
		Trias trias;
		trias = new Trias(0, 0, 0);
		
		// System.out.println(trias + " - " + PositionTools.getPosition(tsTrack, trias));
		assertTrue(trias.bar == 0);
		assertTrue(trias.beat == 0);
		assertTrue(trias.tick == 0);
		assertTrue(PositionTools.getPosition(tsTrack, trias) == 0);
		trias = PositionTools.getTrias(tsTrack, PositionTools.getPosition(tsTrack, trias.nextBeat()));
		// System.out.println(trias + " - " + PositionTools.getPosition(tsTrack, trias));
		assertTrue(trias.bar == 0);
		assertTrue(trias.beat == 1);
		assertTrue(trias.tick == 0);
		assertTrue(PositionTools.getPosition(tsTrack, trias) == 1 * D8);
		trias = PositionTools.getTrias(tsTrack, PositionTools.getPosition(tsTrack, trias.nextBeat()));
		// System.out.println(trias + " - " + PositionTools.getPosition(tsTrack, trias));
		assertTrue(trias.bar == 0);
		assertTrue(trias.beat == 2);
		assertTrue(trias.tick == 0);
		assertTrue(PositionTools.getPosition(tsTrack, trias) == 2 * D8);
		trias = PositionTools.getTrias(tsTrack, PositionTools.getPosition(tsTrack, trias.nextBeat()));
		// System.out.println(trias + " - " + PositionTools.getPosition(tsTrack, trias));
		assertTrue(trias.bar == 0);
		assertTrue(trias.beat == 3);
		assertTrue(trias.tick == 0);
		assertTrue(PositionTools.getPosition(tsTrack, trias) == 3 * D8);
		trias = PositionTools.getTrias(tsTrack, PositionTools.getPosition(tsTrack, trias.nextBeat()));
		// System.out.println(trias + " - " + PositionTools.getPosition(tsTrack, trias));
		assertTrue(trias.bar == 0);
		assertTrue(trias.beat == 4);
		assertTrue(trias.tick == 0);
		assertTrue(PositionTools.getPosition(tsTrack, trias) == 4 * D8);
		trias = PositionTools.getTrias(tsTrack, PositionTools.getPosition(tsTrack, trias.nextBeat()));
		// System.out.println(trias + " - " + PositionTools.getPosition(tsTrack, trias));
		assertTrue(trias.bar == 0);
		assertTrue(trias.beat == 5);
		assertTrue(trias.tick == 0);
		assertTrue(PositionTools.getPosition(tsTrack, trias) == 5 * D8);
		trias = PositionTools.getTrias(tsTrack, PositionTools.getPosition(tsTrack, trias.nextBeat()));
		// System.out.println(trias + " - " + PositionTools.getPosition(tsTrack, trias));
		assertTrue(trias.bar == 1);
		assertTrue(trias.beat == 0);
		assertTrue(trias.tick == 0);
		assertTrue(PositionTools.getPosition(tsTrack, trias) == 6 * D8);
		trias = PositionTools.getTrias(tsTrack, PositionTools.getPosition(tsTrack, trias.nextBeat()));
		// System.out.println(trias + " - " + PositionTools.getPosition(tsTrack, trias));
		assertTrue(trias.bar == 1);
		assertTrue(trias.beat == 1);
		assertTrue(trias.tick == 0);
		assertTrue(PositionTools.getPosition(tsTrack, trias) == 7 * D8);
		trias = PositionTools.getTrias(tsTrack, PositionTools.getPosition(tsTrack, trias.nextBeat()));
		// System.out.println(trias + " - " + PositionTools.getPosition(tsTrack, trias));
		assertTrue(trias.bar == 1);
		assertTrue(trias.beat == 2);
		assertTrue(trias.tick == 0);
		assertTrue(PositionTools.getPosition(tsTrack, trias) == 8 * D8);
		trias = PositionTools.getTrias(tsTrack, PositionTools.getPosition(tsTrack, trias.nextBeat()));
		// System.out.println(trias + " - " + PositionTools.getPosition(tsTrack, trias));
		assertTrue(trias.bar == 1);
		assertTrue(trias.beat == 3);
		assertTrue(trias.tick == 0);
		assertTrue(PositionTools.getPosition(tsTrack, trias) == 9 * D8);
		trias = PositionTools.getTrias(tsTrack, PositionTools.getPosition(tsTrack, trias.nextBeat()));
		// System.out.println(trias + " - " + PositionTools.getPosition(tsTrack, trias));
		assertTrue(trias.bar == 1);
		assertTrue(trias.beat == 4);
		assertTrue(trias.tick == 0);
		assertTrue(PositionTools.getPosition(tsTrack, trias) == 10 * D8);
		trias = PositionTools.getTrias(tsTrack, PositionTools.getPosition(tsTrack, trias.nextBeat()));
		// System.out.println(trias + " - " + PositionTools.getPosition(tsTrack, trias));
		assertTrue(trias.bar == 1);
		assertTrue(trias.beat == 5);
		assertTrue(trias.tick == 0);
		assertTrue(PositionTools.getPosition(tsTrack, trias) == 11 * D8);
		trias = PositionTools.getTrias(tsTrack, PositionTools.getPosition(tsTrack, trias.nextBeat()));
		// System.out.println(trias + " - " + PositionTools.getPosition(tsTrack, trias));
		assertTrue(trias.bar == 2);
		assertTrue(trias.beat == 0);
		assertTrue(trias.tick == 0);
		assertTrue(PositionTools.getPosition(tsTrack, trias) == 12 * D8);
		trias = PositionTools.getTrias(tsTrack, PositionTools.getPosition(tsTrack, trias.nextBeat()));
		// System.out.println(trias + " - " + PositionTools.getPosition(tsTrack, trias));
		assertTrue(trias.bar == 2);
		assertTrue(trias.beat == 1);
		assertTrue(trias.tick == 0);
		assertTrue(PositionTools.getPosition(tsTrack, trias) == 14 * D8);
		trias = PositionTools.getTrias(tsTrack, PositionTools.getPosition(tsTrack, trias.nextBeat()));
		// System.out.println(trias + " - " + PositionTools.getPosition(tsTrack, trias));
		assertTrue(trias.bar == 2);
		assertTrue(trias.beat == 2);
		assertTrue(trias.tick == 0);
		assertTrue(PositionTools.getPosition(tsTrack, trias) == 16 * D8);
		trias = PositionTools.getTrias(tsTrack, PositionTools.getPosition(tsTrack, trias.nextBeat()));
		// System.out.println(trias + " - " + PositionTools.getPosition(tsTrack, trias));
		assertTrue(trias.bar == 2);
		assertTrue(trias.beat == 3);
		assertTrue(trias.tick == 0);
		assertTrue(PositionTools.getPosition(tsTrack, trias) == 18 * D8);
		trias = PositionTools.getTrias(tsTrack, PositionTools.getPosition(tsTrack, trias.nextBeat()));
		// System.out.println(trias + " - " + PositionTools.getPosition(tsTrack, trias));
		assertTrue(trias.bar == 3);
		assertTrue(trias.beat == 0);
		assertTrue(trias.tick == 0);
		assertTrue(PositionTools.getPosition(tsTrack, trias) == 20 * D8);
		trias = PositionTools.getTrias(tsTrack, PositionTools.getPosition(tsTrack, trias.nextBeat()));
		// System.out.println(trias + " - " + PositionTools.getPosition(tsTrack, trias));
		assertTrue(trias.bar == 3);
		assertTrue(trias.beat == 1);
		assertTrue(trias.tick == 0);
		assertTrue(PositionTools.getPosition(tsTrack, trias) == 22 * D8);
		trias = PositionTools.getTrias(tsTrack, PositionTools.getPosition(tsTrack, trias.nextBeat()));
		// System.out.println(trias + " - " + PositionTools.getPosition(tsTrack, trias));
		assertTrue(trias.bar == 3);
		assertTrue(trias.beat == 2);
		assertTrue(trias.tick == 0);
		assertTrue(PositionTools.getPosition(tsTrack, trias) == 24 * D8);
		trias = PositionTools.getTrias(tsTrack, PositionTools.getPosition(tsTrack, trias.nextBeat()));
		// System.out.println(trias + " - " + PositionTools.getPosition(tsTrack, trias));
		assertTrue(trias.bar == 3);
		assertTrue(trias.beat == 3);
		assertTrue(trias.tick == 0);
		assertTrue(PositionTools.getPosition(tsTrack, trias) == 26 * D8);
		trias = PositionTools.getTrias(tsTrack, PositionTools.getPosition(tsTrack, trias.nextBeat()));
		
		trias = PositionTools.getTrias(tsTrack, PositionTools.getPosition(tsTrack, trias.nextBeat()));
		trias = PositionTools.getTrias(tsTrack, PositionTools.getPosition(tsTrack, trias.nextBeat()));
		trias = PositionTools.getTrias(tsTrack, PositionTools.getPosition(tsTrack, trias.nextBeat()));
		trias = PositionTools.getTrias(tsTrack, PositionTools.getPosition(tsTrack, trias.nextBeat()));
		trias = PositionTools.getTrias(tsTrack, PositionTools.getPosition(tsTrack, trias.nextBeat()));
		trias = PositionTools.getTrias(tsTrack, PositionTools.getPosition(tsTrack, trias.nextBeat()));
		trias = PositionTools.getTrias(tsTrack, PositionTools.getPosition(tsTrack, trias.nextBeat()));
		trias = PositionTools.getTrias(tsTrack, PositionTools.getPosition(tsTrack, trias.nextBeat()));
		
		// System.out.println(trias + " - " + PositionTools.getPosition(tsTrack, trias));
		assertTrue(trias.bar == 6);
		assertTrue(trias.beat == 0);
		assertTrue(trias.tick == 0);
		assertTrue(PositionTools.getPosition(tsTrack, trias) == 44 * D8);
		trias = PositionTools.getTrias(tsTrack, PositionTools.getPosition(tsTrack, trias.nextBeat()));
		// System.out.println(trias + " - " + PositionTools.getPosition(tsTrack, trias));
		assertTrue(trias.bar == 6);
		assertTrue(trias.beat == 1);
		assertTrue(trias.tick == 0);
		assertTrue(PositionTools.getPosition(tsTrack, trias) == 45 * D8);
		trias = PositionTools.getTrias(tsTrack, PositionTools.getPosition(tsTrack, trias.nextBeat()));
		// System.out.println(trias + " - " + PositionTools.getPosition(tsTrack, trias));
		assertTrue(trias.bar == 7);
		assertTrue(trias.beat == 0);
		assertTrue(trias.tick == 0);
		assertTrue(PositionTools.getPosition(tsTrack, trias) == 46 * D8);
		trias = PositionTools.getTrias(tsTrack, PositionTools.getPosition(tsTrack, trias.nextBeat()));
		// System.out.println(trias + " - " + PositionTools.getPosition(tsTrack, trias));
		assertTrue(trias.bar == 7);
		assertTrue(trias.beat == 1);
		assertTrue(trias.tick == 0);
		assertTrue(PositionTools.getPosition(tsTrack, trias) == 47 * D8);
		trias = PositionTools.getTrias(tsTrack, PositionTools.getPosition(tsTrack, trias.nextBeat()));
		// System.out.println(trias + " - " + PositionTools.getPosition(tsTrack, trias));
		assertTrue(trias.bar == 8);
		assertTrue(trias.beat == 0);
		assertTrue(trias.tick == 0);
		assertTrue(PositionTools.getPosition(tsTrack, trias) == 48 * D8);
		trias = PositionTools.getTrias(tsTrack, PositionTools.getPosition(tsTrack, trias.nextBeat()));
		// System.out.println(trias + " - " + PositionTools.getPosition(tsTrack, trias));
		assertTrue(trias.bar == 8);
		assertTrue(trias.beat == 1);
		assertTrue(trias.tick == 0);
		assertTrue(PositionTools.getPosition(tsTrack, trias) == 49 * D8);
	}
	
	@Test
	public void testTimeSignature() {
		CwnFactory factory = new SampleFactory();
		TimeSignature timeSignature = new SimpleTimeSignature("2*3/8");
		TimeSignature timeSignature2 = new SimpleTimeSignature("4/4");
		TimeSignature timeSignature3 = new SimpleTimeSignature("2/8");
		CwnTrack tsTrack = factory.createTrack(PPQ);
		tsTrack.addEvent(factory.createTimeSignatureEvent(0, timeSignature)); // 6/8 from 1.1.0
		tsTrack.addEvent(factory.createTimeSignatureEvent(6 * PPQ, timeSignature2)); // 4/4 from 3.1.0
		tsTrack.addEvent(factory.createTimeSignatureEvent(22 * PPQ, timeSignature3)); // 2/8 from 7.1.0
		
		assertTrue("ts should be 6/8, but is: " + tsTrack.getTimeSignature(0).getTimeSignature().toString(), tsTrack.getTimeSignature(0).getTimeSignature().toString().equals("6/8"));
		assertTrue(tsTrack.getTimeSignature(6 * PPQ - 1).getTimeSignature().toString().equals("6/8"));
		assertTrue(tsTrack.getTimeSignature(6 * PPQ).getTimeSignature().toString().equals("4/4"));
		assertTrue(tsTrack.getTimeSignature(22 * PPQ - 1).getTimeSignature().toString().equals("4/4"));
		assertTrue(tsTrack.getTimeSignature(22 * PPQ).getTimeSignature().toString().equals("2/8"));
		assertTrue(tsTrack.getTimeSignature(31 * PPQ).getTimeSignature().toString().equals("2/8"));
	}
	
	@Test
	public void testPosition() {
		CwnFactory factory = new SampleFactory();
		TimeSignature timeSignature = new SimpleTimeSignature("4/4");
		CwnTrack cwnTrack = factory.createTrack(384);
		cwnTrack.addEvent(factory.createTimeSignatureEvent(0, timeSignature));
		assert (PositionTools.getPosition(cwnTrack, "1.2:8") == (int) (1.5 * cwnTrack.getPPQ()));
		assert (PositionTools.getPosition(cwnTrack, "1.2:16") == (int) (1.25 * cwnTrack.getPPQ()));
		assert (PositionTools.getPosition(cwnTrack, "1.2:8+16") == (int) (1.75 * cwnTrack.getPPQ()));
		assert (PositionTools.getPosition(cwnTrack, "1.2:8T") == (int) (cwnTrack.getPPQ() + cwnTrack.getPPQ() / 3));
	}

	@Test
	public void testEmptyBars() {
		CwnFactory factory = new SampleFactory();
		TimeSignature timeSignature = new SimpleTimeSignature("4/4");
		CwnTrack cwnTrack = factory.createTrack(384);
		List<CwnTrack> trackList = new ArrayList<>();
		CwnTrack track = factory.createTrack(PPQ);
		track.addEvent(factory.createTimeSignatureEvent(0, timeSignature));
		track.addEvent(factory.createClefEvent(0, 0));
		track.addEvent(factory.createKeyEvent(0, 0));
		// track.addEvent(factory.createNoteEvent(D2 * 2 + D2 + D4 + D8T, D8T, 78, 0, 0, 0));
		trackList.add(track);

		ScoreParameter scoreParameter = new ScoreParameter(PPQ, 960 / 16, 1, 1, 1,
				Arrays.asList(new DurationType[] { DurationType.REGULAR, DurationType.DOTTED, DurationType.BIDOTTED, DurationType.TRIPLET, DurationType.QUINTUPLET }),
				new ArrayList<>(), 0, 0); // 4 bars
		ScoreBuilder scoreBuilder = new ScoreBuilder(new TrackContainer(trackList, 0), scoreParameter, new SampleScoreLayout());
		assertEquals(10, scoreBuilder.getNumberOfShownBars());
	}
}
