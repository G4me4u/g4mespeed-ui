package com.g4mesoft.ui.renderer.text;

public class GSStyle {

	public static final GSStyle EMPTY = new GSStyle(false);
	public static final GSStyle RESET = new GSStyle(true);

	/* Visible for GSText */
	static final char STYLING_CHAR = '\u00A7';
	
	// Formatting codes
	private static final char OBFUSCATED_FORMAT_CODE = 'k';
	private static final char BOLD_FORMAT_CODE = 'l';
	private static final char STRIKETHROUGH_FORMAT_CODE = 'm';
	private static final char UNDERLINE_FORMAT_CODE = 'n';
	private static final char ITALIC_FORMAT_CODE = 'o';
	private static final char RESET_FORMAT_CODE = 'r';
	
	private final GSETextColor color;
	private final boolean obfuscated;
	private final boolean bold;
	private final boolean strikethrough;
	private final boolean underline;
	private final boolean italic;
	private final boolean reset;
	
	public GSStyle(GSETextColor color, boolean obfuscated, boolean bold,
	               boolean strikethrough, boolean underline, boolean italic) {
		this.color = color;
		this.obfuscated = obfuscated;
		this.bold = bold;
		this.strikethrough = strikethrough;
		this.underline = underline;
		this.italic = italic;
		this.reset = false;
	}
	
	public GSStyle(boolean reset) {
		this.color = null;
		this.reset = reset;
		
		obfuscated = bold = strikethrough = underline = italic = false;
	}

	public GSETextColor getColor() {
		return color;
	}
	
	public boolean isObfuscated() {
		return obfuscated;
	}
	
	public boolean isBold() {
		return bold;
	}
	
	public boolean isStrikethrough() {
		return strikethrough;
	}
	
	public boolean isUnderline() {
		return underline;
	}
	
	public boolean isItalic() {
		return italic;
	}
	
	public boolean isReset() {
		return reset;
	}
	
	public GSStyle withColor(GSETextColor newColor) {
		return new GSStyle(newColor, obfuscated, bold, strikethrough, underline, italic);
	}
	
	public GSStyle withObfuscated(boolean newObfuscated) {
		return new GSStyle(color, newObfuscated, bold, strikethrough, underline, italic);
	}

	public GSStyle withBold(boolean newBold) {
		return new GSStyle(color, obfuscated, newBold, strikethrough, underline, italic);
	}
	
	public GSStyle withStrikethrough(boolean newStrikethrough) {
		return new GSStyle(color, obfuscated, bold, newStrikethrough, underline, italic);
	}

	public GSStyle withUnderline(boolean newUnderline) {
		return new GSStyle(color, obfuscated, bold, strikethrough, newUnderline, italic);
	}

	public GSStyle withItalic(boolean newItalic) {
		return new GSStyle(color, obfuscated, bold, strikethrough, underline, newItalic);
	}
	
	private void appendFormatting(char formatCode, StringBuilder dst) {
		dst.append(STYLING_CHAR);
		dst.append(formatCode);
	}

	/* Visible for GSText */
	void build(StringBuilder dst) {
		if (reset) {
			appendFormatting(RESET_FORMAT_CODE, dst);
			return;
		}
		if (color != null)
			appendFormatting(color.getFormatCode(), dst);
		if (obfuscated)
			appendFormatting(OBFUSCATED_FORMAT_CODE, dst);
		if (bold)
			appendFormatting(BOLD_FORMAT_CODE, dst);
		if (strikethrough)
			appendFormatting(STRIKETHROUGH_FORMAT_CODE, dst);
		if (underline)
			appendFormatting(UNDERLINE_FORMAT_CODE, dst);
		if (italic)
			appendFormatting(ITALIC_FORMAT_CODE, dst);
	}
}
