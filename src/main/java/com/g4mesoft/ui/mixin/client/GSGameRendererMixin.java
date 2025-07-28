package com.g4mesoft.ui.mixin.client;

import java.util.Collection;

import org.lwjgl.opengl.GL11;
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
import com.mojang.blaze3d.vertex.Tessellator;

import net.minecraft.client.Minecraft;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.resource.manager.ResourceManager;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.Vec3d;

@Mixin(GameRenderer.class)
public abstract class GSGameRendererMixin {

	@Shadow @Final private Minecraft minecraft;
	
	@Unique
	private GSBasicRenderer3D gs_renderer3d;

	@Inject(
		method = "<init>",
		at = @At("RETURN")
	)
	private void onInit(Minecraft client, ResourceManager resourceManager, CallbackInfo ci) {
		gs_renderer3d = new GSBasicRenderer3D();
	}
	
	@Inject(
		method = "render(IFJ)V",
		allow = 1,
		slice = @Slice(
			from = @At(
				value = "CONSTANT",
				args = "stringValue=translucent",
				shift = Shift.AFTER
			)
		),
		at = @At(
			value = "INVOKE",
			shift = Shift.AFTER,
			target =
				"Lnet/minecraft/client/render/world/WorldRenderer;render(" +
					"Lnet/minecraft/client/render/block/BlockLayer;" +
					"D" +
					"I" +
					"Lnet/minecraft/entity/Entity;" +
				")I"
		)
	)
	private void onRenderTransparentLastDefault(int anaglyphRenderPass, float tickDelta, long renderTimeLimit, CallbackInfo ci) {
		Entity camera = minecraft.getCamera();
		
		double cameraX = camera.prevTickX + (camera.x - camera.prevTickX) * tickDelta;
		double cameraY = camera.prevTickY + (camera.y - camera.prevTickY) * tickDelta;
		double cameraZ = camera.prevTickZ + (camera.z - camera.prevTickZ) * tickDelta;
	
		handleOnRenderTransparentLast(tickDelta, new Vec3d(cameraX, cameraY, cameraZ));
	}

	@Unique
	private void handleOnRenderTransparentLast(float tickDelta, Vec3d cameraPos) {
		Collection<GSIRenderable3D> renderables = G4mespeedUIMod.getRenderables();
		
		if (hasRenderPhase(renderables, GSERenderPhase.TRANSPARENT_LAST)) {
			// Rendering world border sometimes has depth and
			// depth mask disabled. Fix it here.
			GlStateManager.depthMask(true);
			GlStateManager.enableDepthTest();

			// Sometimes face culling is disabled
			GlStateManager.enableCull();
			
			GlStateManager.enableBlend();
			GlStateManager.blendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA,
					GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
			GlStateManager.shadeModel(GL11.GL_SMOOTH);
			GlStateManager.disableTexture();
			
			gs_renderer3d.begin(Tessellator.getInstance().getBuilder(), tickDelta, cameraPos);
			for (GSIRenderable3D renderable : renderables) {
				if (renderable.getRenderPhase() == GSERenderPhase.TRANSPARENT_LAST)
					renderable.render(gs_renderer3d);
			}
			gs_renderer3d.end();
	
			GlStateManager.enableTexture();
			GlStateManager.disableBlend();
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
