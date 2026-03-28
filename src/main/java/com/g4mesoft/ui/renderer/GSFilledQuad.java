package com.g4mesoft.ui.renderer;

import org.jetbrains.annotations.Nullable;

import com.mojang.blaze3d.pipeline.RenderPipeline;
import com.mojang.blaze3d.vertex.VertexConsumer;

import net.minecraft.client.gui.navigation.ScreenRectangle;
import net.minecraft.client.gui.render.TextureSetup;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.client.renderer.state.gui.GuiElementRenderState;

public class GSFilledQuad implements GuiElementRenderState {

	private final int x0, y0, x1, y1;
	private final int tlColor, trColor, blColor, brColor;
	private final boolean mirror;
	private final @Nullable ScreenRectangle scissorArea;
	private final ScreenRectangle layerBounds;

	GSFilledQuad(int x, int y, int width, int height,
	             int tlColor, int trColor, int blColor, int brColor,
	             boolean mirror,
	             @Nullable ScreenRectangle scissorArea,
	             ScreenRectangle layerBounds) {

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
	public void buildVertices(VertexConsumer vertices) {
		if (mirror) {
			vertices.addVertex(x0, y0, 0.0f).setColor(tlColor);
			vertices.addVertex(x0, y1, 0.0f).setColor(blColor);
			vertices.addVertex(x1, y1, 0.0f).setColor(brColor);
			vertices.addVertex(x1, y0, 0.0f).setColor(trColor);
		} else {
			vertices.addVertex(x0, y1, 0.0f).setColor(blColor);
			vertices.addVertex(x1, y1, 0.0f).setColor(brColor);
			vertices.addVertex(x1, y0, 0.0f).setColor(trColor);
			vertices.addVertex(x0, y0, 0.0f).setColor(tlColor);
		}
	}
	
	@Override
	public ScreenRectangle bounds() {
		return layerBounds;
	}
	
	@Override
	public RenderPipeline pipeline() {
		return RenderPipelines.GUI;
	}

	@Override
	public TextureSetup textureSetup() {
		return TextureSetup.noTexture();
	}

	@Override
	public ScreenRectangle scissorArea() {
		return scissorArea;
	}
}
