package com.g4mesoft.ui.mixin.client;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import com.mojang.blaze3d.vertex.VertexFormatElement;

@Mixin(VertexFormatElement.class)
public interface GSIVertexFormatElementAccess {

	@Accessor("count")
	public int getCount();
	
}
