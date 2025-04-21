package com.g4mesoft.ui.renderer.text;

public class GSLiteralText extends GSText {

	private final String text;
	
	public GSLiteralText(String text) {
		this.text = text;
	}

	@Override
	public String build(boolean withStyling) {
		return withStyling ? text : stripStyling(text);
	}
}
