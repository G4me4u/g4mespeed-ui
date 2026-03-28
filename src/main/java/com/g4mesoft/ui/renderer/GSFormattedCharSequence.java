package com.g4mesoft.ui.renderer;

import net.minecraft.network.chat.Style;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.util.FormattedCharSink;

public class GSFormattedCharSequence implements FormattedCharSequence {

	private final CharSequence sequence;
	private final Style style;

	public GSFormattedCharSequence(CharSequence sequence) {
		this(sequence, Style.EMPTY);
	}
	
	public GSFormattedCharSequence(CharSequence sequence, Style style) {
		this.sequence = sequence;
		this.style = style;
	}
	
	@Override
	public boolean accept(FormattedCharSink sink) {
		for (int i = 0, len = sequence.length(); i < len; ) {
			char c = sequence.charAt(i);
			if (Character.isHighSurrogate(c) && i + 1 < len) {
				// Character is UTF-16, and we have a trailing surrogate.
				char low = sequence.charAt(i + 1);
				int codePoint = Character.toCodePoint(c, low);
				if (!sink.accept(i, style, codePoint))
					return false;
				i += 2;
			} else {
				// Character is not UTF-16, simply cast it.
				if (!sink.accept(i, style, (int)c))
					return false;
				i++;
			}
		}
		return true;
	}
}
