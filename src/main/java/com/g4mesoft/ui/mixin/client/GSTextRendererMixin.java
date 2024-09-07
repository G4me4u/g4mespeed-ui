package com.g4mesoft.ui.mixin.client;

import org.joml.Matrix4f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.At.Shift;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.g4mesoft.ui.G4mespeedUIMod;
import com.g4mesoft.ui.access.client.GSIBufferBuilderAccess;

import net.minecraft.client.font.GlyphRenderer;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.render.VertexConsumer;

@Mixin(TextRenderer.class)
public class GSTextRendererMixin {

	@Inject(
		method = "drawGlyph",
		expect = 2,
		at = @At(
			value = "INVOKE",
			shift = Shift.AFTER,
			target =
				"Lnet/minecraft/client/font/GlyphRenderer;draw(" +
					"ZFF" +
					"Lorg/joml/Matrix4f;" +
					"Lnet/minecraft/client/render/VertexConsumer;" +
					"FFFFI" +
				")V"
		)
	)
    private void drawGlyph(GlyphRenderer glyphRenderer, boolean bold, boolean italic,
                           float weight, float x, float y, Matrix4f matrix,
                           VertexConsumer vertexConsumer, float red, float green,
                           float blue, float alpha, int light, CallbackInfo ci)
	{
		if (G4mespeedUIMod.isSodiumLoaded())
			((GSIBufferBuilderAccess)vertexConsumer).gs_clipPreviousShape();
	}
}
