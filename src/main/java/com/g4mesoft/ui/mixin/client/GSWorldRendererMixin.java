package com.g4mesoft.ui.mixin.client;

import java.util.Collection;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.At.Shift;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Slice;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.g4mesoft.ui.G4mespeedUIMod;
import com.g4mesoft.ui.renderer.GSBasicRenderer3D;
import com.g4mesoft.ui.renderer.GSERenderPhase;
import com.g4mesoft.ui.renderer.GSIRenderable3D;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.BufferBuilderStorage;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.render.LightmapTextureManager;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.WorldRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.Matrix4f;

@Mixin(WorldRenderer.class)
public abstract class GSWorldRendererMixin {

	@Shadow @Final private MinecraftClient client;
	
	@Unique
	private GSBasicRenderer3D gs_renderer3d;

	@Inject(
		method = "<init>",
		at = @At("RETURN")
	)
	private void onInit(MinecraftClient client, BufferBuilderStorage builderStorage, CallbackInfo ci) {
		gs_renderer3d = new GSBasicRenderer3D();
	}
	
	@Inject(
		method = "render",
		slice = @Slice(
			from = @At(
				value = "INVOKE",
				ordinal = 0,
				shift = Shift.BEFORE,
				target =
					"Lnet/minecraft/client/render/WorldRenderer;renderWorldBorder(" +
						"Lnet/minecraft/client/render/Camera;" +
					")V"
			),
			to = @At(
				value = "INVOKE",
				ordinal = 1,
				shift = Shift.AFTER,
				target =
					"Lnet/minecraft/client/render/WorldRenderer;renderWorldBorder(" +
						"Lnet/minecraft/client/render/Camera;" +
					")V"
			)
		), 
		at = @At(
			value = "INVOKE",
			shift = Shift.BEFORE,
			target = "Lnet/minecraft/client/gl/ShaderEffect;render(F)V"
		)
	)
	private void onRenderTransparentLastFabulous(MatrixStack matrixStack, float tickDelta, long limitTime, boolean renderBlockOutline, Camera camera, GameRenderer gameRenderer, LightmapTextureManager lightmapTextureManager, Matrix4f matrix4f, CallbackInfo ci) {
		if (MinecraftClient.isFabulousGraphicsOrBetter())
			client.worldRenderer.getTranslucentFramebuffer().beginWrite(false);
		
		handleOnRenderTransparentLast(matrixStack);
		
		if (MinecraftClient.isFabulousGraphicsOrBetter())
            client.getFramebuffer().beginWrite(false);
	}
	
	@Inject(
		method = "render",
		at = @At(
			value = "INVOKE",
			ordinal = 1,
			shift = Shift.AFTER,
			target =
				"Lnet/minecraft/client/render/WorldRenderer;renderWorldBorder(" +
					"Lnet/minecraft/client/render/Camera;" +
				")V"
		)
	)
	private void onRenderTransparentLastDefault(MatrixStack matrixStack, float tickDelta, long limitTime, boolean renderBlockOutline, Camera camera, GameRenderer gameRenderer, LightmapTextureManager lightmapTextureManager, Matrix4f matrix4f, CallbackInfo ci) {
		handleOnRenderTransparentLast(matrixStack);
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
			RenderSystem.disableTexture();
			
			// View matrix is already uploaded to shader uniform
			matrixStack.push();
			matrixStack.loadIdentity();
			
			gs_renderer3d.begin(Tessellator.getInstance().getBuffer(), matrixStack);
			for (GSIRenderable3D renderable : renderables) {
				if (renderable.getRenderPhase() == GSERenderPhase.TRANSPARENT_LAST)
					renderable.render(gs_renderer3d);
			}
			gs_renderer3d.end();

			matrixStack.pop();
	
			RenderSystem.enableTexture();
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
