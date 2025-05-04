package com.g4mesoft.ui.renderer.text;

import net.minecraft.text.Formatting;

public enum GSETextColor {

	BLACK(Formatting.BLACK),
	WHITE(Formatting.WHITE),
	GRAY(Formatting.GRAY),
	DARK_GRAY(Formatting.DARK_GRAY),
	BLUE(Formatting.BLUE),
	DARK_BLUE(Formatting.DARK_BLUE),
	GREEN(Formatting.GREEN),
	DARK_GREEN(Formatting.DARK_GREEN),
	AQUA(Formatting.AQUA),
	DARK_AQUA(Formatting.DARK_AQUA),
	RED(Formatting.RED),
	DARK_RED(Formatting.DARK_RED),
	YELLOW(Formatting.YELLOW),
	GOLD(Formatting.GOLD),
	LIGHT_PURPLE(Formatting.LIGHT_PURPLE);
	
	private final Formatting formatting;
	
	private GSETextColor(Formatting formatting) {
		this.formatting = formatting;
	}

	/* Visible for GSStyle */
	Formatting getFormatting() {
		return formatting;
	}
}
