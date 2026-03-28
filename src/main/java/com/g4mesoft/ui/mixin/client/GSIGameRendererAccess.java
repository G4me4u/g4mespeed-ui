package com.g4mesoft.ui.mixin.client;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.PostChain;

@Mixin(GameRenderer.class)
public interface GSIGameRendererAccess {

	@Accessor("blurEffect")
	public PostChain getBlurEffect();
	
}
