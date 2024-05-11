package com.g4mesoft.ui.mixin.client;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import net.minecraft.client.gl.PostEffectProcessor;
import net.minecraft.client.render.GameRenderer;

@Mixin(GameRenderer.class)
public interface GSIGameRendererAccess {

	@Accessor("blurPostProcessor")
	public PostEffectProcessor getBlurPostProcessor();
	
}
