package com.g4mesoft.ui.renderer;

import org.joml.Quaternionf;

import com.g4mesoft.ui.util.GSColorUtil;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.blaze3d.vertex.VertexFormat;
import com.mojang.blaze3d.vertex.VertexFormat.Mode;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.rendertype.RenderType;

public class GSBasicRenderer3D implements GSIRenderer3D {

	private PoseStack matrixStack;
	private MultiBufferSource.BufferSource vertexConsumers;
	
	private RenderType currentRenderLayer;
	private VertexConsumer currentVertexConsumer;
	
	public void begin(MultiBufferSource.BufferSource vertexConsumers, PoseStack matrixStack) {
		this.vertexConsumers = vertexConsumers;
		this.matrixStack = matrixStack;
	}
	
	public void end() {
		if (isBuilding())
			throw new IllegalStateException("Renderer is still building");

		vertexConsumers.endBatch();
		
		vertexConsumers = null;
		matrixStack = null;
	}
	
	@Override
	public void pushMatrix() {
		matrixStack.pushPose();
	}

	@Override
	public void popMatrix() {
		matrixStack.popPose();
	}

	@Override
	public void identity() {
		matrixStack.setIdentity();
	}
	
	@Override
	public void translate(float tx, float ty, float tz) {
		matrixStack.translate(tx, ty, tz);
	}

	@Override
	public void rotate(float rx, float ry, float rz) {
		matrixStack.mulPose(new Quaternionf().rotateXYZ(rx, ry, rz));
	}

	@Override
	public void scale(float sx, float sy, float sz) {
		matrixStack.scale(sx, sy, sz);
	}
	
	@Override
	public void fillCuboid(float x0, float y0, float z0,
	                       float x1, float y1, float z1,
	                       float r, float g, float b, float a) {

		if (isBuilding() && !isBuilding(Mode.QUADS, DefaultVertexFormat.POSITION_COLOR))
			throw new IllegalStateException("Building quads is required!");
		
		boolean wasBuilding = isBuilding();
		if (!wasBuilding)
			build(Mode.QUADS, DefaultVertexFormat.POSITION_COLOR);
		
		// Back Face
		vert(x0, y0, z0).color(r, g, b, a).next();
		vert(x0, y0, z1).color(r, g, b, a).next();
		vert(x0, y1, z1).color(r, g, b, a).next();
		vert(x0, y1, z0).color(r, g, b, a).next();

		// Front Face
		vert(x1, y0, z0).color(r, g, b, a).next();
		vert(x1, y1, z0).color(r, g, b, a).next();
		vert(x1, y1, z1).color(r, g, b, a).next();
		vert(x1, y0, z1).color(r, g, b, a).next();

		// Left Face
		vert(x0, y0, z0).color(r, g, b, a).next();
		vert(x0, y1, z0).color(r, g, b, a).next();
		vert(x1, y1, z0).color(r, g, b, a).next();
		vert(x1, y0, z0).color(r, g, b, a).next();

		// Right Face
		vert(x0, y0, z1).color(r, g, b, a).next();
		vert(x1, y0, z1).color(r, g, b, a).next();
		vert(x1, y1, z1).color(r, g, b, a).next();
		vert(x0, y1, z1).color(r, g, b, a).next();

		// Bottom Face
		vert(x0, y0, z0).color(r, g, b, a).next();
		vert(x1, y0, z0).color(r, g, b, a).next();
		vert(x1, y0, z1).color(r, g, b, a).next();
		vert(x0, y0, z1).color(r, g, b, a).next();

		// Top Face
		vert(x0, y1, z0).color(r, g, b, a).next();
		vert(x0, y1, z1).color(r, g, b, a).next();
		vert(x1, y1, z1).color(r, g, b, a).next();
		vert(x1, y1, z0).color(r, g, b, a).next();
		
		if (!wasBuilding)
			finish();
	}

	private final void drawLine(float x0, float y0, float z0,
	                            float x1, float y1, float z1,
	                            int argb, float lw) {
		vert(x0, y0, z0).normal(x1 - x0, y1 - y0, z1 - z0).color(argb).lineWidth(lw).next();
		vert(x1, y1, z1).normal(x1 - x0, y1 - y0, z1 - z0).color(argb).lineWidth(lw).next();
	}
	
	@Override
	public void drawCuboidOutline(float x0, float y0, float z0,
	                              float x1, float y1, float z1,
	                              float r, float g, float b, float a) {
		
		if (isBuilding() && !isBuilding(Mode.LINES, DefaultVertexFormat.POSITION_COLOR))
			throw new IllegalStateException("Building lines is required!");
		
		boolean wasBuilding = isBuilding();
		if (!wasBuilding)
			build(Mode.LINES, DefaultVertexFormat.POSITION_COLOR_NORMAL_LINE_WIDTH);
		
		float lw = Minecraft.getInstance().getWindow().getAppropriateLineWidth();
		int argb = GSColorUtil.denormalizeRGBA(r, g, b, a);

		// Lines on X-axis
		drawLine(x0, y0, z0, x1, y0, z0, argb, lw);
		drawLine(x0, y1, z0, x1, y1, z0, argb, lw);
		drawLine(x0, y1, z1, x1, y1, z1, argb, lw);
		drawLine(x0, y0, z1, x1, y0, z1, argb, lw);

		// Lines on Y-axis
		drawLine(x0, y0, z0, x0, y1, z0, argb, lw);
		drawLine(x1, y0, z0, x1, y1, z0, argb, lw);
		drawLine(x1, y0, z1, x1, y1, z1, argb, lw);
		drawLine(x0, y0, z1, x0, y1, z1, argb, lw);
		
		// Lines on Z-axis
		drawLine(x0, y0, z0, x0, y0, z1, argb, lw);
		drawLine(x1, y0, z0, x1, y0, z1, argb, lw);
		drawLine(x1, y1, z0, x1, y1, z1, argb, lw);
		drawLine(x0, y1, z0, x0, y1, z1, argb, lw);
		
		if (!wasBuilding)
			finish();
	}

	@Override
	public void build(Mode drawMode, VertexFormat format) {
		if (isBuilding())
			throw new IllegalStateException("Already building!");
		
		if (drawMode == QUADS && format == DefaultVertexFormat.POSITION_COLOR) {
			build(GSRenderLayers.POSITION_COLOR_QUADS);
		} else if (drawMode == LINES && format == DefaultVertexFormat.POSITION_COLOR_NORMAL_LINE_WIDTH) {
			build(GSRenderLayers.POSITION_COLOR_NORMAL_LINE_WIDTH_LINES);
		} else {
			throw new IllegalArgumentException("Unsupported draw mode and vertex format!");
		}
	}
	
	@Override
	public void build(RenderType renderLayer) {
		if (isBuilding())
			throw new IllegalStateException("Already building!");
	
		currentRenderLayer = renderLayer;
		// Retrieve appropriate buffer for render layer.
		currentVertexConsumer = vertexConsumers.getBuffer(renderLayer);
	}

	@Override
	public GSBasicRenderer3D vert(float x, float y, float z) {
		currentVertexConsumer.addVertex(matrixStack.last().pose(), x, y, z);
		return this;
	}

	@Override
	public GSBasicRenderer3D color(float r, float g, float b, float a) {
		currentVertexConsumer.setColor(r, g, b, a);
		return this;
	}

	@Override
	public GSBasicRenderer3D color(int argb) {
		currentVertexConsumer.setColor(argb);
		return this;
	}

	@Override
	public GSBasicRenderer3D tex(float u, float v) {
		currentVertexConsumer.setUv(u, v);
		return this;
	}

	@Override
	public GSBasicRenderer3D normal(float nx, float ny, float nz) {
		currentVertexConsumer.setNormal(matrixStack.last(), nx, ny, nz);
		return this;
	}

	@Override
	public GSBasicRenderer3D lineWidth(float lw) {
		currentVertexConsumer.setLineWidth(lw);
		return this;
	}

	@Override
	public GSBasicRenderer3D next() {
		// Note: BufferBuilder#next has been removed.
		return this;
	}
	
	@Override
	public void finish() {
		if (!isBuilding())
			throw new IllegalStateException("Not building!");
	
		currentRenderLayer = null;
		currentVertexConsumer = null;
	}
	
	@Override
	public boolean isBuilding() {
		return currentRenderLayer != null;
	}
		
	@Override
	public boolean isBuilding(Mode drawMode, VertexFormat format) {
		if (!isBuilding())
			return false;
		if (currentRenderLayer.mode() != drawMode)
			return false;
		if (!currentRenderLayer.format().equals(format))
			return false;
		return true;
	}
}
