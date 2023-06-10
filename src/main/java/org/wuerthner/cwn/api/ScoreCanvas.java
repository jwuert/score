package org.wuerthner.cwn.api;

public interface ScoreCanvas {
	public void open();
	
	public void drawLine(int x1, int y1, int x2, int y2);
	
	public void drawString(String string, String fontName, int x, int y, String align, boolean alternative);

	default void drawString(String string, String fontName, int x, int y, String align) {
		drawString(string, fontName, x, y, align, false);
	}

	public void drawImage(String string, int x, int y, boolean alternative);

	public void drawDot(int d, int yBase);

	public void drawDot(int d, int yBase, int color);
	
	public void drawLine(int x1, int y1, int x2, int y2, String color);
	
	public void drawLine(int x1, int y1, int x2, int y2, int width);
	
	public void drawLine(int x1, int y, int x2, int y2, boolean alternative);
	
	public void close();
	
	public void drawRect(int xPositionLeft, int yPositionTop, int xPositionRight, int yPositionBottom);
	
	public boolean outsideDrawArea();
	
	// public void drawArc(int x1, int y1, int x2, int y2, int direction);
	
	public void drawArc(int x1, int y1, int x2, int y2, int direction, int delta, boolean alternative);
	
	// public void drawArc(int x, int y, int x2, int stemDirection, boolean alternative);
}
