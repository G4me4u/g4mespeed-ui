package com.g4mesoft.ui.mixin.client;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.render.VertexConsumerProvider;

@Mixin(DrawContext.class)
public interface GSIDrawContextAccess {

	@Accessor("vertexConsumers")
	public VertexConsumerProvider.Immediate gs_getVertexConsumers();

}
