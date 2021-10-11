package org.wuerthner.cwn.midi;

import org.junit.Test;
import org.wuerthner.cwn.api.*;
import org.wuerthner.cwn.sample.SampleFactory;
import org.wuerthner.cwn.score.ScoreBar;
import org.wuerthner.cwn.score.ScoreBuilder;
import org.wuerthner.cwn.score.ScoreNote;
import org.wuerthner.cwn.timesignature.SimpleTimeSignature;

import java.io.*;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertTrue;

public class MidiTest {
    int PPQ = 384;
    int D1 = 4 * PPQ;
    int D2 = 2 * PPQ;
    int D4 = PPQ;
    int D8 = (int) (0.5 * PPQ);

    @Test
    public void testWriteMidiFile() {
        CwnFactory factory = new SampleFactory();
        CwnNoteEvent noteEvent1 = factory.createNoteEvent(0, D8, 78, 0, 90, 0);
        CwnNoteEvent noteEvent2 = factory.createNoteEvent(D8, D8, 80, 0, 90, 0);
        CwnNoteEvent noteEvent3 = factory.createNoteEvent(D2, D2, 82, 0, 90, 0);
        CwnNoteEvent noteEvent4 = factory.createNoteEvent(D2*2, D2*2, 94, 0, 90, 0);
        ScoreParameter scoreParameter = new ScoreParameter(PPQ, D1 / 8, 1,4,0,
                Arrays.asList(new DurationType[] { DurationType.REGULAR, DurationType.DOTTED, DurationType.BIDOTTED, DurationType.TRIPLET, DurationType.QUINTUPLET }),
                false,
                4);
        CwnTrack track = factory.createTrack(PPQ);
        track.addEvent(factory.createTimeSignatureEvent(0, new SimpleTimeSignature("4/4")));
        track.addEvent(factory.createKeyEvent(0, 0));
        track.addEvent(factory.createClefEvent(0, 0));
        track.addEvent(factory.createTempoEvent(0, 100));
        track.addEvent(noteEvent1);
        track.addEvent(noteEvent2);
        track.addEvent(noteEvent3);
        track.addEvent(noteEvent4);

        List<CwnTrack> list = Arrays.asList(track);
        MidiWriter writer = new MidiWriter(list);
        OutputStream os = new ByteArrayOutputStream();
        writer.writer(os, 0,D2*4);
        String output = os.toString();
        assertTrue(output.startsWith("MThd"));
        // assertTrue(output.length()==71);
        try (FileOutputStream fos = new FileOutputStream(new File("example.mid"))) {
            writer.writer(fos, 0,D2*4);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }


    }
}
