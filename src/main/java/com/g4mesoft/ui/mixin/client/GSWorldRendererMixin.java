package com.g4mesoft.ui.mixin.client;

import java.util.Collection;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.g4mesoft.ui.G4mespeedUIMod;
import com.g4mesoft.ui.renderer.GSBasicRenderer3D;
import com.g4mesoft.ui.renderer.GSERenderPhase;
import com.g4mesoft.ui.renderer.GSIRenderable3D;
import com.mojang.blaze3d.buffers.GpuBufferSlice;
import com.mojang.blaze3d.systems.RenderSystem;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.BufferBuilderStorage;
import net.minecraft.client.render.DefaultFramebufferSet;
import net.minecraft.client.render.FrameGraphBuilder;
import net.minecraft.client.render.FramePass;
import net.minecraft.client.render.WorldRenderer;
import net.minecraft.client.render.block.entity.BlockEntityRenderDispatcher;
import net.minecraft.client.render.entity.EntityRenderDispatcher;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.Vec3d;

@Mixin(WorldRenderer.class)
public abstract class GSWorldRendererMixin {

	@Shadow @Final private MinecraftClient client;
	@Shadow @Final private DefaultFramebufferSet framebufferSet;
	@Shadow @Final private BufferBuilderStorage bufferBuilders;
	
	@Unique
	private GSBasicRenderer3D gs_renderer3d;

	@Inject(
		method = "<init>",
		at = @At("RETURN")
	)
	private void onInit(MinecraftClient client, EntityRenderDispatcher entityRenderDispatcher, BlockEntityRenderDispatcher blockEntityRenderDispatcher, BufferBuilderStorage bufferBuilders, CallbackInfo ci) {
		gs_renderer3d = new GSBasicRenderer3D();
	}
	
	@Inject(
		method = "renderWeather",
		at = @At("RETURN")
	)
	private void onRenderWeatherReturn(FrameGraphBuilder frameGraphBuilder, Vec3d pos, float tickDelta, GpuBufferSlice fog, CallbackInfo ci) {
		Collection<GSIRenderable3D> renderables = G4mespeedUIMod.getRenderables();
		
		if (hasRenderPhase(renderables, GSERenderPhase.TRANSPARENT_LAST)) {
			FramePass framePass = frameGraphBuilder.createPass("gsTranslucent");
			if (framebufferSet.translucentFramebuffer != null) {
				framebufferSet.translucentFramebuffer = framePass.transfer(framebufferSet.translucentFramebuffer);
			} else {
				framebufferSet.mainFramebuffer = framePass.transfer(framebufferSet.mainFramebuffer);
			}

			framePass.setRenderer(() -> {
				RenderSystem.setShaderFog(fog);
				
				gs_renderer3d.begin(bufferBuilders.getEntityVertexConsumers(), new MatrixStack());
				for (GSIRenderable3D renderable : renderables) {
					if (renderable.getRenderPhase() == GSERenderPhase.TRANSPARENT_LAST)
						renderable.render(gs_renderer3d);
				}
				gs_renderer3d.end();
			});
		}
	}
	
	@Unique
	private boolean hasRenderPhase(Collection<GSIRenderable3D> renderables, GSERenderPhase phase) {
		for (GSIRenderable3D renderable : renderables) {
			if (renderable.getRenderPhase() == phase)
				return true;
		}

		return false;
	}
}
