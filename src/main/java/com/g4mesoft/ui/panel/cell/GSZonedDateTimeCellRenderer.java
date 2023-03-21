package com.g4mesoft.ui.panel.cell;

import java.time.ZonedDateTime;

import com.g4mesoft.ui.panel.GSDimension;
import com.g4mesoft.ui.panel.GSPanelUtil;
import com.g4mesoft.ui.renderer.GSIRenderer2D;

public final class GSZonedDateTimeCellRenderer implements GSICellRenderer<ZonedDateTime> {

	public static final GSZonedDateTimeCellRenderer INSTANCE = new GSZonedDateTimeCellRenderer();
	
	private GSZonedDateTimeCellRenderer() {
	}
	
	@Override
	public void render(GSIRenderer2D renderer, ZonedDateTime value, GSCellContext context) {
		GSStringCellRenderer.INSTANCE.render(renderer, GSPanelUtil.formatZonedDateTime(value), context);
	}
	
	@Override
	public GSDimension getMinimumSize(ZonedDateTime value) {
		return GSStringCellRenderer.INSTANCE.getMinimumSize(GSPanelUtil.formatZonedDateTime(value));
	}
}
