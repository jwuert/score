package org.wuerthner.cwn.score;

import java.util.*;

import org.junit.Ignore;
import org.junit.Test;
import org.wuerthner.cwn.api.*;
import org.wuerthner.cwn.position.PositionTools;
import org.wuerthner.cwn.sample.SampleFactory;
import org.wuerthner.cwn.sample.SampleScoreLayout;
import org.wuerthner.cwn.timesignature.SimpleTimeSignature;

import static org.junit.Assert.assertEquals;

public class GroupTest {
	
	int PPQ = 384;
	int D1 = 4 * PPQ;
	int D4 = PPQ;
	int D8 = (int) (0.5 * PPQ);
	int D16 = (int) (0.25 * PPQ);
	int D2T = (int) (PPQ * 4.0 / 3);
	int D4T = (int) (PPQ * 2.0 / 3);
	int D8T = (int) (0.5 * PPQ * 2.0 / 3);
	
	@Test
	public void testGroups() {
		CwnFactory factory = new SampleFactory();
		SimpleTimeSignature ts1 = new SimpleTimeSignature("4/4");
		CwnTimeSignatureEvent timeSignatureEvent1 = factory.createTimeSignatureEvent(0, ts1);
		CwnKeyEvent keyEvent = factory.createKeyEvent(0,0);
		CwnClefEvent clefEvent = factory.createClefEvent(0, 0);
		List<CwnTrack> trackList = new ArrayList<>();
		CwnTrack track = factory.createTrack(PPQ);
		track.addEvent(timeSignatureEvent1);
		track.addEvent(keyEvent);
		track.addEvent(clefEvent);
		track.addEvent(factory.createNoteEvent(PositionTools.getPosition(track, "1.1:8"), D16, 78, 0, 0, 0));
		track.addEvent(factory.createNoteEvent(PositionTools.getPosition(track, "1.1:8+16"), D16, 78, 0, 0, 0));
		track.addEvent(factory.createNoteEvent(PositionTools.getPosition(track, "1.2.0"), D16, 78, 0, 0, 0));
		track.addEvent(factory.createNoteEvent(PositionTools.getPosition(track, "1.2:16"), D16, 78, 0, 0, 0));
		track.addEvent(factory.createNoteEvent(PositionTools.getPosition(track, "1.2:8"), D16, 78, 0, 0, 0));
		//
		track.addEvent(factory.createNoteEvent(PositionTools.getPosition(track, "1.4.0"), D8T, 78, 0, 0, 0));
		track.addEvent(factory.createNoteEvent(PositionTools.getPosition(track, "1.4:8T"), D8T, 78, 0, 0, 0));
		//
		track.addEvent(factory.createNoteEvent(PositionTools.getPosition(track, "2.1.0"), D8, 78, 0, 0, 0));
		track.addEvent(factory.createNoteEvent(PositionTools.getPosition(track, "2.1:8"), D4, 78, 0, 0, 0));
		track.addEvent(factory.createNoteEvent(PositionTools.getPosition(track, "2.2:8"), D16, 78, 0, 0, 0));
		track.addEvent(factory.createNoteEvent(PositionTools.getPosition(track, "2.2:8+16"), D16, 78, 0, 0, 0));
		//
		track.addEvent(factory.createNoteEvent(PositionTools.getPosition(track, "2.3.0"), D4T, 78, 0, 0, 0));
		track.addEvent(factory.createNoteEvent(PositionTools.getPosition(track, "2.3.0"), D4T, 80, 0, 0, 0));
		track.addEvent(factory.createNoteEvent(PositionTools.getPosition(track, "2.3:4T"), D4T, 78, 0, 0, 0));
		track.addEvent(factory.createNoteEvent(PositionTools.getPosition(track, "2.3:4T"), D4T, 82, 0, 0, 0));
		track.addEvent(factory.createNoteEvent(PositionTools.getPosition(track, "2.3:2T"), D4T, 78, 0, 0, 0));
		track.addEvent(factory.createNoteEvent(PositionTools.getPosition(track, "2.3:2T"), D4T, 83, 0, 0, 0));
		
		trackList.add(track);
		ScoreParameter scoreParameter = new ScoreParameter(PPQ, D1 / 16, 1,4, Score.SPLIT_RESTS | Score.ALLOW_DOTTED_RESTS,
				Arrays.asList(new DurationType[] { DurationType.REGULAR, DurationType.DOTTED, DurationType.BIDOTTED, DurationType.TRIPLET, DurationType.QUINTUPLET }),
				new ArrayList<>(), 0, 0, new HashMap<>()); // 4, Score.SPLIT_RESTS | Score.ALLOW_DOTTED_RESTS, 0); // 4 bars
		ScoreBuilder scoreBuilder = new ScoreBuilder(new TrackContainer(trackList, 0), scoreParameter, new SampleScoreLayout());
		assertEquals("[1.0]", ts1.getMetric().getFlatDurationList(0).toString());
		assertEquals("[0.5, 0.5]", ts1.getMetric().getFlatDurationList(1).toString());
		assertEquals("[0.25, 0.25, 0.25, 0.25]", ts1.getMetric().getFlatDurationList(2).toString());
		assertEquals("[0.25, 0.25, 0.25, 0.25]", ts1.getMetric().getFlatDurationList().toString());
		assertEquals("[1:4, 1:4, 1:4, 1:4]", ts1.getMetric().getFlatMetricList().toString());
		//
		// bar 1
		//
		Iterator<ScoreBar> barIterator = scoreBuilder.iterator().next().iterator().next().iterator();
		ScoreVoice voice = barIterator.next().iterator().next();
		// for (ScoreGroup group : voice.getGroups()) {			System.out.println(group.toString());		}
		// for (CharacterGroup group : voice.getCharacterGroups()) { 			System.out.println(group);		}
		assertEquals(2, voice.getGroups().size());
		Iterator<ScoreGroup> voiceGroupIterator = voice.getGroups().iterator();
		ScoreGroup grp1 = voiceGroupIterator.next();
		List<ScoreGroup> subGroups1 = grp1.getSubGroups();
		assertEquals(7, grp1.size());
		assertEquals("1:1", grp1.getCharacterPresentation());
		assertEquals(0.5, grp1.getRelativeDuration(), 0);
		assertEquals(0, grp1.getRelativeStartPosition(), 0);
		assertEquals(3, subGroups1.size());
		assertEquals(1, subGroups1.get(0).size());
		assertEquals(0, subGroups1.get(0).getRelativeStartPosition(), 0);
		assertEquals(0.125, subGroups1.get(0).getRelativeDuration(), 0);
		assertEquals(5,  subGroups1.get(1).size());
		assertEquals(0.125, subGroups1.get(1).getRelativeStartPosition(), 0);
		assertEquals(0.3125, subGroups1.get(1).getRelativeDuration(), 0);
		assertEquals(1, subGroups1.get(2).size());
		assertEquals(0.4375, subGroups1.get(2).getRelativeStartPosition(), 0);
		assertEquals(0.0625, subGroups1.get(2).getRelativeDuration(), 0);

		ScoreGroup grp2 = voiceGroupIterator.next();
		List<ScoreGroup> subGroups2 = grp2.getSubGroups();
		assertEquals(0.5, grp2.getRelativeStartPosition(), 0);
		assertEquals(0.5, grp2.getRelativeDuration(), 0.001);
		assertEquals(3, subGroups2.size());
		assertEquals(1, subGroups2.get(0).size());
		assertEquals(0.5, subGroups2.get(0).getRelativeStartPosition(), 0);
		assertEquals(0.25, subGroups2.get(0).getRelativeDuration(), 0);
		assertEquals(2, subGroups2.get(1).size());
		assertEquals(0.75, subGroups2.get(1).getRelativeStartPosition(), 0);
		assertEquals(0.16666, subGroups2.get(1).getRelativeDuration(), 0.001);
		assertEquals(1, subGroups2.get(2).size());
		assertEquals(0.9166666, subGroups2.get(2).getRelativeStartPosition(), 0.001);
		assertEquals(0.083333, subGroups2.get(2).getRelativeDuration(), 0.001);
		//
		// Bar 2
		//
		voice = barIterator.next().iterator().next();
		// for (ScoreGroup group : voice.getGroups()) {			System.out.println(group.toString());		}
		// for (CharacterGroup group : voice.getCharacterGroups()) {			System.out.println(group);		}
		Set<ScoreGroup> groups = voice.getGroups();
		assertEquals(2, groups.size());
		Iterator<ScoreGroup> voiceGroupIterator2 = voice.getGroups().iterator();
		ScoreGroup grp3 = voiceGroupIterator2.next();
		List<ScoreGroup> subGroups3 = grp3.getSubGroups();
		assertEquals(3, subGroups3.size());
		assertEquals(1, subGroups3.get(0).size());
		assertEquals(0, subGroups3.get(0).getRelativeStartPosition(), 0);
		assertEquals(0.125, subGroups3.get(0).getRelativeDuration(), 0);
		assertEquals(1, subGroups3.get(1).size());
		assertEquals(0.125, subGroups3.get(1).getRelativeStartPosition(), 0);
		assertEquals(0.25, subGroups3.get(1).getRelativeDuration(), 0);
		assertEquals(2, subGroups3.get(2).size());
		assertEquals(0.375, subGroups3.get(2).getRelativeStartPosition(), 0);
		assertEquals(0.125, subGroups3.get(2).getRelativeDuration(), 0);
		ScoreGroup grp4 = voiceGroupIterator2.next();
		List<ScoreGroup> subGroups4 = grp4.getSubGroups();
		assertEquals(3, subGroups4.size());
		assertEquals(1, subGroups4.get(0).size());
		assertEquals(0.5, subGroups4.get(0).getRelativeStartPosition(), 0);
		assertEquals(0.166666, subGroups4.get(0).getRelativeDuration(), 0.001);
		assertEquals(1, subGroups4.get(1).size());
		assertEquals(0.666666, subGroups4.get(1).getRelativeStartPosition(), 0.001);
		assertEquals(0.166666, subGroups4.get(1).getRelativeDuration(), 0.001);
		assertEquals(1, subGroups4.get(2).size());
		assertEquals(0.833333, subGroups4.get(2).getRelativeStartPosition(), 0.001);
		assertEquals(0.166666, subGroups4.get(2).getRelativeDuration(), 0.001);
	}
}
