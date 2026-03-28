package com.g4mesoft.ui.util;

import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;

public final class GSTextUtil {

	public static final Component EMPTY = Component.empty();

	private GSTextUtil() {
	}

	public static MutableComponent literal(String text) {
		if (text == null)
			throw new IllegalArgumentException("text is null");
		return Component.literal(text);
	}

	public static MutableComponent translatable(String key) {
		if (key == null)
			throw new IllegalArgumentException("text is null");
		return Component.translatable(key);
	}

	public static MutableComponent translatable(String key, Object... params) {
		if (key == null)
			throw new IllegalArgumentException("text is null");
		return Component.translatable(key, params);
	}
}
