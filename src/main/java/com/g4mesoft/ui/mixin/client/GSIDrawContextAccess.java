package com.g4mesoft.ui.mixin.client;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

import com.mojang.blaze3d.pipeline.RenderPipeline;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.util.Identifier;

@Mixin(DrawContext.class)
public interface GSIDrawContextAccess {

	@Invoker("drawTexturedQuad")
	public void gs_drawTexturedQuad(RenderPipeline pipeline, Identifier sprite, int x1, int x2, int y1, int y2, float u1, float u2, float v1, float v2, int color);

}
