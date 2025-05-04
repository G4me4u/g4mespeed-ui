package com.g4mesoft.ui.renderer.text;

public class GSLiteralText extends GSText {

	private final String text;
	
	public GSLiteralText(String text) {
		this.text = text;
	}

	@Override
	protected void buildImpl(boolean withStyling, StringBuilder dst) {
		if (withStyling) {
			dst.append(text);
		} else {
			stripStyling(text, dst);
		}
	}
}
