package com.g4mesoft.ui.mixin.client;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import com.mojang.blaze3d.resource.CrossFrameResourcePool;

import net.minecraft.client.renderer.GameRenderer;

@Mixin(GameRenderer.class)
public interface GSIGameRendererAccess {

	@Accessor("resourcePool")
	public CrossFrameResourcePool getResourcePool();
	
}
