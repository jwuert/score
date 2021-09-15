package org.wuerthner.cwn.midi;

import org.wuerthner.cwn.api.CwnEvent;
import org.wuerthner.cwn.api.CwnNoteEvent;
import org.wuerthner.cwn.api.CwnTempoEvent;
import org.wuerthner.cwn.api.CwnTrack;
import org.wuerthner.cwn.midi.event.MidiEvent;
import org.wuerthner.cwn.midi.event.ProgramChange;
import org.wuerthner.cwn.midi.event.meta.MetaEvent;
import org.wuerthner.cwn.midi.event.meta.Tempo;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

public class MidiWriter {
    private final List<? extends CwnTrack> trackList;
    public MidiWriter(List<? extends CwnTrack> trackList) {
        this.trackList = trackList;
    }

    public void writer(OutputStream os, long startPosition, long endPosition) {
        double tempoFactor = 2.0;
        double volumeWeight = 1.0;

        List<MidiTrack> midiTrackList = new ArrayList<>();
        MidiTrack tempoTrack = new MidiTrack();
        midiTrackList.add(tempoTrack);
        for (CwnTrack cwnTrack : trackList) {
            int program = 0;// track.getInstrument(); // TODO
            int channel = 0;//track.getChannel(); // TODO
            MidiTrack midiTrack = new MidiTrack();
            midiTrackList.add(midiTrack);
            midiTrack.insertEvent(createInstrumentEvent(program, channel));
            for (CwnEvent event : cwnTrack.getList(CwnEvent.class)) {
                long end = event.getPosition() + event.getDuration() - 1;
                if (endPosition == 0 || end <= endPosition) {
                    if (event instanceof CwnTempoEvent) {
                        CwnTempoEvent tempoEvent = CwnTempoEvent.class.cast(event);
                        long start = tempoEvent.getPosition() - startPosition;
                        if (start < 0) {
                            start = 0;
                        }
                        tempoTrack.insertEvent(createTempoEvent(start, (int) (tempoFactor * 20000000.0 / tempoEvent.getTempo())));
                    } else if (event instanceof CwnNoteEvent) {
                        CwnNoteEvent noteEvent = CwnNoteEvent.class.cast(event);
                        long start = noteEvent.getPosition();
                        // long end = noteEvent.getEnd() - 1;
                        if (start >= startPosition && (endPosition == 0 || end <= endPosition)) {
                            int pitch = noteEvent.getPitch();
                            int velocity = (int) (noteEvent.getVelocity() * volumeWeight);
                            if (velocity > 127) {
                                velocity = 127;
                            }
//                            midiTrack.add(createNoteOnEvent(pitch, start - startPosition, velocity, channel));
//                            midiTrack.add(createNoteOffEvent(pitch, end - startPosition, channel));
                            midiTrack.insertNote(channel, pitch, velocity, start - startPosition, end-start);
                        }
                    }
                }
            }
        }

        MidiFile midi = new MidiFile(MidiFile.DEFAULT_RESOLUTION, midiTrackList);

        try {
            midi.writeToOutputStream(os);
        } catch(IOException e) {
            throw new RuntimeException(e);
        }
    }

    private MidiEvent createTempoEvent(long start, int tempo) {
        MidiEvent event = new Tempo(start, 0, tempo);
        return event;
    }

    private MidiEvent createInstrumentEvent(int program, int channel) {
        MidiEvent event = new ProgramChange(0, channel, program);
        return event;
    }
}
