package com.g4mesoft.ui.panel.event;

import java.util.Arrays;
import java.util.Collection;

public class GSCompoundButtonStroke implements GSIButtonStroke {

	private final GSIButtonStroke[] buttons;

	/* Visible for GSButtonStrokeBuilder */
	GSCompoundButtonStroke(Collection<GSIButtonStroke> collection) {
		buttons = collection.toArray(new GSIButtonStroke[0]);
	}
	
	public GSCompoundButtonStroke(GSIButtonStroke first, GSIButtonStroke... others) {
		if (first == null || others == null)
			throw new IllegalArgumentException("Button strokes must not be null!");
		buttons = new GSIButtonStroke[1 + others.length];
		buttons[0] = first;
		for (int i = 0; i < others.length; i++) {
			if (others[i] == null)
				throw new IllegalArgumentException("Button strokes must not be null!");
			buttons[i + 1] = others[i];
		}
	}

	public GSIButtonStroke[] getButtons() {
		return Arrays.copyOf(buttons, buttons.length);
	}

	@Override
	public boolean isMatching(GSEvent event) {
		for (GSIButtonStroke button : buttons) {
			if (button.isMatching(event))
				return true;
		}
		return false;
	}
}
