package com.g4mesoft.ui.renderer.text;

import java.util.ArrayList;
import java.util.List;

public class GSCompoundText extends GSText {

	private final List<GSText> children;
	
	public GSCompoundText() {
		this.children = new ArrayList<>(2);
	}
	
	public GSCompoundText(GSText... texts) {
		this.children = new ArrayList<>(texts.length);
		for (GSText text : texts)
			this.children.add(text);
	}
	
	@Override
	public GSText append(GSText other) {
		children.add(other);
		return this;
	}
	
	@Override
	protected void buildImpl(boolean withStyling, StringBuilder dst) {
		for (GSText text : children)
			dst.append(text.build(withStyling));
	}
}
