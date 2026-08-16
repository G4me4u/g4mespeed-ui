package com.g4mesoft.ui.mixin.client;

import org.joml.Matrix4fStack;
import org.joml.Matrix4fc;
import org.joml.Vector4f;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.At.Shift;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import com.g4mesoft.ui.G4mespeedUIMod;
import com.g4mesoft.ui.access.client.GSIPreparedFrameAccess;
import com.g4mesoft.ui.renderer.GSBasicRenderCollector3D;
import com.g4mesoft.ui.renderer.GSIRenderCollector3D;
import com.g4mesoft.ui.renderer.GSIRenderable3D;
import com.mojang.blaze3d.buffers.GpuBufferSlice;
import com.mojang.blaze3d.framegraph.FrameGraphBuilder;
import com.mojang.blaze3d.framegraph.FramePass;
import com.mojang.blaze3d.pipeline.RenderTarget;
import com.mojang.blaze3d.resource.GraphicsResourceAllocator;
import com.mojang.blaze3d.resource.RenderTargetDescriptor;
import com.mojang.blaze3d.resource.ResourceHandle;
import com.mojang.blaze3d.systems.RenderSystem;

import net.minecraft.client.CloudStatus;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.LevelTargetBundle;
import net.minecraft.client.renderer.PostChain;
import net.minecraft.client.renderer.SubmitNodeStorage;
import net.minecraft.client.renderer.chunk.ChunkSectionsToRender;
import net.minecraft.client.renderer.feature.FeatureRenderDispatcher;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.util.profiling.ProfilerFiller;

@Mixin(LevelRenderer.class)
public abstract class GSLevelRendererMixin {

	@Shadow @Final private SubmitNodeStorage submitNodeStorage;
	@Shadow @Final private LevelTargetBundle targets;

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

	@Inject(
		method = "render",
		locals = LocalCapture.CAPTURE_FAILHARD,
		at = @At(
			value = "INVOKE",
			shift = Shift.AFTER,
			target =
				"Lnet/minecraft/client/renderer/LevelRenderer;addWeatherPass(" +
					"Lcom/mojang/blaze3d/framegraph/FrameGraphBuilder;" +
					"Lcom/mojang/blaze3d/buffers/GpuBufferSlice;" +
				")V"
		)
	)
	private void onRenderAfterAddWeatherPass(final GraphicsResourceAllocator resourceAllocator, final DeltaTracker deltaTracker, final boolean renderOutline,
			final CameraRenderState cameraState, final Matrix4fc modelViewMatrix, final GpuBufferSlice terrainFog, final Vector4f fogColor,
			final boolean shouldRenderSky, CallbackInfo ci, float deltaPartialTick, ProfilerFiller profiler, Matrix4fStack modelViewStack,
			FeatureRenderDispatcher.PreparedFrame featureFrame, FrameGraphBuilder frame, int screenWidth, int screenHeight,
			RenderTargetDescriptor screenSizeTargetDescriptor, PostChain transparencyChain, FramePass clearPass,
			ChunkSectionsToRender chunkSectionsToRender, PostChain entityOutlineChain, CloudStatus cloudStatus) {
		if (((GSIPreparedFrameAccess)featureFrame).gs_hasTransparentLast()) {
			FramePass pass = frame.addPass("gs_transparent_last");
			this.targets.main = pass.readsAndWrites(this.targets.main);
			if (this.targets.translucent != null) {
				this.targets.translucent = pass.readsAndWrites(this.targets.translucent);
			}

			ResourceHandle<RenderTarget> mainTarget = this.targets.main;
			ResourceHandle<RenderTarget> translucentTarget = this.targets.translucent;
			pass.executes(() -> {
				RenderSystem.setShaderFog(terrainFog);

				if (translucentTarget != null) {
					translucentTarget.get().copyDepthFrom(mainTarget.get());
				}

				((GSIPreparedFrameAccess)featureFrame).gs_executeTransparentLast();
			});
		}
	}
}
