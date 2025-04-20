package com.g4mesoft.ui.mixin.client;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

import com.g4mesoft.ui.access.client.GSITextRendererAccess;

import net.minecraft.client.render.TextRenderer;
import net.minecraft.client.render.font.FontSet;

@Mixin(TextRenderer.class)
public class GSTextRendererMixin implements GSITextRendererAccess {

	@Shadow @Final private FontSet fonts;
	
	@Unique
	private boolean gs_escapeFormatting;
	
	@ModifyConstant(
		method = "drawLayer",
		allow = 1,
		constant = @Constant(
			intValue = 167
		)
	)
	private int onDrawLayerModify167(int value) {
		return gs_escapeFormatting ? -1 : value;
	}
	
	@Override
	public FontSet getFonts() {
		return this.fonts;
	}

	@Override
	public void setEscapeTextFlag(boolean escapeFormatting) {
		gs_escapeFormatting = escapeFormatting;
	}
}
