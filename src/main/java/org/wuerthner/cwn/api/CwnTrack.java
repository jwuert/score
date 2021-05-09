package org.wuerthner.cwn.api;

import java.util.List;

import org.wuerthner.cwn.api.exception.TimeSignatureException;

public interface CwnTrack {
	void addEvent(CwnEvent event);
	
	int getPPQ();
	
	public String getName();
	
	public Trias nextBar(Trias trias);
	
	public long nextBar(long position) throws TimeSignatureException;
	
	<T extends CwnEvent> List<T> getList(Class<T> eventClass);
	
	public CwnTimeSignatureEvent getTimeSignature(long from);
	
	public CwnTimeSignatureEvent getTimeSignature(String from);
	
	public CwnKeyEvent getKey(long from);
	
	public CwnClefEvent getClef(long from);
}
