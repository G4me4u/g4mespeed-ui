package com.g4mesoft.ui.renderer;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.Tessellator;
import com.mojang.blaze3d.vertex.VertexFormat;

public class GSBasicRenderer3D implements GSIRenderer3D {

	private BufferBuilder builder;
	
	private boolean building;
	private int buildingShape;
	
	public void begin(BufferBuilder builder) {
		this.builder = builder;
	}
	
	public void end() {
		if (building)
			throw new IllegalStateException("Renderer is still building");

		builder = null;
	}
	
	@Override
	public void pushMatrix() {
		GlStateManager.pushMatrix();
	}

	@Override
	public void popMatrix() {
		GlStateManager.popMatrix();
	}

	@Override
	public void translate(float tx, float ty, float tz) {
		GlStateManager.translatef(tx, ty, tz);
	}

	@Override
	public void rotate(float rx, float ry, float rz) {
		GlStateManager.rotatef(rx, 1.0f, 0.0f, 0.0f);
		GlStateManager.rotatef(ry, 0.0f, 1.0f, 0.0f);
		GlStateManager.rotatef(rz, 0.0f, 0.0f, 1.0f);
	}

	@Override
	public void scale(float sx, float sy, float sz) {
		GlStateManager.scalef(sx, sy, sz);
	}
	
	@Override
	public void fillCuboid(float x0, float y0, float z0,
	                       float x1, float y1, float z1,
	                       float r, float g, float b, float a) {
		
		if (building && buildingShape != QUADS)
			throw new IllegalStateException("Building quads is required!");
		
		boolean wasBuilding = building;
		if (!wasBuilding)
			build(QUADS, DefaultVertexFormat.POSITION_COLOR);
		
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

	@Override
	public void drawCuboidOutline(float x0, float y0, float z0,
	                              float x1, float y1, float z1,
	                              float r, float g, float b, float a) {
		
		if (building && buildingShape != LINES)
			throw new IllegalStateException("Building lines is required!");
		
		boolean wasBuilding = building;
		if (!wasBuilding)
			build(LINES, DefaultVertexFormat.POSITION_COLOR);
		
		// Lines on X-axis
		vert(x0, y0, z0).color(r, g, b, a).next();
		vert(x1, y0, z0).color(r, g, b, a).next();
		vert(x0, y1, z0).color(r, g, b, a).next();
		vert(x1, y1, z0).color(r, g, b, a).next();
		vert(x0, y1, z1).color(r, g, b, a).next();
		vert(x1, y1, z1).color(r, g, b, a).next();
		vert(x0, y0, z1).color(r, g, b, a).next();
		vert(x1, y0, z1).color(r, g, b, a).next();

		// Lines on Y-axis
		vert(x0, y0, z0).color(r, g, b, a).next();
		vert(x0, y1, z0).color(r, g, b, a).next();
		vert(x1, y0, z0).color(r, g, b, a).next();
		vert(x1, y1, z0).color(r, g, b, a).next();
		vert(x1, y0, z1).color(r, g, b, a).next();
		vert(x1, y1, z1).color(r, g, b, a).next();
		vert(x0, y0, z1).color(r, g, b, a).next();
		vert(x0, y1, z1).color(r, g, b, a).next();

		// Lines on Z-axis
		vert(x0, y0, z0).color(r, g, b, a).next();
		vert(x0, y0, z1).color(r, g, b, a).next();
		vert(x1, y0, z0).color(r, g, b, a).next();
		vert(x1, y0, z1).color(r, g, b, a).next();
		vert(x1, y1, z0).color(r, g, b, a).next();
		vert(x1, y1, z1).color(r, g, b, a).next();
		vert(x0, y1, z0).color(r, g, b, a).next();
		vert(x0, y1, z1).color(r, g, b, a).next();
		
		if (!wasBuilding)
			finish();
	}

	@Override
	public void build(int shape, VertexFormat format) {
		if (building)
			throw new IllegalStateException("Already building!");
		
		builder.begin(shape, format);
		
		buildingShape = shape;
		building = true;
	}

	@Override
	public GSBasicRenderer3D vert(float x, float y, float z) {
		builder.vertex(x, y, z);
		return this;
	}

	@Override
	public GSBasicRenderer3D color(float r, float g, float b, float a) {
		builder.color(r, g, b, a);
		return this;
	}

	@Override
	public GSBasicRenderer3D tex(float u, float v) {
		builder.texture(u, v);
		return this;
	}

	@Override
	public GSBasicRenderer3D next() {
		builder.nextVertex();
		return this;
	}
	
	@Override
	public void finish() {
		if (!building)
			throw new IllegalStateException("Not building!");
		
		Tessellator.getInstance().end();
		building = false;
	}
}
