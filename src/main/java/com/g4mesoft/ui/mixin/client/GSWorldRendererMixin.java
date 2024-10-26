package com.g4mesoft.ui.mixin.client;

import java.util.Collection;

import org.jetbrains.annotations.Nullable;
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
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gl.Framebuffer;
import net.minecraft.client.render.BufferBuilderStorage;
import net.minecraft.client.render.DefaultFramebufferSet;
import net.minecraft.client.render.Fog;
import net.minecraft.client.render.FrameGraphBuilder;
import net.minecraft.client.render.LightmapTextureManager;
import net.minecraft.client.render.RenderPass;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.WorldRenderer;
import net.minecraft.client.render.block.entity.BlockEntityRenderDispatcher;
import net.minecraft.client.render.entity.EntityRenderDispatcher;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.Vec3d;

@Mixin(WorldRenderer.class)
public abstract class GSWorldRendererMixin {

	@Shadow @Final private MinecraftClient client;
	@Shadow @Final private DefaultFramebufferSet framebufferSet;
	
	@Shadow @Nullable public abstract Framebuffer getTranslucentFramebuffer();
	
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
	private void onRenderWeatherReturn(FrameGraphBuilder frameGraphBuilder, LightmapTextureManager lightmapTextureManager, Vec3d pos, float tickDelta, Fog fog, CallbackInfo ci) {
		RenderPass renderPass = frameGraphBuilder.createPass("gsTranslucent");
		if (MinecraftClient.isFabulousGraphicsOrBetter() && framebufferSet.translucentFramebuffer != null) {
			framebufferSet.translucentFramebuffer = renderPass.transfer(framebufferSet.translucentFramebuffer);
		} else {
			framebufferSet.mainFramebuffer = renderPass.transfer(framebufferSet.mainFramebuffer);
		}
		renderPass.setRenderer(() -> {
			// See: RenderTarget.TRANSLUCENT_TARGET
			if (MinecraftClient.isFabulousGraphicsOrBetter()) {
				Framebuffer framebuffer = ((WorldRenderer)(Object)this).getTranslucentFramebuffer();
				if (framebuffer != null) {
					framebuffer.beginWrite(false);
				} else {
					client.getFramebuffer().beginWrite(false);
				}
			}
	
			handleOnRenderTransparentLast(new MatrixStack());
	
			if (MinecraftClient.isFabulousGraphicsOrBetter())
				client.getFramebuffer().beginWrite(false);
		});
	}
	
	@Unique
	private void handleOnRenderTransparentLast(MatrixStack matrixStack) {
		Collection<GSIRenderable3D> renderables = G4mespeedUIMod.getRenderables();
		
		if (hasRenderPhase(renderables, GSERenderPhase.TRANSPARENT_LAST)) {
			// Rendering world border sometimes has depth and
			// depth mask disabled. Fix it here.
			RenderSystem.depthMask(true);
			RenderSystem.enableDepthTest();

			// Sometimes face culling is disabled
			RenderSystem.enableCull();
			
			RenderSystem.enableBlend();
			if (MinecraftClient.isFabulousGraphicsOrBetter()) {
				// The Fabulous graphics setting seems to use a different blend func
				RenderSystem.blendFuncSeparate(GlStateManager.SrcFactor.SRC_ALPHA, GlStateManager.DstFactor.ONE_MINUS_SRC_ALPHA,
						GlStateManager.SrcFactor.ONE, GlStateManager.DstFactor.ONE_MINUS_SRC_ALPHA);
			} else {
				RenderSystem.defaultBlendFunc();
			}
			
			// View matrix is already uploaded to shader uniform
			matrixStack.push();
			matrixStack.loadIdentity();
			
			gs_renderer3d.begin(Tessellator.getInstance(), matrixStack);
			for (GSIRenderable3D renderable : renderables) {
				if (renderable.getRenderPhase() == GSERenderPhase.TRANSPARENT_LAST)
					renderable.render(gs_renderer3d);
			}
			gs_renderer3d.end();

			matrixStack.pop();
	
			RenderSystem.disableBlend();
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
