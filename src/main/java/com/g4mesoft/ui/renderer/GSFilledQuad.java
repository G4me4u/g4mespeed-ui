package com.g4mesoft.ui.renderer;

import org.jetbrains.annotations.Nullable;

import com.mojang.blaze3d.pipeline.RenderPipeline;

import net.minecraft.client.gl.RenderPipelines;
import net.minecraft.client.gui.ScreenRect;
import net.minecraft.client.gui.render.state.SimpleGuiElementRenderState;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.texture.TextureSetup;

public class GSFilledQuad implements SimpleGuiElementRenderState {

	private final int x0, y0, x1, y1;
	private final int tlColor, trColor, blColor, brColor;
	private final boolean mirror;
	private final @Nullable ScreenRect scissorArea;
	private final ScreenRect layerBounds;

	GSFilledQuad(int x, int y, int width, int height,
	             int tlColor, int trColor, int blColor, int brColor,
	             boolean mirror,
	             @Nullable ScreenRect scissorArea,
	             ScreenRect layerBounds) {

		this.x0 = x;
		this.y0 = y;
		this.x1 = x + width;
		this.y1 = y + height;
		
		this.tlColor = tlColor;
		this.trColor = trColor;
		this.blColor = blColor;
		this.brColor = brColor;
		
		this.mirror = mirror;
		
		this.scissorArea = scissorArea;
		this.layerBounds = layerBounds;
	}
	
	@Override
	public void setupVertices(VertexConsumer vertices) {
		if (mirror) {
			vertices.vertex(x0, y0, 0.0f).color(tlColor);
			vertices.vertex(x0, y1, 0.0f).color(blColor);
			vertices.vertex(x1, y1, 0.0f).color(brColor);
			vertices.vertex(x1, y0, 0.0f).color(trColor);
		} else {
			vertices.vertex(x0, y1, 0.0f).color(blColor);
			vertices.vertex(x1, y1, 0.0f).color(brColor);
			vertices.vertex(x1, y0, 0.0f).color(trColor);
			vertices.vertex(x0, y0, 0.0f).color(tlColor);
		}
	}
	
	@Override
	public ScreenRect bounds() {
		return layerBounds;
	}
	
	@Override
	public RenderPipeline pipeline() {
		return RenderPipelines.GUI;
	}

	@Override
	public TextureSetup textureSetup() {
		return TextureSetup.empty();
	}

	@Override
	public ScreenRect scissorArea() {
		return scissorArea;
	}
}
