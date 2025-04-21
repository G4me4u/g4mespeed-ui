package com.g4mesoft.ui.util;

import com.g4mesoft.ui.renderer.text.GSLiteralText;
import com.g4mesoft.ui.renderer.text.GSText;
import com.g4mesoft.ui.renderer.text.GSTranslatableText;

public final class GSTextUtil {

	public static final GSText EMPTY = literal("");

	private GSTextUtil() {
	}

	public static GSText literal(String text) {
		if (text == null)
			throw new IllegalArgumentException("text is null");
		return new GSLiteralText(text);
	}

	public static GSText translatable(String key) {
		if (key == null)
			throw new IllegalArgumentException("text is null");
		return new GSTranslatableText(key);
	}

	public static GSText translatable(String key, Object... params) {
		if (key == null)
			throw new IllegalArgumentException("text is null");
		return new GSTranslatableText(key, params);
	}
}
