package com.g4mesoft.ui.mixin.client;

import java.util.List;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import com.g4mesoft.ui.renderer.GSRenderPipelines;
import com.mojang.blaze3d.pipeline.RenderPipeline;

import net.minecraft.client.gl.RenderPipelines;

@Mixin(RenderPipelines.class)
public class GSRenderPipelinesMixin {

	@Inject(
		method = "getAll",
		at = @At("HEAD")
	)
	private static void onGetAllBeforeHead(CallbackInfoReturnable<List<RenderPipeline>> cir) {
		RenderPipelines.register(GSRenderPipelines.POSITION_COLOR_QUADS);
		RenderPipelines.register(GSRenderPipelines.POSITION_COLOR_LINES);
	}
}
