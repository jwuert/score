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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class DurationTest {

    @Test
    public void testDuration() {
        ScoreParameter scoreParameter = new ScoreParameter(960, 240, 1,4, Score.SPLIT_RESTS | Score.ALLOW_DOTTED_RESTS,
                Arrays.asList(DurationType.REGULAR, DurationType.DOTTED, DurationType.BIDOTTED, DurationType.TRIPLET, DurationType.QUINTUPLET),
                new ArrayList<>(), 0, 0);
        QuantizedDuration qdur = new QuantizedDuration(scoreParameter, 1200);
        int[] md;

        md = qdur.getMinDelta(DurationType.REGULAR, scoreParameter, 1200);
        assertEquals(0, md[0]);
        assertEquals(960, md[1]);
        assertEquals(2, md[2]);

        md = qdur.getMinDelta(DurationType.TRIPLET, scoreParameter, 1200);
        assertEquals(80, md[0]);
        assertEquals(1280, md[1]);
        assertEquals(3, md[2]);

        md = qdur.getMinDelta(DurationType.QUINTUPLET, scoreParameter, 1200);
        assertEquals(336, md[0]);
        assertEquals(1536, md[1]);
        assertEquals(3, md[2]);

        md = qdur.getMinDelta(DurationType.DOTTED, scoreParameter, 1200);
        assertEquals(240, md[0]);
        assertEquals(1440, md[1]);
        assertEquals(2, md[2]);
    }

    @Test
    public void testPosition() {
        ScoreParameter scoreParameter = new ScoreParameter(960, 240, 1,4, Score.SPLIT_RESTS | Score.ALLOW_DOTTED_RESTS,
                Arrays.asList(DurationType.REGULAR, DurationType.DOTTED, DurationType.BIDOTTED, DurationType.TRIPLET, DurationType.QUINTUPLET),
                new ArrayList<>(), 0, 0);
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
        assertEquals(0l, qpos.getSnappedPosition());
        assertEquals(DurationType.REGULAR, qpos.getType());
        QuantizedDuration qdur = new QuantizedDuration(scoreParameter, 1200, qpos.getType());
        assertEquals(960, qdur.getSnappedDuration());
        assertEquals(DurationType.REGULAR, qdur.getType());
        assertEquals(2, qdur.getPower());
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
                new ArrayList<>(), 0, 0); // 4, Score.SPLIT_RESTS | Score.ALLOW_DOTTED_RESTS, 0); // 4 bars
        ScoreBuilder scoreBuilder = new ScoreBuilder(new TrackContainer(trackList, 0), scoreParameter, new SampleScoreLayout(), 1);
        Iterator<ScoreBar> barIterator = scoreBuilder.iterator().next().iterator().next().iterator();
        ScoreVoice voice = barIterator.next().iterator().next();
        assertEquals(4, voice.size());
        for (ScoreObject scoreObject : voice) {
            System.out.println(scoreObject);
        }
        Iterator<ScoreObject> voiceIterator = voice.iterator();
        {
            ScoreObject so = voiceIterator.next();
            assertTrue(so.isChord());
            assertEquals(0, so.getStartPosition());
            assertEquals(960, so.getDuration());
            assertEquals(DurationType.REGULAR, so.getDurationType());
        }
        {
            ScoreObject so = voiceIterator.next();
            assertTrue(so.isChord());
            assertEquals(960, so.getStartPosition());
            assertEquals(240, so.getDuration());
            assertEquals(DurationType.REGULAR, so.getDurationType());
        }
        {
            ScoreObject so = voiceIterator.next();
            assertTrue(so.isRest());
            assertEquals(1200, so.getStartPosition());
            assertEquals(720, so.getDuration());
            assertEquals(DurationType.DOTTED, so.getDurationType());
        }
        {
            ScoreObject so = voiceIterator.next();
            assertTrue(so.isRest());
            assertEquals(1920, so.getStartPosition());
            assertEquals(1920, so.getDuration());
            assertEquals(DurationType.REGULAR, so.getDurationType());
        }
    }
}
