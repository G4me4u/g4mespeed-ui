package com.g4mesoft.ui.panel.table;

import com.g4mesoft.ui.panel.GSETextAlignment;

public interface GSITableColumn extends GSITableHeaderElement {

	public int getWidth();

	public void setWidth(int width);
	
	public GSETextAlignment getTextAlignment();

	public void setTextAlignment(GSETextAlignment textAlignment);
	
}
