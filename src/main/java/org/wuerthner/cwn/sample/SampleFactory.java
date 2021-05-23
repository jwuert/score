package org.wuerthner.cwn.sample;

import org.wuerthner.cwn.api.*;

public class SampleFactory implements CwnFactory {
	
	@Override
	public CwnTrack createTrack(int ppq) {
		return new SampleTrack(ppq);
	}
	
	@Override
	public CwnTimeSignatureEvent createTimeSignatureEvent(long position, TimeSignature timeSignature) {
		return new SampleTimeSignatureEvent(position, timeSignature);
	}
	
	@Override
	public CwnKeyEvent createKeyEvent(long position, int key) {
		return new SampleKeyEvent(position, key);
	}
	
	@Override
	public CwnClefEvent createClefEvent(long position, int clef) {
		return new SampleClefEvent(position, clef);
	}
	
	@Override
	public CwnNoteEvent createNoteEvent(long position, long duration, int pitch, int shift, int velocity, int voice) {
		return new SampleNoteEvent(position, duration, pitch, shift, velocity, voice);
	}
	
	public CwnNoteEvent createNoteEvent(long position, long duration, int pitch, int shift, int velocity, int voice, String lyrics) {
		return new SampleNoteEvent(position, duration, pitch, shift, velocity, voice, lyrics, false);
	}

	@Override
	public CwnTempoEvent createTempoEvent(long position, int tempo) {
		return new SampleTempoEvent(position, tempo);
	}

	public CwnNoteEvent createNoteEvent(long position, long duration, int pitch, int shift, int velocity, int voice, String lyrics, boolean isUngrouped) {
		return new SampleNoteEvent(position, duration, pitch, shift, velocity, voice, lyrics, isUngrouped);
	}
	
	@Override
	public CwnBarEvent createBarEvent(long position, String type) {
		return new SampleBarEvent(position, type);
	}
	
	@Override
	public CwnAccent createAccent(String name) {
		return new SampleAccent(name);
	}
}
