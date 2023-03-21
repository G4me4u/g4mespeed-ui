package com.g4mesoft.ui.panel.cell;

import com.g4mesoft.ui.panel.GSDimension;
import com.g4mesoft.ui.renderer.GSIRenderer2D;

public final class GSEmptyCellRenderer implements GSICellRenderer<Object> {

	public static final GSEmptyCellRenderer INSTANCE = new GSEmptyCellRenderer();
	
	private GSEmptyCellRenderer() {
	}
	
	@Override
	public void render(GSIRenderer2D renderer, Object value, GSCellContext context) {
		// Do nothing
	}
	
	@Override
	public GSDimension getMinimumSize(Object value) {
		return GSDimension.ZERO;
	}

	public static <T> GSICellRenderer<T> getInstance() {
		@SuppressWarnings("unchecked")
		GSICellRenderer<T> instance = (GSICellRenderer<T>)INSTANCE;
		return instance;
	}
}
