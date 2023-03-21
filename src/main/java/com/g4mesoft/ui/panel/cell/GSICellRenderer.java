package com.g4mesoft.ui.panel.cell;

import com.g4mesoft.ui.panel.GSDimension;
import com.g4mesoft.ui.renderer.GSIRenderer2D;

public interface GSICellRenderer<T> {

	public void render(GSIRenderer2D renderer, T value, GSCellContext context);
	
	public GSDimension getMinimumSize(T value);

	default public GSDimension getMaximumSize(T value) {
		return GSDimension.MAX_VALUE;
	}
}
