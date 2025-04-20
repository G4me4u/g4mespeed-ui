package com.g4mesoft.ui.util;

import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;

public final class GSTextUtil {

	public static final Text EMPTY = new LiteralText("");

	private GSTextUtil() {
	}

	public static Text literal(String text) {
		if (text == null)
			throw new IllegalArgumentException("text is null");
		return new LiteralText(text);
	}

	public static Text translatable(String key) {
		if (key == null)
			throw new IllegalArgumentException("text is null");
		return new TranslatableText(key);
	}

	public static Text translatable(String key, Object... params) {
		if (key == null)
			throw new IllegalArgumentException("text is null");
		return new TranslatableText(key, params);
	}
}
