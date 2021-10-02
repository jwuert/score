package org.wuerthner.cwn.sample;

import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Collectors;

import org.wuerthner.cwn.api.CwnClefEvent;
import org.wuerthner.cwn.api.CwnEvent;
import org.wuerthner.cwn.api.CwnKeyEvent;
import org.wuerthner.cwn.api.CwnTimeSignatureEvent;
import org.wuerthner.cwn.api.CwnTrack;
import org.wuerthner.cwn.api.Trias;
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
}