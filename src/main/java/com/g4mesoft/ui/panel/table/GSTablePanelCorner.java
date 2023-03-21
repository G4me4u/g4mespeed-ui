package com.g4mesoft.ui.panel.table;

import com.g4mesoft.ui.panel.GSPanel;
import com.g4mesoft.ui.renderer.GSIRenderer2D;
import com.g4mesoft.ui.util.GSColorUtil;

public class GSTablePanelCorner extends GSPanel {

	private final GSTablePanel table;
	
	public GSTablePanelCorner(GSTablePanel table) {
		this.table = table;
	}
	
	@Override
	public void render(GSIRenderer2D renderer) {
		if (table.getBackgroundColor() != 0) {
			int backgroundColor = GSColorUtil.darker(table.getBackgroundColor());
			renderer.fillRect(0, 0, width, height, backgroundColor);
		}

		super.render(renderer);
	}
}
