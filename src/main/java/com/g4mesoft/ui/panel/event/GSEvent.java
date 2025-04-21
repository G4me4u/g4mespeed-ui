package com.g4mesoft.ui.panel.event;

import com.g4mesoft.ui.panel.GSPanel;

public abstract class GSEvent {
	
	public static final int UNKNOWN_TYPE = 0;
	
	public static final int NO_MODIFIERS = 0;

	public static final int MODIFIER_SHIFT     = 0x01;
	public static final int MODIFIER_CONTROL   = 0x02;
	public static final int MODIFIER_ALT       = 0x04;
	public static final int MODIFIER_SUPER     = 0x08;
	public static final int MODIFIER_CAPS_LOCK = 0x10;
	public static final int MODIFIER_NUM_LOCK  = 0x20;

	public static final int ALL_MODIFIERS = MODIFIER_SHIFT     | MODIFIER_CONTROL | 
	                                        MODIFIER_ALT       | MODIFIER_SUPER |
	                                        MODIFIER_CAPS_LOCK | MODIFIER_NUM_LOCK;
	
	private GSPanel panel;
	
	private boolean consumed;
	
	public GSEvent() {
		panel = null;
		
		consumed = false;
	}
	
	public abstract int getType();
	
	/* Visible for GSEventDispatcher */
	void setPanel(GSPanel panel) {
		this.panel = panel;
	}
	
	public GSPanel getPanel() {
		return panel;
	}
	
	public void consume() {
		consumed = true;
	}
	
	public boolean isConsumed() {
		return consumed;
	}
}
