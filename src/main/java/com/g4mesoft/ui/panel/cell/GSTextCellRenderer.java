package com.g4mesoft.ui.panel.cell;

import com.g4mesoft.ui.panel.GSDimension;
import com.g4mesoft.ui.panel.GSPanelUtil;
import com.g4mesoft.ui.renderer.GSIRenderer2D;
import com.g4mesoft.ui.renderer.text.GSText;

public final class GSTextCellRenderer implements GSICellRenderer<GSText> {

	public static final GSTextCellRenderer INSTANCE = new GSTextCellRenderer();
	
	private GSTextCellRenderer() {
	}
	
	@Override
	public void render(GSIRenderer2D renderer, GSText value, GSCellContext context) {
		GSPanelUtil.drawLabel(renderer, null, 0, value, context.textColor,
				false, null, context.textAlignment, context.bounds);
	}
	
	@Override
	public GSDimension getMinimumSize(GSText value) {
		return GSPanelUtil.labelPreferredSize(null, value, 0);
	}
}
