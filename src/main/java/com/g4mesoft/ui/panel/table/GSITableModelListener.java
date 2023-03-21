package com.g4mesoft.ui.panel.table;

public interface GSITableModelListener {

	public void columnHeaderChanged(int columnIndex);

	public void columnSizeChanged(int columnIndex);

	public void rowHeaderChanged(int rowIndex);
	
	public void rowSizeChanged(int rowIndex);

	public void cellValueChanged(int columnIndex, int rowIndex);
	
	public void headerVisibilityChanged();
	
}
