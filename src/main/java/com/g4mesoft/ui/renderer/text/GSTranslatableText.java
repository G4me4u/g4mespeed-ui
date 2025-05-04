package com.g4mesoft.ui.renderer.text;

import net.minecraft.locale.LanguageManager;

public class GSTranslatableText extends GSText {

	private final String key;
	private final Object[] params;
	
	private String cachedText;
	private String cachedTextLanguage;
	
	public GSTranslatableText(String key) {
		this(key, (Object[])null);
	}
	
	public GSTranslatableText(String key, Object... params) {
		this.key = key;
		this.params = params;
		
		cachedText = cachedTextLanguage = null;
	}

	@Override
	protected void buildImpl(boolean withStyling, StringBuilder dst) {
		LanguageManager language = LanguageManager.getInstance();
		// Attempt to use cached translation.
		String text;
		if (cachedText != null && language.getCurrentLanguage().equals(cachedTextLanguage)) {
			text = cachedText;
		} else {
			// Otherwise, translate (again).
			if (params != null) {
				text = language.translate(key, params);
			} else {
				text = language.translate(key);
			}
			cachedText = text;
			cachedTextLanguage = language.getCurrentLanguage();
		}
		if (withStyling) {
			dst.append(text);
		} else {
			stripStyling(text, dst);
		}
	}
}
