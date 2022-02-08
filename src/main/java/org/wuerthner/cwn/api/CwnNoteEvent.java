package org.wuerthner.cwn.api;

import java.util.List;

public interface CwnNoteEvent extends CwnEvent {
	@Override
	public long getPosition();
	
	@Override
	public long getDuration();
	
	public int getPitch();
	
	public int getEnharmonicShift();
	
	public int getVelocity();
	
	public int getTuplet();
	
	public int getVoice();
	
	/**
	 * direction of the stem
	 * 
	 * @return returns -1 (down), 0 (auto) or 1 (up)
	 */
	public int getStemDirection();
	
	public String getLyrics();
	
	public boolean isUngrouped();
	
	public List<? extends CwnAccent> getAccentList();
	
	public void addAccent(CwnAccent accent);
	
	public void clearAccents();
	
	boolean hasAccents();
	
	public void addMark(String mark);
	
	public void clearMark();
	
	public String getMarks();

	public List<String> getMarkList();
}
