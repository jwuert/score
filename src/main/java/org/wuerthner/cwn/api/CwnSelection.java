package org.wuerthner.cwn.api;

import java.util.List;

public interface CwnSelection<T> {
	public enum SelectionType {
		NOTE, POSITION
	}
	
	public boolean contains(T event);
	
	public boolean hasStaffSelected(int index);

	public int getSelectedStaff();
	
	public boolean isEmpty();
	
	public boolean hasCursor();
	
	public long getCursorPosition();
	
	public boolean hasMouseDown();
	
	public long getMouseLeftPosition();
	
	public long getMouseRightPosition();
	
	public int getMouseStaff();
	
	public SelectionType getSelectionType();

	public CwnPointer getPointer();

	public boolean hasSingleSelection();

	public T getSingleSelection();

	// public void setSingleSelection(T event);

	public List<T> getSelection();
}
