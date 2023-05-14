package com.g4mesoft.ui.util;

import net.minecraft.text.MutableText;
import net.minecraft.text.Text;

public final class GSTextUtil {

	public static final Text EMPTY = Text.empty();

	private GSTextUtil() {
	}

	public static MutableText literal(String text) {
		return Text.literal(text);
	}

	public static MutableText translatable(String key) {
		return Text.translatable(key);
	}

	public static MutableText translatable(String key, Object... params) {
		return Text.translatable(key, params);
	}
}
