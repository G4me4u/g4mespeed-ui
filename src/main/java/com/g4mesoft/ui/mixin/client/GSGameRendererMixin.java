package com.g4mesoft.ui.mixin.client;

import java.util.Collection;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL14;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.At.Shift;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.g4mesoft.ui.G4mespeedUIMod;
import com.g4mesoft.ui.renderer.GSBasicRenderer3D;
import com.g4mesoft.ui.renderer.GSERenderPhase;
import com.g4mesoft.ui.renderer.GSIRenderable3D;
import com.mojang.blaze3d.vertex.BufferBuilder;

import net.minecraft.client.Minecraft;
import net.minecraft.client.render.GameRenderer;

@Mixin(GameRenderer.class)
public abstract class GSGameRendererMixin {

	@Shadow @Final private Minecraft minecraft;
	
	@Unique
	private GSBasicRenderer3D gs_renderer3d;

	@Inject(
		method = "<init>",
		at = @At("RETURN")
	)
	private void onInit(Minecraft client, CallbackInfo ci) {
		gs_renderer3d = new GSBasicRenderer3D();
	}
	
	@Inject(
		method = "renderWorld(FJ)V",
		allow = 1,
		at = @At(
			value = "INVOKE",
			shift = Shift.AFTER,
			target =
				"Lnet/minecraft/client/render/GameRenderer;renderSnowAndRain(" +
					"F" +
				")V"
		)
	)
	private void onRenderTransparentLastDefault(CallbackInfo ci) {
		handleOnRenderTransparentLast();
	}

	@Unique
	private void handleOnRenderTransparentLast() {
		Collection<GSIRenderable3D> renderables = G4mespeedUIMod.getRenderables();
		
		if (hasRenderPhase(renderables, GSERenderPhase.TRANSPARENT_LAST)) {
			// Rendering world border sometimes has depth and
			// depth mask disabled. Fix it here.
			GL11.glDepthMask(true);
			GL11.glEnable(GL11.GL_DEPTH_TEST);

			// Sometimes face culling is disabled
			GL11.glEnable(GL11.GL_CULL_FACE);

			GL11.glEnable(GL11.GL_BLEND);
			GL14.glBlendFuncSeparate(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA, GL11.GL_ONE, GL11.GL_ZERO);
			GL11.glShadeModel(GL11.GL_SMOOTH);
			GL11.glDisable(GL11.GL_TEXTURE_2D);

			// Fix model matrix
			GL11.glPushMatrix();
			GL11.glLoadIdentity();

			gs_renderer3d.begin(BufferBuilder.INSTANCE);
			for (GSIRenderable3D renderable : renderables) {
				if (renderable.getRenderPhase() == GSERenderPhase.TRANSPARENT_LAST)
					renderable.render(gs_renderer3d);
			}
			gs_renderer3d.end();

			GL11.glPopMatrix();

			GL11.glShadeModel(GL11.GL_FLAT);
			GL11.glEnable(GL11.GL_TEXTURE_2D);
			GL11.glDisable(GL11.GL_BLEND);
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
