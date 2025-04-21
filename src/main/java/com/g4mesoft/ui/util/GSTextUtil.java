package com.g4mesoft.ui.util;

import net.minecraft.text.Text;

public final class GSTextUtil {

	public static final Text EMPTY = Text.literal("");

	private GSTextUtil() {
	}

	public static Text literal(String text) {
		if (text == null)
			throw new IllegalArgumentException("text is null");
		return Text.literal(text);
	}

	public static Text translatable(String key) {
		if (key == null)
			throw new IllegalArgumentException("text is null");
		return Text.translatable(key);
	}

	public static Text translatable(String key, Object... params) {
		if (key == null)
			throw new IllegalArgumentException("text is null");
		return Text.translatable(key, params);
	}
}
