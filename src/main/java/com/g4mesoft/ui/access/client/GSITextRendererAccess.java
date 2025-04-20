package com.g4mesoft.ui.access.client;

import net.minecraft.client.render.font.FontSet;

public interface GSITextRendererAccess {

	public FontSet getFonts();

	public void setEscapeTextFlag(boolean escapeFormatting);

}
