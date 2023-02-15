package org.wuerthner.cwn.api;

import java.util.List;
import java.util.Optional;

import org.wuerthner.cwn.api.exception.TimeSignatureException;

public interface CwnTrack {
	public static final String[] VOLUMES = new String[]{"0%", "10%", "20%", "30%", "40%", "50%", "60%", "70%", "80%", "90%", "100%"};
	public static final String[] CHANNELS = new String[]{"01", "02", "03", "04", "05", "06", "07", "08", "09", "10", "11", "12", "13", "14", "15", "16"};
	public final static String[] MIDI_INSTRUMENTS = new String[] { "Acoustic Grand Piano", "Bright Acoustic Piano", "Electric grand Piano", "Honky Tonk Piano", "Eiectric Piano 1", "Electric Piano 2", "Harpsichord",
			"Clavinet", "Celesra", "Glockenspiel", "Music Box", "Vibraphone", "Marimba", "Xylophone", "Tubular bells", "Dulcimer", "Drawbar Organ", "Percussive Organ", "Rock Organ", "Church Organ", "Reed Organ",
			"Accordion", "Harmonica", "Tango Accordion", "Nylon Accustic Guitar", "Steel Acoustic Guitar", "Jazz Electric Guitar", "Clean Electric Guitar", "Muted Electric Guitar", "Overdrive Guitar", "Distorted Guitar",
			"Guitar Harmonics", "Acoustic Bass", "Electric Fingered Bass", "Electric Picked Bass", "Fretless Bass", "Slap Bass 1", "Slap Bass 2", "Syn Bass 1", "Syn Bass 2", "Violin", "Viola", "Cello", "Contrabass",
			"Tremolo Strings", "Pizzicato Strings", "Orchestral Harp", "Timpani", "String Ensemble 1", "String Ensemble 2 (Slow)", "Syn Strings 1", "Syn Strings 2", "Choir Aahs", "Voice Oohs", "Syn Choir",
			"Orchestral Hit", "Trumpet", "Trombone", "Tuba", "Muted Trumpet", "French Horn", "Brass Section", "Syn Brass 1", "Syn Brass 2", "Soprano Sax", "Alto Sax", "Tenor Sax", "Baritone Sax", "Oboe", "English Horn",
			"Bassoon", "Clarinet", "Piccolo", "Flute", "Recorder", "Pan Flute", "Bottle Blow", "Shakuhachi", "Whistle", "Ocarina", "Syn Square Wave", "Syn Sawtooth Wave", "Syn Calliope", "Syn Chiff", "Syn Charang",
			"Syn Voice", "Syn Fifths Sawtooth Wave", "Syn Brass & Lead", "New Age Syn Pad", "Warm Syn Pad", "Polysynth Syn Pad", "Choir Syn Pad", "Bowed Syn Pad", "Metal Syn Pad", "Halo Syn Pad", "Sweep Syn Pad",
			"SFX Rain", "SFX Soundtrack", "SFX Crystal", "SFX Atmosphere", "SFX Brightness", "SFX Goblins", "SFX Echoes", "SFX Sci-fi", "Sitar", "Banjo", "Shamisen", "Koto", "Kalimba", "Bag Pipe", "Fiddle", "Shanai",
			"Tinkle Bell", "Agogo", "Steel Drums", "Woodblock", "Taiko Drum", "Melodic Tom", "Syn Drum", "Reverse Cymbal", "Guitar Fret Noise", "Breath Noise", "Seashore", "Bird Tweet", "Telephone Ring", "Helicopter",
			"Applause", "Gun Shot" };

	void addEvent(CwnEvent event);
	
	int getPPQ();
	
	public String getName();

	public boolean getMute();

	public int getChannel();

	public int getInstrument();

	public int getVolume();

	public List<? extends CwnEvent> getEvents();
	
	public Trias nextBar(Trias trias);
	
	public long nextBar(long position) throws TimeSignatureException;
	
	<T extends CwnEvent> List<T> getList(Class<T> eventClass);
	
	public CwnTimeSignatureEvent getTimeSignature(long from);
	
	public CwnTimeSignatureEvent getTimeSignature(String from);
	
	public CwnKeyEvent getKey(long from);
	
	public CwnClefEvent getClef(long from);

	public CwnNoteEvent getHighestNote();

	public CwnNoteEvent getLowestNote();

	public boolean getPiano();

	public <T extends CwnEvent> Optional<T> findEventAtPosition(long position, Class<T> eventClass);

	public boolean isInfoTrack();
}
