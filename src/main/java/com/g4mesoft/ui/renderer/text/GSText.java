package com.g4mesoft.ui.renderer.text;

import com.g4mesoft.ui.mixin.client.GSITextRendererInvoker;

public abstract class GSText {
	
	private GSStyle style;
	
	public GSText() {
		style = GSStyle.EMPTY;
	}
	
	public GSText append(GSText other) {
		return new GSCompoundText(this, other);
	}

	public void setStyle(GSStyle style) {
		if (style == null)
			throw new IllegalArgumentException("style is null");
		this.style = style;
	}
	
	public GSStyle getStyle() {
		return style;
	}
	
	protected static boolean isStyling(char c) {
		return GSITextRendererInvoker.gs_isFormatting(c) || GSITextRendererInvoker.gs_isColor(c);
	}

	protected static void stripStyling(String text, StringBuilder dst) {
		int styleIdx = text.indexOf(GSStyle.STYLING_CHAR);
		if (styleIdx == -1) {
			// Already without styling.
			dst.append(text);
		}
		// Strip styling.
		int i = 0;
		do {
			dst.append(text.subSequence(i, styleIdx));
			if (styleIdx + 1 < text.length() && isStyling(text.charAt(styleIdx + 1))) {
				// Skip formatting char and styling.
				i = styleIdx + 2;
			} else {
				// Include style character.
				i = styleIdx + 1;
				dst.append(GSStyle.STYLING_CHAR);
			}
			styleIdx = text.indexOf(GSStyle.STYLING_CHAR, i);
		} while (styleIdx != -1);
		// Append trailing part of text.
		if (i < text.length())
			dst.append(text.subSequence(i, text.length()));
	}

	protected abstract void buildImpl(boolean withStyling, StringBuilder dst);
	
	public final String build(boolean withStyling) {
		StringBuilder sb = new StringBuilder();
		if (withStyling)
			style.build(sb);
		buildImpl(withStyling, sb);
		if (withStyling)
			GSStyle.RESET.build(sb);
		return sb.toString();
	}

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
