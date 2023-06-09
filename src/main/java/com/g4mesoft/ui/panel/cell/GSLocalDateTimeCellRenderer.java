package com.g4mesoft.ui.panel.cell;

import java.time.LocalDateTime;

import com.g4mesoft.ui.panel.GSDimension;
import com.g4mesoft.ui.panel.GSPanelUtil;
import com.g4mesoft.ui.renderer.GSIRenderer2D;

public final class GSLocalDateTimeCellRenderer implements GSICellRenderer<LocalDateTime> {

	public static final GSLocalDateTimeCellRenderer INSTANCE = new GSLocalDateTimeCellRenderer();
	
	private GSLocalDateTimeCellRenderer() {
	}
	
	@Override
	public void render(GSIRenderer2D renderer, LocalDateTime value, GSCellContext context) {
		GSStringCellRenderer.INSTANCE.render(renderer, GSPanelUtil.formatLocalDateTime(value), context);
	}
	
	@Override
	public GSDimension getMinimumSize(LocalDateTime value) {
		return GSStringCellRenderer.INSTANCE.getMinimumSize(GSPanelUtil.formatLocalDateTime(value));
	}
}
