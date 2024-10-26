package com.g4mesoft.ui.mixin.client;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.util.Pool;

@Mixin(GameRenderer.class)
public interface GSIGameRendererAccess {

	@Accessor("pool")
	public Pool getPool();
	
}
