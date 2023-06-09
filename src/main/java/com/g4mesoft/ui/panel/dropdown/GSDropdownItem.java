package com.g4mesoft.ui.panel.dropdown;

import com.g4mesoft.ui.panel.GSDimension;
import com.g4mesoft.ui.panel.GSPanel;

public abstract class GSDropdownItem extends GSPanel {

	protected static final int ICON_SIZE        = 10;
	protected static final int ICON_MARGIN      = 4;
	protected static final int PADDING          = 2;
	
	protected static final int HOVERED_BACKGROUND_COLOR = 0xFF094771;

	protected static final int TEXT_COLOR               = 0xFFCCCCCC;
	protected static final int HOVERED_TEXT_COLOR       = 0xFFF3F6F8;
	protected static final int DISABLED_TEXT_COLOR      = 0xFF686869;
	
	public GSDropdownItem() {
		// Drop-down menu items should never be focusable,
		// since that would cause the drop-down to close.
		super.setFocusable(false);
	}
	
	@Override
	protected abstract GSDimension calculatePreferredSize();

	@Override
	public void setFocusable(boolean focusable) {
		throw new UnsupportedOperationException("Dropdown items can not be focusable");
	}
}
