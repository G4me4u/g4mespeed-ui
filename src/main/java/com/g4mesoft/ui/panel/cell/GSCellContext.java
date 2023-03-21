package com.g4mesoft.ui.panel.cell;

import com.g4mesoft.ui.panel.GSETextAlignment;
import com.g4mesoft.ui.panel.GSRectangle;

public final class GSCellContext {

	public int backgroundColor;
	public int textColor;
	public GSETextAlignment textAlignment;
	
	public final GSRectangle bounds;
	
	public GSCellContext() {
		backgroundColor = textColor = 0;
		textAlignment = GSETextAlignment.CENTER;
		bounds = new GSRectangle();
	}
}
