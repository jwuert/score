package org.wuerthner.cwn.score;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.junit.Ignore;
import org.junit.Test;
import org.wuerthner.cwn.api.CwnFactory;
import org.wuerthner.cwn.api.CwnTimeSignatureEvent;
import org.wuerthner.cwn.api.CwnTrack;
import org.wuerthner.cwn.api.ScoreParameter;
import org.wuerthner.cwn.position.PositionTools;
import org.wuerthner.cwn.sample.SampleFactory;
import org.wuerthner.cwn.sample.SampleScoreLayout;
import org.wuerthner.cwn.timesignature.SimpleTimeSignature;

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
	@Ignore
	public void testGroups() {
		CwnFactory factory = new SampleFactory();
		SimpleTimeSignature ts1 = new SimpleTimeSignature("4/4");
		CwnTimeSignatureEvent timeSignatureEvent1 = factory.createTimeSignatureEvent(0, ts1);
		List<CwnTrack> trackList = new ArrayList<>();
		CwnTrack track = factory.createTrack(PPQ);
		track.addEvent(timeSignatureEvent1);
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
		ScoreParameter scoreParameter = new ScoreParameter(0, 5 * PPQ * 4, PPQ, D1 / 16, 1, 4, Score.SPLIT_RESTS | Score.ALLOW_DOTTED_RESTS, 0); // 4 bars
		ScoreBuilder scoreBuilder = new ScoreBuilder(new TrackContainer(trackList, 0), scoreParameter, new SampleScoreLayout());
		System.out.println(ts1.getMetric().getFlatDurationList(0));
		System.out.println(ts1.getMetric().getFlatDurationList(1));
		System.out.println(ts1.getMetric().getFlatDurationList(2));
		System.out.println(ts1.getMetric().getFlatDurationList());
		System.out.println(ts1.getMetric().getFlatMetricList());
		System.out.println("------------- bar 1 --------------");
		Iterator<ScoreBar> barIterator = scoreBuilder.iterator().next().iterator().next().iterator();
		ScoreVoice voice = barIterator.next().iterator().next();
		for (ScoreGroup group : voice.getGroups()) {
			System.out.println(group.toString());
		}
		for (CharacterGroup group : voice.getCharacterGroups()) {
			System.out.println(group);
		}
		System.out.println("------------- bar 2 --------------");
		voice = barIterator.next().iterator().next();
		for (ScoreGroup group : voice.getGroups()) {
			System.out.println(group.toString());
		}
		for (CharacterGroup group : voice.getCharacterGroups()) {
			System.out.println(group);
		}
		
	}
}
