package org.wuerthner.cwn.api;

public interface CwnSelection<T> {
	public enum SelectionType {
		NOTE, POSITION
	}
	
	public boolean contains(T event);
	
	public boolean hasStaffSelected(int index);
	
	public boolean isEmpty();
	
	public boolean hasCursor();
	
	public long getCursorPosition();
	
	public boolean hasMouseDown();
	
	public long getMouseLeftPosition();
	
	public long getMouseRightPosition();
	
	public int getMouseStaff();
	
	public SelectionType getSelectionType();

	public CwnPointer getPointer();
}
