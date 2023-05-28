package com.g4mesoft.ui.panel.dialog;

public enum GSEConfirmOptionPlacement {

	LEFT(0),
	CENTER(1),
	RIGHT(2);

	private final int order;
	
	private GSEConfirmOptionPlacement(int order) {
		this.order = order;
	}

	/* Note: order is smallest first from left to right. */
	public int getOrder() {
		return order;
	}
}
