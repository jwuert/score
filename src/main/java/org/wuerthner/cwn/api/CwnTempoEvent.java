package org.wuerthner.cwn.api;

public interface CwnTempoEvent extends CwnEvent {
	public String getLabel();
	
	public int getTempo();
}
