package com.g4mesoft.ui.panel.field;

public interface GSITextModelListener {

	public void textInserted(GSITextModel model, int offset, int count);
	
	public void textRemoved(GSITextModel model, int offset, int count);
	
}
