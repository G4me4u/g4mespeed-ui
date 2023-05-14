package com.g4mesoft.ui.panel.table;

import com.g4mesoft.ui.panel.GSIChangeListener;

public interface GSIHeaderSelectionModel {

	public static final int INVALID_SELECTION = GSTablePanel.INVALID_HEADER_INDEX;
	
	public void addListener(GSIChangeListener listener);

	public void removeListener(GSIChangeListener listener);
	
	public void set(int index);

	public void setInterval(int i0, int i1);
	
	public void clear();
	
	public int getIntervalMin();
	
	public int getIntervalMax();
	
	public boolean isSelected(int index);
	
	public boolean isEmpty();
	
	public void setSelectionPolicy(GSEHeaderSelectionPolicy policy);

	public GSEHeaderSelectionPolicy getSelectionPolicy();

	public int getAnchor();
	
	public void setAnchor(int index);

	public int getLead();
	
}
