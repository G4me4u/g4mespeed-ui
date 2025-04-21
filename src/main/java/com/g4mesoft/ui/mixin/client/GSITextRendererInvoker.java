package com.g4mesoft.ui.mixin.client;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

import net.minecraft.client.render.TextRenderer;

@Mixin(TextRenderer.class)
public interface GSITextRendererInvoker {

	@Invoker("isColor")
	public static boolean gs_isColor(char c) {
		throw new AssertionError();
	}

	@Invoker("isFormatting")
	public static boolean gs_isFormatting(char c) {
		throw new AssertionError();
	}
}
