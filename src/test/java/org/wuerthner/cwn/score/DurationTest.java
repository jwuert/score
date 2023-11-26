package org.wuerthner.cwn.score;

import org.junit.Test;
import org.wuerthner.cwn.api.*;
import org.wuerthner.cwn.position.PositionTools;
import org.wuerthner.cwn.sample.SampleFactory;
import org.wuerthner.cwn.sample.SampleScoreLayout;
import org.wuerthner.cwn.sample.SampleTrack;
import org.wuerthner.cwn.timesignature.SimpleTimeSignature;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

public class DurationTest {

    @Test
    public void testDuration() {
        ScoreParameter scoreParameter = new ScoreParameter(960, 240, 1,4, Score.SPLIT_RESTS | Score.ALLOW_DOTTED_RESTS,
                Arrays.asList(DurationType.REGULAR, DurationType.DOTTED, DurationType.BIDOTTED, DurationType.TRIPLET, DurationType.QUINTUPLET),
                new ArrayList<>(), 0);
        System.out.println("res. in ticks: " + scoreParameter.getResolutionInTicks());
        QuantizedDuration qdur = new QuantizedDuration(scoreParameter, 1200);
        System.out.println(qdur);
        int[] md;
        System.out.println(String.format("%10s %10s %10s %10s %10s","Regular","Factor","min-delta", "snap-dur", "snap-pow"));

        md = qdur.getMinDelta(DurationType.REGULAR, scoreParameter, 1200);
        System.out.println(String.format("%10s %10f %10d %10d %10d","Regular", DurationType.REGULAR.getFactor(), md[0],md[1],md[2]));

        md = qdur.getMinDelta(DurationType.TRIPLET, scoreParameter, 1200);
        System.out.println(String.format("%10s %10f %10d %10d %10d","Triplet", DurationType.TRIPLET.getFactor(), md[0],md[1],md[2]));

        md = qdur.getMinDelta(DurationType.QUINTUPLET, scoreParameter, 1200);
        System.out.println(String.format("%10s %10f %10d %10d %10d","Quintuplet", DurationType.QUINTUPLET.getFactor(), md[0],md[1],md[2]));

        md = qdur.getMinDelta(DurationType.DOTTED, scoreParameter, 1200);
        System.out.println(String.format("%10s %10f %10d %10d %10d","Dotted", DurationType.DOTTED.getFactor(), md[0],md[1],md[2]));

//        md = qdur.getMinDelta(DurationType.HALFDOTTED, scoreParameter, 1200);
//        System.out.println(String.format("%10s %10f %10d %10d %10d","Half-Dotted", DurationType.HALFDOTTED.getFactor(), md[0],md[1],md[2]));
    }

    @Test
    public void testPosition() {
        ScoreParameter scoreParameter = new ScoreParameter(960, 240, 1,4, Score.SPLIT_RESTS | Score.ALLOW_DOTTED_RESTS,
                Arrays.asList(DurationType.REGULAR, DurationType.DOTTED, DurationType.BIDOTTED, DurationType.TRIPLET, DurationType.QUINTUPLET),
                new ArrayList<>(), 0);
        CwnFactory factory = new SampleFactory();
        SimpleTimeSignature ts1 = new SimpleTimeSignature("4/4");
        CwnTimeSignatureEvent timeSignatureEvent1 = factory.createTimeSignatureEvent(0, ts1);
        List<CwnTrack> trackList = new ArrayList<>();
        CwnTrack track = factory.createTrack(960);
        track.addEvent(timeSignatureEvent1);
        track.addEvent(factory.createKeyEvent(0, 0));
        track.addEvent(factory.createClefEvent(0, 0));
        ScoreBar bar = new ScoreBar(0, track, scoreParameter);
        QuantizedPosition qpos = new QuantizedPosition(bar, 0, 240);
        System.out.println("qpos type: " + qpos.getType());
        QuantizedDuration qdur = new QuantizedDuration(scoreParameter, 1200, qpos.getType());
        System.out.println(qdur);
    }

    @Test
    public void testNote1200() {
        CwnFactory factory = new SampleFactory();
        SimpleTimeSignature ts1 = new SimpleTimeSignature("4/4");
        CwnTimeSignatureEvent timeSignatureEvent1 = factory.createTimeSignatureEvent(0, ts1);
        List<CwnTrack> trackList = new ArrayList<>();
        CwnTrack track = factory.createTrack(960);
        track.addEvent(timeSignatureEvent1);
        track.addEvent(factory.createKeyEvent(0, 0));
        track.addEvent(factory.createClefEvent(0, 0));
        // track.addEvent(factory.createNoteEvent(0, 2160, 78, 0, 0, 0));
        track.addEvent(factory.createNoteEvent(0, 1200, 78, 0, 0, 0));
        trackList.add(track);
        ScoreParameter scoreParameter = new ScoreParameter(960, 240, 1,4, Score.SPLIT_RESTS | Score.ALLOW_DOTTED_RESTS,
                Arrays.asList(new DurationType[] { DurationType.REGULAR, DurationType.DOTTED, DurationType.BIDOTTED, DurationType.TRIPLET, DurationType.QUINTUPLET }),
                new ArrayList<>(), 0); // 4, Score.SPLIT_RESTS | Score.ALLOW_DOTTED_RESTS, 0); // 4 bars
        ScoreBuilder scoreBuilder = new ScoreBuilder(new TrackContainer(trackList, 0), scoreParameter, new SampleScoreLayout(), 1);
        Iterator<ScoreBar> barIterator = scoreBuilder.iterator().next().iterator().next().iterator();
        ScoreVoice voice = barIterator.next().iterator().next();
        for (ScoreObject scoreObject : voice) {
            System.out.println(scoreObject);
        }
    }
}
