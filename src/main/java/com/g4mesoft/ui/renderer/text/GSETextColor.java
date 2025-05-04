package com.g4mesoft.ui.renderer.text;

public enum GSETextColor {

	BLACK('0'),
	WHITE('f'),
	GRAY('7'),
	DARK_GRAY('8'),
	BLUE('9'),
	DARK_BLUE('1'),
	GREEN('a'),
	DARK_GREEN('2'),
	AQUA('b'),
	DARK_AQUA('3'),
	RED('c'),
	DARK_RED('4'),
	YELLOW('e'),
	GOLD('6'),
	LIGHT_PURPLE('d');
	
	private final char formatCode;
	
	private GSETextColor(char formatCode) {
		this.formatCode = formatCode;
	}

	/* Visible for GSStyle */
	char getFormatCode() {
		return formatCode;
	}
}
