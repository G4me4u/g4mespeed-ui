package com.g4mesoft.ui.panel.event;

import java.util.ArrayList;
import java.util.List;

public final class GSButtonStrokeBuilder {

	private final List<GSIButtonStroke> buttons;
	
	private GSButtonStrokeBuilder() {
		buttons = new ArrayList<>();
	}

	public GSButtonStrokeBuilder or(GSIButtonStroke button) {
		if (button == null)
			throw new IllegalArgumentException("button is null");
		buttons.add(button);
		return this;
	}
	
	public GSButtonStrokeBuilder key(int key) {
		return key(key, GSKeyEvent.NO_MODIFIERS);
	}
	
	public GSButtonStrokeBuilder key(int key, int modifiers) {
		return or(new GSKeyButtonStroke(key, modifiers));
	}

	public GSButtonStrokeBuilder mouse(int button) {
		return mouse(button, GSMouseEvent.NO_MODIFIERS);
	}
	
	public GSButtonStrokeBuilder mouse(int button, int modifiers) {
		return or(new GSMouseButtonStroke(button, modifiers));
	}
	
	public GSIButtonStroke build() {
		if (buttons.isEmpty())
			throw new IllegalStateException("Builder is empty");
		if (buttons.size() == 1)
			return buttons.get(0);
		return new GSCompoundButtonStroke(buttons);
	}
	
	public static GSButtonStrokeBuilder get() {
		return new GSButtonStrokeBuilder();
	}
}
