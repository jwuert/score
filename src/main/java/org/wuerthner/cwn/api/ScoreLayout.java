package org.wuerthner.cwn.api;

public interface ScoreLayout {
	
	public int getBorder();
	
	public int getWidth();
	
	public int getTitleHeight();
	
	public int getSystemSpace();
	
	public int getSystemIndent();
	
	public int getStemLength();
	
	public double getPixelPerTick();
	
	public boolean showGrid();
	
	public default int getLineHeight() {
		return 6;
	}
	
	public default int getStaffHeight() {
		return 4 * getLineHeight() + 2 * getSystemSpace();
	}
	
	public boolean hasFullTupletPresentation();
	
	public int lyricsSpace();
	
	public boolean showVelocity();
}
