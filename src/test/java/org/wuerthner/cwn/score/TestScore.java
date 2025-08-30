package org.wuerthner.cwn.score;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.junit.Test;
import org.wuerthner.cwn.api.*;
import org.wuerthner.cwn.sample.SampleFactory;
import org.wuerthner.cwn.sample.SampleScoreLayout;
import org.wuerthner.cwn.timesignature.SimpleTimeSignature;

import static org.junit.Assert.*;

public class TestScore {
	public final static String TEST = "";
	public final static String SCORE_FILE_PATH = "src/test/resources/tests/";
	public final static String SCORE_FILE_EXTENSION = TEST + "score";
	
	@Test
	public void scoreTest() throws IOException {
		try (Stream<Path> stream = Files.walk(Paths.get(SCORE_FILE_PATH))) {
			stream.filter(p -> p.getFileName().toString().endsWith(SCORE_FILE_EXTENSION)).forEach((path) -> {
				try (Stream<String> steam = Files.lines(path)) {
					Map<String, List<String>> map = steam.filter(s -> s.trim().length() > 0).filter(s -> !s.startsWith("#"))
							.collect(Collectors.groupingBy(s -> s.split(":", 2)[0], Collectors.mapping(s -> s.split(":", 2)[1].trim(), Collectors.toList())));
					ScoreTestSuite test = new ScoreTestSuite(path.getFileName().toString(), map);
					List<String> deviations = test.check();
					assertTrue("Check failed in: " + test.getDescription() + ": " + deviations.stream().collect(Collectors.joining(", ")), deviations.isEmpty());
				} catch (Exception e) {
					System.err.println(e + " in " + path);
					e.printStackTrace();
					fail(e.toString() + " in " + path);
				}
			});
		} catch (Exception e) {
			System.err.println(e);
			fail(e.toString());
		}
	}

	@Test
	public void testOverlap() {
		CwnFactory factory = new SampleFactory();
		ScoreParameter scoreParameter = new ScoreParameter(960, 480, 1,4, 0,
				Arrays.asList(new DurationType[] { DurationType.REGULAR, DurationType.DOTTED, DurationType.BIDOTTED, DurationType.TRIPLET, DurationType.QUINTUPLET }),
				new ArrayList<>(), 0, 0);
		CwnTrack track = factory.createTrack(960);
		track.addEvent(factory.createTimeSignatureEvent(0, new SimpleTimeSignature("4/4")));
		track.addEvent(factory.createKeyEvent(0, 0));
		track.addEvent(factory.createClefEvent(0, 0));
		track.addEvent(factory.createNoteEvent(1920, 2400, 78, 0, 0, 0));

		ScoreBuilder scoreBuilder = new ScoreBuilder(new TrackContainer(Arrays.asList(track), 0), scoreParameter, new SampleScoreLayout(), 1);
		Iterator<ScoreBar> barIterator = scoreBuilder.iterator().next().iterator().next().iterator();
		ScoreVoice voiceBar1 = barIterator.next().iterator().next();

		ScoreObject scoreObject;
		Iterator<ScoreObject> scoreObjectIterator1 = voiceBar1.iterator();
		// bar 1
		scoreObject = scoreObjectIterator1.next();
		assertTrue(scoreObject.isRest() && scoreObject.getDuration()==1920);
		scoreObject = scoreObjectIterator1.next();
		assertTrue(scoreObject.isChord() && scoreObject.getDuration()==1920);
		assertTrue(scoreObject.getDurationType() == DurationType.REGULAR);
		// bar 2
		ScoreVoice voiceBar2 = barIterator.next().iterator().next();
		Iterator<ScoreObject> scoreObjectIterator2 = voiceBar2.iterator();
		scoreObject = scoreObjectIterator2.next();
		assertTrue(scoreObject.isChord() && scoreObject.getDuration()==480);
		assertTrue(scoreObject.getDurationType() == DurationType.REGULAR);
	}

	@Test
	public void testSmallTriplets() {
		CwnFactory factory = new SampleFactory();
		ScoreParameter scoreParameter = new ScoreParameter(960, 240, 1,4, 0,
				Arrays.asList(new DurationType[] { DurationType.REGULAR, DurationType.DOTTED, DurationType.BIDOTTED, DurationType.TRIPLET, DurationType.QUINTUPLET }),
				new ArrayList<>(), 0, 0);
		CwnTrack track = factory.createTrack(960);
		track.addEvent(factory.createTimeSignatureEvent(0, new SimpleTimeSignature("4/4")));
		track.addEvent(factory.createKeyEvent(0, 0));
		track.addEvent(factory.createClefEvent(0, 0));
		track.addEvent(factory.createNoteEvent(0, 480, 78, 0, 0, 0));
		track.addEvent(factory.createNoteEvent(480, 160, 79, 0, 0, 0));
		track.addEvent(factory.createNoteEvent(640, 160, 81, 0, 0, 0));
		track.addEvent(factory.createNoteEvent(800, 160, 82, 0, 0, 0));
		ScoreBuilder scoreBuilder = new ScoreBuilder(new TrackContainer(Arrays.asList(track), 0), scoreParameter, new SampleScoreLayout(), 1);
		Iterator<ScoreBar> barIterator = scoreBuilder.iterator().next().iterator().next().iterator();
		ScoreVoice voiceBar1 = barIterator.next().iterator().next();

		ScoreObject scoreObject;
		Iterator<ScoreObject> scoreObjectIterator1 = voiceBar1.iterator();
		// bar 1
		scoreObject = scoreObjectIterator1.next();
		assertTrue(scoreObject.isChord() && scoreObject.getDuration()==480);
		assertTrue(scoreObject.getDurationType() == DurationType.REGULAR);
		scoreObject = scoreObjectIterator1.next();
		assertTrue(scoreObject.isChord() && scoreObject.getDuration()==160);
		assertTrue(scoreObject.getDurationType() == DurationType.TRIPLET);
		scoreObject = scoreObjectIterator1.next();
		assertTrue(scoreObject.isChord() && scoreObject.getDuration()==160);
		assertTrue(scoreObject.getDurationType() == DurationType.TRIPLET);
	}

	@Test
	public void testCharacterGroups() {
		CwnFactory factory = new SampleFactory();
		ScoreParameter scoreParameter = new ScoreParameter(960, 240, 1,4, 0,
				Arrays.asList(new DurationType[] { DurationType.REGULAR, DurationType.DOTTED, DurationType.BIDOTTED, DurationType.TRIPLET, DurationType.QUINTUPLET }),
				new ArrayList<>(), 0, 0);
		CwnTrack track = factory.createTrack(960);
		track.addEvent(factory.createTimeSignatureEvent(0, new SimpleTimeSignature("4/4")));
		track.addEvent(factory.createKeyEvent(0, 0));
		track.addEvent(factory.createClefEvent(0, 0));
		track.addEvent(factory.createNoteEvent(0, 480, 78, 0, 0, 0));
		track.addEvent(factory.createNoteEvent(480, 160, 79, 0, 0, 0));
		track.addEvent(factory.createNoteEvent(640, 160, 81, 0, 0, 0));
		track.addEvent(factory.createNoteEvent(800, 160, 82, 0, 0, 0));
		track.addEvent(factory.createNoteEvent(960, 480, 84, 0, 0, 0));

		track.addEvent(factory.createNoteEvent(1920, 160, 84, 0, 0, 0));
		track.addEvent(factory.createNoteEvent(2080, 160, 84, 0, 0, 0));
		track.addEvent(factory.createNoteEvent(2240, 160, 84, 0, 0, 0));
		track.addEvent(factory.createNoteEvent(2400, 480, 84, 0, 0, 0));
		ScoreBuilder scoreBuilder = new ScoreBuilder(new TrackContainer(Arrays.asList(track), 0), scoreParameter, new SampleScoreLayout(), 1);
		Iterator<ScoreBar> barIterator = scoreBuilder.iterator().next().iterator().next().iterator();
		ScoreVoice voice = barIterator.next().iterator().next();

		Set<CharacterGroup> characterGroups = voice.getCharacterGroups();
		assertEquals(2, characterGroups.size());
		Iterator<CharacterGroup> cgIterator = characterGroups.iterator();
		CharacterGroup g1 = cgIterator.next();
		CharacterGroup g2 = cgIterator.next();
		assertEquals(3, g1.getCharacter());
		assertEquals("3:2", g1.getFullCharacter());
		assertEquals(0.125, g1.getRelativePosition(),0);
		assertEquals(0.125, g1.getRelativeDuration(), 0);
		assertEquals("3:2", g2.getFullCharacter());
		assertEquals(0.5, g2.getRelativePosition(),0);
		assertEquals(0.125, g2.getRelativeDuration(), 0);
	}
}
