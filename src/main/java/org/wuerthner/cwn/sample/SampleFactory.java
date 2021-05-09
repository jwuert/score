package org.wuerthner.cwn.sample;

import org.wuerthner.cwn.api.CwnAccent;
import org.wuerthner.cwn.api.CwnBarEvent;
import org.wuerthner.cwn.api.CwnClefEvent;
import org.wuerthner.cwn.api.CwnFactory;
import org.wuerthner.cwn.api.CwnKeyEvent;
import org.wuerthner.cwn.api.CwnNoteEvent;
import org.wuerthner.cwn.api.CwnTimeSignatureEvent;
import org.wuerthner.cwn.api.CwnTrack;
import org.wuerthner.cwn.api.TimeSignature;

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
