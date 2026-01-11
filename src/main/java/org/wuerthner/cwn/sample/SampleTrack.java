package org.wuerthner.cwn.sample;

import java.util.*;
import java.util.stream.Collectors;

import org.wuerthner.cwn.api.*;
import org.wuerthner.cwn.api.exception.TimeSignatureException;
import org.wuerthner.cwn.position.PositionTools;

public class SampleTrack implements CwnTrack {
	private final Set<CwnEvent> cwnEventList = new TreeSet<>();
	private final int ppq;
	
	private final String name;
	
	public SampleTrack(int ppq) {
		this(ppq, "untitled");
	}
	
	SampleTrack(int ppq, String name) {
		this.ppq = ppq;
		this.name = name;
	}
	
	@Override
	public String getName() {
		return name;
	}

	@Override
	public boolean getMute() {
		return false;
	}

    @Override
    public boolean getVisible() { return true; }

	@Override
	public int getChannel() {
		return 0;
	}

	@Override
	public int getInstrument() {
		return 0;
	}

	@Override
	public int getVolume() {
		return 8;
	}

	@Override
	public List<? extends CwnEvent> getEvents() {
		return new ArrayList<>(cwnEventList);
	}

	@Override
	public <T extends CwnEvent> List<T> getList(Class<T> eventClass) {
		return cwnEventList.stream().filter(event -> eventClass.isAssignableFrom(event.getClass())).map(event -> eventClass.cast(event)).collect(Collectors.toList());
	}
	
	@Override
	public Trias nextBar(Trias trias) {
		return trias.nextBar();
	}
	
	@Override
	public long nextBar(long position) throws TimeSignatureException {
		return PositionTools.getPosition(this, PositionTools.getTrias(this, position).nextBar());
	}
	
	@Override
	public void addEvent(CwnEvent event) {
		cwnEventList.add(event);
	}

	public void removeEvent(CwnEvent event) { cwnEventList.remove(event); }
	
	@Override
	public String toString() {
		return "SimpleTrack={size: " + cwnEventList.size() + "}";
	}
	
	@Override
	public int getPPQ() {
		return ppq;
	}
	
	@Override
	public CwnTimeSignatureEvent getTimeSignature(String from) {
		return getTimeSignature(PositionTools.getPosition(this, new Trias(from)));
	}
	
	@Override
	public CwnTimeSignatureEvent getTimeSignature(long from) {
		CwnTimeSignatureEvent timeSignatureEvent = cwnEventList.stream().filter(event -> CwnTimeSignatureEvent.class.isAssignableFrom(event.getClass())).filter(event -> event.getPosition() <= from)
				.map(event -> CwnTimeSignatureEvent.class.cast(event)).reduce((a, b) -> b).orElse(null);
		if (timeSignatureEvent == null) {
			throw new RuntimeException("Track must contain a time signature!");
		}
		return timeSignatureEvent;
	}
	
	@Override
	public CwnKeyEvent getKey(long from) {
		CwnKeyEvent keyEvent = cwnEventList.stream().filter(event -> CwnKeyEvent.class.isAssignableFrom(event.getClass())).filter(event -> event.getPosition() <= from).map(event -> CwnKeyEvent.class.cast(event))
				.reduce((a, b) -> b).orElse(null);
		if (keyEvent == null) {
			throw new RuntimeException("Track must contain a key!");
		}
		return keyEvent;
	}
	
	@Override
	public CwnClefEvent getClef(long from) {
		CwnClefEvent clefEvent = cwnEventList.stream().filter(event -> CwnClefEvent.class.isAssignableFrom(event.getClass())).filter(event -> event.getPosition() <= from).map(event -> CwnClefEvent.class.cast(event))
				.reduce((a, b) -> b).orElse(null);
		if (clefEvent == null) {
			throw new RuntimeException("Track must contain a clef!");
		}
		return clefEvent;
	}

	@Override
	public CwnNoteEvent getHighestNote() {
		return (CwnNoteEvent) cwnEventList.stream().filter(ev -> ev instanceof CwnNoteEvent)
				.max((ev1, ev2) -> Integer.compare(((CwnNoteEvent)ev1).getPitch(), ((CwnNoteEvent)ev2).getPitch())).orElse(null);
	}

	@Override
	public CwnNoteEvent getLowestNote() {
		return (CwnNoteEvent) cwnEventList.stream().filter(ev -> ev instanceof CwnNoteEvent)
				.min((ev1, ev2) -> Integer.compare(((CwnNoteEvent)ev1).getPitch(), ((CwnNoteEvent)ev2).getPitch())).orElse(null);
	}

	@Override
	public boolean getPiano() {
		return false;
	}

	@Override
	public <T extends CwnEvent> Optional<T> findEventAtPosition(long position, Class<T> eventClass) {
		return Optional.empty();
	}

	@Override
	public <T extends CwnEvent> Optional<T> findFirstEvent(Class<T> clasz) {
		return Optional.empty();
	}

	@Override
	public boolean isInfoTrack() {
		return false;
	}
}