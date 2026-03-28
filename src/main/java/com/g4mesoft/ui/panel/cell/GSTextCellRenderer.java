package com.g4mesoft.ui.panel.cell;

import com.g4mesoft.ui.panel.GSDimension;
import com.g4mesoft.ui.panel.GSPanelUtil;
import com.g4mesoft.ui.renderer.GSIRenderer2D;

import net.minecraft.network.chat.Component;

public final class GSTextCellRenderer implements GSICellRenderer<Component> {

	public static final GSTextCellRenderer INSTANCE = new GSTextCellRenderer();
	
	private GSTextCellRenderer() {
	}
	
	@Override
	public void render(GSIRenderer2D renderer, Component value, GSCellContext context) {
		GSPanelUtil.drawLabel(renderer, null, 0, value, context.textColor,
				false, null, context.textAlignment, context.bounds);
	}
	
	@Override
	public GSDimension getMinimumSize(Component value) {
		return GSPanelUtil.labelPreferredSize(null, value, 0);
	}
}
