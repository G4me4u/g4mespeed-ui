package com.g4mesoft.ui.panel;

import com.g4mesoft.ui.renderer.GSIRenderer2D;

public abstract class GSIcon {

	public abstract void render(GSIRenderer2D renderer, GSRectangle bounds);

	public abstract int getWidth();

	public abstract int getHeight();
	
	public GSDimension getSize() {
		return new GSDimension(getWidth(), getHeight());
	}
}
