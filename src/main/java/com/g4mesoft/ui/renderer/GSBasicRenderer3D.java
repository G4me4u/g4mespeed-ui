package com.g4mesoft.ui.renderer;

import org.joml.Quaternionf;

import com.g4mesoft.ui.util.GSColorUtil;
import com.mojang.blaze3d.PrimitiveTopology;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.blaze3d.vertex.VertexFormat;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.rendertype.RenderType;

public class GSBasicRenderer3D implements GSIRenderer3D {

	private RenderType renderType;
	private PoseStack poseStack;
	private VertexConsumer buffer;

	public GSBasicRenderer3D(RenderType renderType, PoseStack poseStack, VertexConsumer buffer) {
		this.renderType = renderType;
		this.poseStack = poseStack;
		this.buffer = buffer;
	}

	@Override
	public void pushMatrix() {
		poseStack.pushPose();
	}

	@Override
	public void popMatrix() {
		poseStack.popPose();
	}

	@Override
	public void identity() {
		poseStack.setIdentity();
	}
	
	@Override
	public void translate(float tx, float ty, float tz) {
		poseStack.translate(tx, ty, tz);
	}

	@Override
	public void rotate(float rx, float ry, float rz) {
		poseStack.mulPose(new Quaternionf().rotateXYZ(rx, ry, rz));
	}

	@Override
	public void scale(float sx, float sy, float sz) {
		poseStack.scale(sx, sy, sz);
	}
	
	@Override
	public void fillCuboid(float x0, float y0, float z0,
	                       float x1, float y1, float z1,
	                       float r, float g, float b, float a) {

		if (!isBuilding(PrimitiveTopology.QUADS, DefaultVertexFormat.POSITION_COLOR))
			throw new IllegalStateException("Building quads is required!");
		
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
		
		if (!isBuilding(PrimitiveTopology.LINES, DefaultVertexFormat.POSITION_COLOR))
			throw new IllegalStateException("Building lines is required!");
		
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
	}

	@Override
	public GSBasicRenderer3D vert(float x, float y, float z) {
		buffer.addVertex(poseStack.last().pose(), x, y, z);
		return this;
	}

	@Override
	public GSBasicRenderer3D color(float r, float g, float b, float a) {
		buffer.setColor(r, g, b, a);
		return this;
	}

	@Override
	public GSBasicRenderer3D color(int argb) {
		buffer.setColor(argb);
		return this;
	}

	@Override
	public GSBasicRenderer3D tex(float u, float v) {
		buffer.setUv(u, v);
		return this;
	}

	@Override
	public GSBasicRenderer3D normal(float nx, float ny, float nz) {
		buffer.setNormal(poseStack.last(), nx, ny, nz);
		return this;
	}

	@Override
	public GSBasicRenderer3D lineWidth(float lw) {
		buffer.setLineWidth(lw);
		return this;
	}

	@Override
	public GSBasicRenderer3D next() {
		// Note: BufferBuilder#next has been removed.
		return this;
	}
	
	@Override
	public boolean isBuilding(PrimitiveTopology drawMode, VertexFormat format) {
		if (renderType.primitiveTopology() != drawMode)
			return false;
		if (!renderType.format().equals(format))
			return false;
		return true;
	}
}
