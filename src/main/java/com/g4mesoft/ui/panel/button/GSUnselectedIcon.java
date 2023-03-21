package com.g4mesoft.ui.panel.button;

import com.g4mesoft.ui.renderer.GSIRenderer2D;

public class GSUnselectedIcon extends GSRadioButtonIcon {

	public GSUnselectedIcon(int color, int preferredSize) {
		super(color, preferredSize);
	}

	@Override
	public void renderIcon(GSIRenderer2D renderer, int x, int y, int size) {
		renderer.drawRect(x, y, size, size, color);
	}
}
