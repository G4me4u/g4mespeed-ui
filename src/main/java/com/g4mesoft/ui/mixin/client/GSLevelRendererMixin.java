package com.g4mesoft.ui.mixin.client;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.At.Shift;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.g4mesoft.ui.G4mespeedUIMod;
import com.g4mesoft.ui.renderer.GSBasicRenderCollector3D;
import com.g4mesoft.ui.renderer.GSIRenderCollector3D;
import com.g4mesoft.ui.renderer.GSIRenderable3D;

import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.SubmitNodeStorage;

@Mixin(LevelRenderer.class)
public abstract class GSLevelRendererMixin {

	@Shadow @Final private SubmitNodeStorage submitNodeStorage;

	@Unique
	private GSIRenderCollector3D gs_renderCollector;

	@Inject(
		method = "<init>",
		at = @At("RETURN")
	)
	private void onInitReturn(CallbackInfo ci) {
		gs_renderCollector = new GSBasicRenderCollector3D(submitNodeStorage);
	}

	@Inject(
		method = "render",
		at = @At(
			value = "INVOKE",
			shift = Shift.AFTER,
			target =
				"Lnet/minecraft/client/renderer/LevelRenderer;submitFeatures(" +
					"Lnet/minecraft/client/renderer/state/level/LevelRenderState;" +
					"Lnet/minecraft/client/renderer/SubmitNodeCollector;" +
					"Z" +
				")V"
		)
	)
	private void onRenderAfterSubmitFeatures(CallbackInfo ci) {
		for (GSIRenderable3D renderable : G4mespeedUIMod.getRenderables())
			renderable.render(gs_renderCollector);
	}
}
