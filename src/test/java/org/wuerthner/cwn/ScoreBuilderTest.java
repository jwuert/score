package org.wuerthner.cwn;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.*;
import java.util.stream.Collectors;

import org.junit.Ignore;
import org.junit.Test;
import org.wuerthner.cwn.api.*;
import org.wuerthner.cwn.midi.MidiTrack;
import org.wuerthner.cwn.position.PositionTools;
import org.wuerthner.cwn.sample.SampleFactory;
import org.wuerthner.cwn.sample.SampleScoreLayout;
import org.wuerthner.cwn.sample.SampleTrack;
import org.wuerthner.cwn.score.*;
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
		ScoreParameter scoreParameter = new ScoreParameter(PPQ, D1 / 16, 1,4, Score.SPLIT_RESTS,
				Arrays.asList(new DurationType[] { DurationType.REGULAR, DurationType.DOTTED, DurationType.BIDOTTED, DurationType.TRIPLET, DurationType.QUINTUPLET }),
				new ArrayList<>(), 0); // 4 bars
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
		ScoreParameter scoreParameter = new ScoreParameter(PPQ, D1 / 16, 1, 4, Score.NONE,
				Arrays.asList(new DurationType[] { DurationType.REGULAR, DurationType.DOTTED, DurationType.BIDOTTED, DurationType.TRIPLET, DurationType.QUINTUPLET }),
				new ArrayList<>(), 0); // 4 bars
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
		ScoreParameter scoreParameter = new ScoreParameter(PPQ, D1 / 16, 1,4, Score.SPLIT_RESTS,
				Arrays.asList(new DurationType[] { DurationType.REGULAR, DurationType.DOTTED, DurationType.BIDOTTED, DurationType.TRIPLET, DurationType.QUINTUPLET }),
				new ArrayList<>(),0); // 4 bars
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
		ScoreParameter scoreParameter = new ScoreParameter(PPQ, D1 / 16, 1, 4, Score.ALLOW_DOTTED_RESTS,
				Arrays.asList(new DurationType[] { DurationType.REGULAR, DurationType.DOTTED, DurationType.BIDOTTED, DurationType.TRIPLET, DurationType.QUINTUPLET }),
				new ArrayList<>(), 0); // 4 bars - allow dotted rests
		ScoreBuilder scoreBuilder = new ScoreBuilder(new TrackContainer(trackList, 0), scoreParameter, new SampleScoreLayout(), 1);
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

	@Test
	public void testSingleSystem() {
		CwnFactory factory = new SampleFactory();
		SimpleTimeSignature ts = new SimpleTimeSignature("4/4");
		CwnTimeSignatureEvent timeSignatureEvent = factory.createTimeSignatureEvent(0, ts);
		List<CwnTrack> trackList = new ArrayList<>();
		CwnTrack track = factory.createTrack(PPQ);

		track.addEvent(timeSignatureEvent);
		track.addEvent(factory.createKeyEvent(0, 0));
		track.addEvent(factory.createClefEvent(0, 0));
		// bar 1
		track.addEvent(factory.createNoteEvent(0, D4, 78, 0, 0, 0));
		track.addEvent(factory.createNoteEvent(D4, D4, 80, 0, 0, 0));
		track.addEvent(factory.createNoteEvent(D2, D4, 81, 0, 0, 0));
		// bar 2
		track.addEvent(factory.createNoteEvent(D1, D2, 83, 0, 0, 0));
		track.addEvent(factory.createNoteEvent(D1+D2, D2, 85, 0, 0, 0));
		// bar 3
		track.addEvent(factory.createNoteEvent(2*D1+D2, D4, 87, 0, 0, 0));
		trackList.add(track);
		ScoreParameter scoreParameter = new ScoreParameter(PPQ, D1 / 16, 1, 4, Score.ALLOW_DOTTED_RESTS,
				Arrays.asList(new DurationType[] { DurationType.REGULAR, DurationType.DOTTED, DurationType.BIDOTTED, DurationType.TRIPLET, DurationType.QUINTUPLET }),
				new ArrayList<>(), 0); // 4 bars - allow dotted rests
		System.out.println("sp1: " + scoreParameter.getBarOffset() + ", " + scoreParameter.startPosition + "-" + scoreParameter.endPosition);
		TrackContainer container = new TrackContainer(trackList, 0);
		ScoreBuilder scoreBuilder = new ScoreBuilder(container, scoreParameter, new SampleScoreLayout(), 1);
		System.out.println("sp2: " + scoreParameter.getBarOffset() + ", " + scoreParameter.startPosition + "-" + scoreParameter.endPosition);

		// scoreBuilder.update(ScoreUpdate.FULL.setTrack(track));

		ScoreSystem system = scoreBuilder.getSystem(0);
		ScoreStaff staff = system.get(0);
		int barListSize = staff.getBarListSize();
		assertEquals(12, barListSize );

		// bar 1
		ScoreBar origBar1 = staff.getBar(0);
		{
			assertEquals(origBar1.getVoice(0).getScoreObjectSet().size(), 4);
			List<String> list = origBar1.getVoice(0).getScoreObjectSet().stream().map(obj -> obj.getStartPosition() + ":" + obj.getDuration() + ":" + obj.getAveragePitch()).collect(Collectors.toList());
			assertEquals(list.get(0), "0:384:78.0");
			assertEquals(list.get(1), "384:384:80.0");
			assertEquals(list.get(2), "768:384:81.0");
			assertEquals(list.get(3), "1152:384:0.0");
		}
		// bar 2
		ScoreBar origBar2 = staff.getBar(1);
		{
			assertEquals(origBar2.getVoice(0).getScoreObjectSet().size(), 2);
			List<String> list = origBar2.getVoice(0).getScoreObjectSet().stream().map(obj -> obj.getStartPosition() + ":" + obj.getDuration() + ":" + obj.getAveragePitch()).collect(Collectors.toList());
			assertEquals(list.get(0), "1536:768:83.0");
			assertEquals(list.get(1), "2304:768:85.0");
		}
		// bar 3
		ScoreBar origBar3 = staff.getBar(2);
		{
			assertEquals(origBar3.getVoice(0).getScoreObjectSet().size(), 3);
			List<String> list = origBar3.getVoice(0).getScoreObjectSet().stream().map(obj -> obj.getStartPosition() + ":" + obj.getDuration() + ":" + obj.getAveragePitch()).collect(Collectors.toList());
			assertEquals(list.get(0), "3072:768:0.0");
			assertEquals(list.get(1), "3840:384:87.0");
			assertEquals(list.get(2), "4224:384:0.0");
		}
		// bar 4
		ScoreBar origBar4 = staff.getBar(3);
		{
			assertEquals(origBar4.getVoice(0).getScoreObjectSet().size(), 1);
			List<String> list = origBar4.getVoice(0).getScoreObjectSet().stream().map(obj -> obj.getStartPosition() + ":" + obj.getDuration() + ":" + obj.getAveragePitch()).collect(Collectors.toList());
			assertEquals(list.get(0), "4608:1536:0.0");
		}
		// bar 5
		ScoreBar origBar5 = staff.getBar(4);
		{
			assertEquals(origBar5.getVoice(0).getScoreObjectSet().size(), 1);
			List<String> list = origBar5.getVoice(0).getScoreObjectSet().stream().map(obj -> obj.getStartPosition() + ":" + obj.getDuration() + ":" + obj.getAveragePitch()).collect(Collectors.toList());
			assertEquals(list.get(0), "6144:1536:0.0");
		}
		// bar 6
		ScoreBar origBar6 = staff.getBar(5);
		{
			assertEquals(origBar6.getVoice(0).getScoreObjectSet().size(), 1);
			List<String> list = origBar6.getVoice(0).getScoreObjectSet().stream().map(obj -> obj.getStartPosition() + ":" + obj.getDuration() + ":" + obj.getAveragePitch()).collect(Collectors.toList());
			assertEquals(list.get(0), "7680:1536:0.0");
		}

		//
		// NOW: change bar 2
		//
		CwnNoteEvent note = track.getList(CwnNoteEvent.class).stream().filter(n -> n.getPitch() == 85).findAny().get();
		((SampleTrack)track).removeEvent(note);
		track.addEvent(factory.createNoteEvent(D1+D2+D4, D2, 86, 0, 0, 0));

		scoreBuilder.update(new ScoreUpdate(track, 1536, 2*1536));

		ScoreBar changedBar1 = scoreBuilder.getSystem(0).get(0).getBar(0);
		ScoreBar changedBar2 = scoreBuilder.getSystem(0).get(0).getBar(1);
		ScoreBar changedBar3 = scoreBuilder.getSystem(0).get(0).getBar(2);
		ScoreBar changedBar4 = scoreBuilder.getSystem(0).get(0).getBar(3);
		ScoreBar changedBar5 = scoreBuilder.getSystem(0).get(0).getBar(4);

		List<String> dev1 = deviations(track, 0, origBar1, changedBar1);
		List<String> dev2 = deviations(track, 1, origBar2, changedBar2);
		List<String> dev3 = deviations(track, 2, origBar3, changedBar3);
		List<String> dev4 = deviations(track, 3, origBar4, changedBar4);
		List<String> dev5 = deviations(track, 4, origBar5, changedBar5);

		assertEquals(0, dev1.size());
		assertEquals(5, dev2.size(), 5);
		assertEquals("bar: 2, voice: 1 sizes differ (original|changed): 2|3", dev2.get(0));
		assertEquals(5, dev3.size());
		assertEquals("bar: 3, voice: 1 sizes differ (original|changed): 3|4", dev3.get(0));
		assertEquals(0, dev4.size() );
		assertEquals(0, dev5.size());

		//
		// NOW: set barOffset to 2
		//
		System.out.println("-----------------------");
		System.out.println("sp: " + scoreParameter.getBarOffset() + ", " + scoreParameter.startPosition + "-" + scoreParameter.endPosition);
		System.out.println("bld: " + scoreBuilder.getSystem(0).get(0).getBarListSize());
		int offset = 0;
//		scoreBuilder.getScoreParameter().setBarOffset(offset);
//		container.setBarOffset(offset);
//		// scoreBuilder = new ScoreBuilder(container, scoreParameter, new SampleScoreLayout(), 1);
//		scoreBuilder.update(new ScoreUpdate(ScoreUpdate.Type.RELAYOUT));

		// reference:
//		==> clef0: 145, 87
//		==> head4: 203, 93 - D4 78
//		==> sign1: 193, 84
//		==> head4: 263, 90 - D4 80
//		==> sign1: 253, 81
//		==> head4: 323, 87 - D4 81
//		==> rest4: 382, 96 - R4

//		==> head2: 455, 84 - D2 83
//		==> rest4: 574, 96 - R4
//		==> head4: 635, 78 - D4 86

//		==> head4: 707, 78 - D4 86
//		==> rest4: 766, 96 - R4
//		==> head4: 827, 78 - D4 87
//		==> sign1: 817, 69
//		==> rest4: 886, 96 - R4

		TestScoreCanvas scoreCanvas = new TestScoreCanvas();
		ScoreLayout scoreLayout = new SampleScoreLayout();
		ScorePresenter scorePresenter = new ScorePresenter(scoreBuilder, scoreCanvas, scoreLayout, new TestSelection());
		scorePresenter.present("title", "subtitle", "composer", offset);
//		System.out.println("* " + scoreCanvas.head2);
//		System.out.println("* " + scoreCanvas.head4);
//		System.out.println("* " + scoreCanvas.rest4);
	}

	private List<String> deviations(CwnTrack cwnTrack, int barNo, ScoreBar firstBar, ScoreBar secondBar) {
		List<String> deviationList = new ArrayList<>();
		if (firstBar.size() != secondBar.size()) {
			deviationList.add("bar " + (barNo + 1) + " number of voices differ (original|changed): " + firstBar.size() + "|" + secondBar.size());
		}
		if (firstBar.getStartPosition() != secondBar.getStartPosition()) {
			deviationList.add("bar " + (barNo + 1) + " start positions differ (original|changed): " + PositionTools.getTrias(cwnTrack, firstBar.getStartPosition()) + "|"
					+ PositionTools.getTrias(cwnTrack, secondBar.getStartPosition()));
		}
		if (firstBar.getEndPosition() != secondBar.getEndPosition()) {
			deviationList.add("bar " + (barNo + 1) + " end positions differ (original|changed): " + PositionTools.getTrias(cwnTrack, firstBar.getEndPosition()) + "|"
					+ PositionTools.getTrias(cwnTrack, secondBar.getEndPosition()));
		}
		if (firstBar.getDuration() != secondBar.getDuration()) {
			deviationList.add("bar " + (barNo + 1) + " durations differ (original|changed): " + firstBar.getDuration() + "|" + secondBar.getDuration());
		}

		int voiceNo = 0;
		Iterator<ScoreVoice> secondVoiceInterator = secondBar.iterator();
		for (ScoreVoice firstVoice : firstBar) {
			ScoreVoice secondVoice = secondVoiceInterator.next();
			if (firstVoice.size() != secondVoice.size()) {
				deviationList.add("bar: " + (barNo + 1) + ", voice: " + (voiceNo + 1) + " sizes differ (original|changed): " + firstVoice.size() + "|" + secondVoice.size());
			} else {
				Iterator<ScoreObject> secondIterator = secondVoice.iterator();
				for (ScoreObject firstObject : firstVoice) {
					ScoreObject secondObject = secondIterator.next();
					if (firstObject.getStartPosition() != secondObject.getStartPosition()) {
						deviationList.add(
								"In bar: " + (barNo + 1) + ", voice: " + (voiceNo + 1) + " object start positions differ (original|changed): " + firstObject.toString(cwnTrack) + "|" + secondObject.toString(cwnTrack));
					}
					if (firstObject.getEndPosition() != secondObject.getEndPosition()) {
						deviationList.add(
								"In bar: " + (barNo + 1) + ", voice: " + (voiceNo + 1) + " object end positions differ (original|changed): " + firstObject.toString(cwnTrack) + "|" + secondObject.toString(cwnTrack));
					}
					if (firstObject.getDuration() != secondObject.getDuration()) {
						deviationList
								.add("In bar: " + (barNo + 1) + ", voice: " + (voiceNo + 1) + " object durations differ (original|changed): " + firstObject.toString(cwnTrack) + "|" + secondObject.toString(cwnTrack));
					}
				}
			}
			voiceNo++;
		}
		if (!deviationList.isEmpty()) {
			deviationList.add("--- Original ---");
			deviationList.add(firstBar.toString());
			deviationList.add("--- Changed ---");
			deviationList.add(secondBar.toString());
		} else if (false) {
			System.out.println(firstBar.toString());
		}
		return deviationList;
	}

	private class TestScoreCanvas implements ScoreCanvas {
		public int head2 = 0;
		public int head4 = 0;
		public int rest4 = 0;
		public int other = 0;

		public void open() {
		}
		public void drawLine(int x1, int y1, int x2, int y2) {
		}
		public void drawString(String string, String fontName, int x, int y, String align) {
		}
		public void drawImage(String string, int x, int y, boolean alternative) {
			String n = (string.equals("head4") ? "D4" : string.equals("head2") ? "D2" : string.equals("rest4") ? "r4" : "");
			System.out.println("==> " + string + ": " + x + ", " + y + " " + n);
			switch (string) {
				case "head2": head2++; break;
				case "head4": head4++; break;
				case "rest4": rest4++; break;
				default: other++;
			}
		}
		public void drawDot(int d, int yBase) {
		}
		public void drawDot(int d, int yBase, int color) {
		}
		public void drawLine(int x1, int y1, int x2, int y2, String color) {
		}
		public void drawLine(int x1, int y1, int x2, int y2, int width) {
		}
		public void drawLine(int x1, int y, int x2, int y2, boolean alternative) {
		}
		public void close() {
		}
		public void drawRect(int xPositionLeft, int yPositionTop, int xPositionRight, int yPositionBottom) {
		}
		public boolean outsideDrawArea() {
			return false;
		}
		public void drawArc(int x1, int y1, int x2, int y2, int direction, int delta, boolean alternative) {
		}
	}
}
