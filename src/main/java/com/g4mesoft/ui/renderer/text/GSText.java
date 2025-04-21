package com.g4mesoft.ui.renderer.text;

import com.g4mesoft.ui.mixin.client.GSITextRendererInvoker;

public abstract class GSText {

	protected static final char STYLING_CHAR = '\u00A7';
	
	public GSText append(GSText other) {
		return new GSCompoundText(this, other);
	}

	protected static boolean isStyling(char c) {
		return GSITextRendererInvoker.gs_isFormatting(c) || GSITextRendererInvoker.gs_isColor(c);
	}

	protected static String stripStyling(String text) {
		int styleIdx = text.indexOf(STYLING_CHAR);
		if (styleIdx == -1) {
			// Already without styling.
			return text;
		}
		// Strip styling.
		StringBuilder sb = new StringBuilder();
		int i = 0;
		do {
			sb.append(text.subSequence(i, styleIdx));
			if (styleIdx + 1 < text.length() && isStyling(text.charAt(styleIdx + 1))) {
				// Skip formatting char and styling.
				i = styleIdx + 2;
			} else {
				// Include style character.
				i = styleIdx + 1;
				sb.append(STYLING_CHAR);
			}
			styleIdx = text.indexOf(STYLING_CHAR, i);
		} while (styleIdx != -1);
		// Append trailing part of text.
		if (i < text.length())
			sb.append(text.subSequence(i, text.length()));
		return sb.toString();
	}

	public abstract String build(boolean withStyling);

	@Override
	public int hashCode() {
		return build(true).hashCode();
	}

	@Override
	public boolean equals(Object other) {
		if (other == this)
			return true;
		if (other instanceof GSText)
			return ((GSText)other).build(true).equals(build(true));
		return false;
	}
}
