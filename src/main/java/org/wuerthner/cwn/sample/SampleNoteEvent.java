package org.wuerthner.cwn.sample;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.wuerthner.cwn.api.CwnAccent;
import org.wuerthner.cwn.api.CwnEvent;
import org.wuerthner.cwn.api.CwnNoteEvent;

public class SampleNoteEvent implements CwnNoteEvent, Comparable<CwnEvent> {
	
	final private long position;
	final private long duration;
	final private int pitch;
	final private int shift;
	final private int velocity;
	final private int voice;
	final private int stemDirection;
	final private String lyrics;
	final private boolean isUngrouped;
	final private List<CwnAccent> accentList;
	final private List<String> markList;
	
	public SampleNoteEvent(long position, long duration, int pitch, int shift, int velocity, int voice) {
		this(position, duration, pitch, shift, velocity, voice, "", false);
	}
	
	public SampleNoteEvent(long position, long duration, int pitch, int shift, int velocity, int voice, String lyrics, boolean isUngrouped, CwnAccent accent) {
		this(position, duration, pitch, shift, velocity, voice, lyrics, isUngrouped);
		accentList.add(accent);
	}
	
	public SampleNoteEvent(long position, long duration, int pitch, int shift, int velocity, int voice, String lyrics, boolean isUngrouped) {
		
		this.position = position;
		this.duration = duration;
		this.pitch = pitch;
		this.shift = shift;
		this.velocity = velocity;
		this.voice = voice;
		this.stemDirection = 0;
		this.lyrics = lyrics;
		this.isUngrouped = isUngrouped;
		this.accentList = new ArrayList<>();
		this.markList = new ArrayList<>();
	}
	
	@Override
	public long getPosition() {
		return position;
	}
	
	@Override
	public long getDuration() {
		return duration;
	}
	
	@Override
	public int getPitch() {
		return pitch;
	}
	
	@Override
	public int getEnharmonicShift() {
		return shift;
	}
	
	@Override
	public int getVelocity() {
		return velocity;
	}
	
	@Override
	public int getTuplet() {
		return 0;
	}
	
	@Override
	public int getVoice() {
		return voice;
	}
	
	@Override
	public boolean isUngrouped() {
		return isUngrouped;
	}
	
	@Override
	public String toString() {
		return "SimpleNoteEvent={position: " + position + ", duration: " + duration + ", pitch: " + pitch + "}";
	}
	
	@Override
	public int getStemDirection() {
		return stemDirection;
	}
	
	@Override
	public String getLyrics() {
		return lyrics;
	}
	
	@Override
	public List<CwnAccent> getAccentList() {
		return accentList;
	}
	
	@Override
	public void addAccent(CwnAccent accent) {
		accentList.add(accent);
	}
	
	@Override
	public void clearAccents() {
		accentList.clear();
	}
	
	@Override
	public boolean hasAccents() {
		return !accentList.isEmpty();
	}
	
	@Override
	public int compareTo(CwnEvent event) {
		long thisPosition = getPosition();
		int thisPitch = getPitch();
		long thatPosition = event.getPosition();
		if (thisPosition != thatPosition) {
			return (int) (thisPosition - thatPosition);
		} else if (event instanceof CwnNoteEvent) {
			CwnNoteEvent noteEvent = (CwnNoteEvent) event;
			int thatPitch = noteEvent.getPitch();
			return thisPitch - thatPitch;
		}
		return 1;
	}
	
	@Override
	public void addMark(String mark) {
		markList.add(mark);
	}
	
	@Override
	public void clearMark() {
		markList.clear();
	}
	
	@Override
	public String getMarks() {
		return markList.stream().collect(Collectors.joining(", "));
	}

	@Override
	public List<String> getMarkList() {
		return markList;
	}
}
