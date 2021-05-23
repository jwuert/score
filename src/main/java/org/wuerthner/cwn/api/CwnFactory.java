package org.wuerthner.cwn.api;

public interface CwnFactory {
	public CwnTrack createTrack(int ppq);
	
	public CwnTimeSignatureEvent createTimeSignatureEvent(long position, TimeSignature timeSignature);
	
	public CwnKeyEvent createKeyEvent(long position, int key);
	
	public CwnClefEvent createClefEvent(long position, int clef);
	
	public CwnNoteEvent createNoteEvent(long position, long duration, int pitch, int shift, int velocity, int voice);
	
	public CwnNoteEvent createNoteEvent(long position, long duration, int pitch, int shift, int velocity, int voice, String lyrics);

	public CwnTempoEvent createTempoEvent(long position, int tempo);

	public CwnBarEvent createBarEvent(long position, String type);
	
	public CwnAccent createAccent(String name);

}
