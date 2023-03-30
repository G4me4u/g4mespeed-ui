package com.g4mesoft.ui.panel.dropdown;

import java.util.ArrayList;
import java.util.List;

import com.g4mesoft.ui.panel.GSDimension;
import com.g4mesoft.ui.panel.GSIActionListener;
import com.g4mesoft.ui.panel.GSILayoutProperty;
import com.g4mesoft.ui.panel.GSPanel;
import com.g4mesoft.ui.panel.GSParentPanel;
import com.g4mesoft.ui.panel.GSPopup;
import com.g4mesoft.ui.panel.event.GSIKeyListener;
import com.g4mesoft.ui.panel.event.GSKeyEvent;

public class GSDropdown extends GSParentPanel implements GSIKeyListener {

	protected static final int VERTICAL_PADDING = 4;
	
	private final List<GSIActionListener> actionListeners;
	
	private boolean separateNextItem;
	
	public GSDropdown() {
		actionListeners = new ArrayList<>();
		
		addKeyEventListener(this);
	}
	
	@Override
	public void add(GSPanel panel) {
		if (!(panel instanceof GSDropdownItem))
			throw new IllegalArgumentException("Drop-down menus only support drop-down items.");
		addItem((GSDropdownItem)panel);
	}
	
	public void addItem(GSDropdownItem item) {
		if (separateNextItem) {
			separateNextItem = false;
			super.add(new GSDropdownSeparator());
		}
		
		super.add(item);
	}
	
	public void separate() {
		separateNextItem |= !isEmpty();
	}
	
	@Override
	public void layout() {
		int y = VERTICAL_PADDING;
		
		for (GSPanel child : getChildren()) {
			// Since children must implement GSDropdownItem, it is
			// assumed that they have a preferred size.
			GSDimension pref = child.getProperty(PREFERRED_SIZE);
			// Ensure there is actually space for the preferred size.
			int h = Math.max(Math.min(pref.getHeight(), height - y), 0);
			child.setBounds(0, y, width, h);

			y += h;
		}
	}
	
	@Override
	public GSDimension calculateMinimumSize() {
		return calculateSize(MINIMUM_SIZE);
	}
	
	@Override
	protected GSDimension calculatePreferredSize() {
		return calculateSize(PREFERRED_SIZE);
	}
	
	private GSDimension calculateSize(GSILayoutProperty<GSDimension> sizeProperty) {
		int w = 0, h = 0;
		
		for (GSPanel child : getChildren()) {
			GSDimension size = child.getProperty(sizeProperty);
			if (size.getWidth() > w)
				w = size.getWidth();
			h += size.getHeight();
		}
		
		return new GSDimension(w, h + 2 * VERTICAL_PADDING);
	}
	
	public boolean isEmpty() {
		return children.isEmpty();
	}
	
	/* Visible for GSDropdownSubMenu */
	void addActionListener(GSIActionListener listener) {
		if (listener == null)
			throw new IllegalArgumentException("listener is null");
		actionListeners.add(listener);
	}

	/* Visible for GSDropdownSubMenu */
	void removeActionListener(GSIActionListener listener) {
		actionListeners.remove(listener);
	}
	
	/* Visible for GSDropdownAction */
	void onActionPerformed() {
		hide();
	}
	
	private void hide() {
		GSPanel parent = getParent();
		if (parent instanceof GSPopup)
			((GSPopup)parent).hide();

		actionListeners.forEach(GSIActionListener::actionPerformed);
	}

	@Override
	public void keyPressed(GSKeyEvent event) {
		switch (event.getKeyCode()) {
		case GSKeyEvent.KEY_ESCAPE:
		case GSKeyEvent.KEY_ENTER:
			hide();
			event.consume();
			break;
		default:
			break;
		}
	}
}
