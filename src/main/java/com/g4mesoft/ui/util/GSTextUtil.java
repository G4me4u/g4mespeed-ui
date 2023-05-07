package com.g4mesoft.ui.util;

import net.minecraft.text.LiteralText;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;

public final class GSTextUtil {

	public static final Text EMPTY = LiteralText.EMPTY;

	private GSTextUtil() {
	}

	public static MutableText literal(String text) {
		return new LiteralText(text);
	}

	public static MutableText translatable(String key) {
		return new TranslatableText(key);
	}

	public static MutableText translatable(String key, Object... params) {
		return new TranslatableText(key, params);
	}
}
