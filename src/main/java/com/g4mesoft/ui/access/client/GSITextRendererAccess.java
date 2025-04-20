package com.g4mesoft.ui.access.client;

import net.minecraft.client.font.FontStorage;

public interface GSITextRendererAccess {

	public FontStorage getFontStorage();

	public void setEscapeTextFlag(boolean escapeFormatting);

}
