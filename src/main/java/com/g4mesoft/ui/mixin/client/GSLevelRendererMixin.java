package com.g4mesoft.ui.mixin.client;

import java.util.Collection;

import org.joml.Matrix4f;
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
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.Tesselator;

import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.RenderBuffers;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderDispatcher;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;

@Mixin(LevelRenderer.class)
public abstract class GSLevelRendererMixin {

	@Shadow @Final private Minecraft minecraft;
	
	@Unique
	private GSBasicRenderer3D gs_renderer3d;

	@Inject(
		method = "<init>",
		at = @At("RETURN")
	)
	private void onInit(Minecraft client, EntityRenderDispatcher entityRenderDispatcher, BlockEntityRenderDispatcher blockEntityRenderDispatcher, RenderBuffers bufferBuilders, CallbackInfo ci) {
		gs_renderer3d = new GSBasicRenderer3D();
	}
	
	@Inject(
		method = "renderLevel",
		slice = @Slice(
			from = @At(
				value = "INVOKE",
				ordinal = 0,
				shift = Shift.BEFORE,
				target =
					"Lnet/minecraft/client/renderer/LevelRenderer;renderWorldBorder(" +
						"Lnet/minecraft/client/Camera;" +
					")V"
			),
			to = @At(
				value = "INVOKE",
				ordinal = 1,
				shift = Shift.AFTER,
				target =
					"Lnet/minecraft/client/renderer/LevelRenderer;renderWorldBorder(" +
						"Lnet/minecraft/client/Camera;" +
					")V"
			)
		), 
		at = @At(
			value = "INVOKE",
			shift = Shift.BEFORE,
			target = "Lnet/minecraft/client/renderer/PostChain;process(F)V"
		)
	)
	private void onRenderTransparentLastFabulous(float tickDelta, long limitTime, boolean renderBlockOutline, Camera camera, GameRenderer gameRenderer, LightTexture lightmapTextureManager, Matrix4f matrix4f, Matrix4f matrix4f2, CallbackInfo ci) {
		if (Minecraft.useShaderTransparency())
			minecraft.levelRenderer.getTranslucentTarget().bindWrite(false);
		
		handleOnRenderTransparentLast(new PoseStack());
		
		if (Minecraft.useShaderTransparency())
			minecraft.getMainRenderTarget().bindWrite(false);
	}
	
	@Inject(
		method = "renderLevel",
		at = @At(
			value = "INVOKE",
			ordinal = 1,
			shift = Shift.AFTER,
			target =
				"Lnet/minecraft/client/renderer/LevelRenderer;renderWorldBorder(" +
					"Lnet/minecraft/client/Camera;" +
				")V"
		)
	)
	private void onRenderTransparentLastDefault(float tickDelta, long limitTime, boolean renderBlockOutline, Camera camera, GameRenderer gameRenderer, LightTexture lightmapTextureManager, Matrix4f matrix4f, Matrix4f matrix4f2, CallbackInfo ci) {
		handleOnRenderTransparentLast(new PoseStack());
	}

	@Unique
	private void handleOnRenderTransparentLast(PoseStack matrixStack) {
		Collection<GSIRenderable3D> renderables = G4mespeedUIMod.getRenderables();
		
		if (hasRenderPhase(renderables, GSERenderPhase.TRANSPARENT_LAST)) {
			// Rendering world border sometimes has depth and
			// depth mask disabled. Fix it here.
			RenderSystem.depthMask(true);
			RenderSystem.enableDepthTest();

			// Sometimes face culling is disabled
			RenderSystem.enableCull();
			
			RenderSystem.enableBlend();
			if (Minecraft.useShaderTransparency()) {
				// The Fabulous graphics setting seems to use a different blend func
				RenderSystem.blendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA,
						GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
			} else {
				RenderSystem.defaultBlendFunc();
			}
			
			// View matrix is already uploaded to shader uniform
			matrixStack.pushPose();
			matrixStack.setIdentity();
			
			gs_renderer3d.begin(Tesselator.getInstance().getBuilder(), matrixStack);
			for (GSIRenderable3D renderable : renderables) {
				if (renderable.getRenderPhase() == GSERenderPhase.TRANSPARENT_LAST)
					renderable.render(gs_renderer3d);
			}
			gs_renderer3d.end();

			matrixStack.popPose();
	
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
