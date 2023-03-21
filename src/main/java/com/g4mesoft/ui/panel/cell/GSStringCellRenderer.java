package com.g4mesoft.ui.panel.cell;

import com.g4mesoft.ui.panel.GSDimension;
import com.g4mesoft.ui.renderer.GSIRenderer2D;

import net.minecraft.text.LiteralText;

public final class GSStringCellRenderer implements GSICellRenderer<String> {

	public static final GSStringCellRenderer INSTANCE = new GSStringCellRenderer();
	
	private GSStringCellRenderer() {
	}
	
	@Override
	public void render(GSIRenderer2D renderer, String value, GSCellContext context) {
		GSTextCellRenderer.INSTANCE.render(renderer, new LiteralText(value), context);
	}
	
	@Override
	public GSDimension getMinimumSize(String value) {
		return GSTextCellRenderer.INSTANCE.getMinimumSize(new LiteralText(value));
	}
}
