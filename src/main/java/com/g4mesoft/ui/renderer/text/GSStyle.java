package com.g4mesoft.ui.renderer.text;

import net.minecraft.text.Formatting;

public class GSStyle {

	public static final GSStyle EMPTY = new GSStyle(false);
	public static final GSStyle RESET = new GSStyle(true);

	/* Visible for GSText */
	static final char STYLING_CHAR = '\u00A7';
	
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
	
	private void appendFormatting(Formatting formatting, StringBuilder dst) {
		dst.append(STYLING_CHAR);
		dst.append(formatting.getCode());
	}

	/* Visible for GSText */
	void build(StringBuilder dst) {
		if (reset) {
			appendFormatting(Formatting.RESET, dst);
			return;
		}
		if (color != null)
			appendFormatting(color.getFormatting(), dst);
		if (obfuscated)
			appendFormatting(Formatting.OBFUSCATED, dst);
		if (bold)
			appendFormatting(Formatting.BOLD, dst);
		if (strikethrough)
			appendFormatting(Formatting.STRIKETHROUGH, dst);
		if (underline)
			appendFormatting(Formatting.UNDERLINE, dst);
		if (italic)
			appendFormatting(Formatting.ITALIC, dst);
	}
}
