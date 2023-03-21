package com.g4mesoft.ui.panel;

public interface GSILayoutManager {

	public GSDimension getMinimumSize(GSParentPanel parent);

	public GSDimension getPreferredSize(GSParentPanel parent);
	
	public void layoutChildren(GSParentPanel parent);
	
}
