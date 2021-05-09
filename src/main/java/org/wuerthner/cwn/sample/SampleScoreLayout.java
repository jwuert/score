package org.wuerthner.cwn.sample;

import org.wuerthner.cwn.api.ScoreLayout;

public class SampleScoreLayout implements ScoreLayout {
	@Override
	public int getBorder() {
		return 25;
	}
	
	@Override
	public int getTitleHeight() {
		return 50;
	}
	
	@Override
	public int getSystemSpace() {
		
		return 20;
	}
	
	@Override
	public int getSystemIndent() {
		return 120;
	}
	
	@Override
	public int getStemLength() {
		return 21;
	}
	
	@Override
	public double getPixelPerTick() {
		return 0.020;
	}
	
	@Override
	public boolean showGrid() {
		return false;
	}
	
	@Override
	public boolean showVelocity() {
		return true;
	}
	
	@Override
	public int getWidth() {
		return 400;
	}
	
	@Override
	public boolean hasFullTupletPresentation() {
		return false;
	}
	
	@Override
	public int lyricsSpace() {
		return 0;
	}
}
