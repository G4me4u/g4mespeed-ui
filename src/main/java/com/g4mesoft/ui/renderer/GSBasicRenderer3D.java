package com.g4mesoft.ui.renderer;

import org.joml.Quaternionf;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.blaze3d.vertex.VertexFormat;
import com.mojang.blaze3d.vertex.VertexFormat.Mode;

import net.minecraft.client.renderer.GameRenderer;

public class GSBasicRenderer3D implements GSIRenderer3D {

	private BufferBuilder builder;
	private PoseStack matrixStack;
	
	private boolean building;
	private Mode buildingDrawMode;
	
	public void begin(BufferBuilder builder, PoseStack matrixStack) {
		this.builder = builder;
		this.matrixStack = matrixStack;
	}
	
	public void end() {
		if (building)
			throw new IllegalStateException("Renderer is still building");
		
		matrixStack = null;
		builder = null;
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
		
		if (building && buildingDrawMode != Mode.QUADS)
			throw new IllegalStateException("Building quads is required!");
		
		boolean wasBuilding = building;
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

	@Override
	public void drawCuboidOutline(float x0, float y0, float z0,
	                              float x1, float y1, float z1,
	                              float r, float g, float b, float a) {
		
		if (building && buildingDrawMode != Mode.LINES)
			throw new IllegalStateException("Building lines is required!");
		
		boolean wasBuilding = building;
		if (!wasBuilding)
			build(Mode.LINES, DefaultVertexFormat.POSITION_COLOR);
		
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
	public void build(Mode drawMode, VertexFormat format) {
		if (building)
			throw new IllegalStateException("Already building!");
		
		if (format == DefaultVertexFormat.POSITION) {
			RenderSystem.setShader(GameRenderer::getPositionShader);
		} else if (format == DefaultVertexFormat.POSITION_COLOR) {
			RenderSystem.setShader(GameRenderer::getPositionColorShader);
		} else if (format == DefaultVertexFormat.POSITION_COLOR_LIGHTMAP) {
			RenderSystem.setShader(GameRenderer::getPositionColorLightmapShader);
		} else if (format == DefaultVertexFormat.POSITION_COLOR_TEX) {
			RenderSystem.setShader(GameRenderer::getPositionColorTexShader);
		} else if (format == DefaultVertexFormat.POSITION_COLOR_TEX_LIGHTMAP) {
			RenderSystem.setShader(GameRenderer::getPositionColorTexLightmapShader);
		} else if (format == DefaultVertexFormat.BLOCK) {
			RenderSystem.setShader(GameRenderer::getRendertypeSolidShader);
		} else if (format == DefaultVertexFormat.NEW_ENTITY) {			
			RenderSystem.setShader(GameRenderer::getRendertypeEntitySolidShader);
		} else if (format == DefaultVertexFormat.POSITION_TEX) {
			RenderSystem.setShader(GameRenderer::getPositionTexShader);
		} else if (format == DefaultVertexFormat.POSITION_TEX_COLOR) {
			RenderSystem.setShader(GameRenderer::getPositionTexColorShader);
		} else if (format == DefaultVertexFormat.PARTICLE) {
			RenderSystem.setShader(GameRenderer::getParticleShader);			
		} else if (format == DefaultVertexFormat.POSITION_TEX_COLOR_NORMAL) {
			RenderSystem.setShader(GameRenderer::getRendertypeCloudsShader);
		} else {
			throw new IllegalArgumentException("Unsupported vertex format!");
		}
		
		builder.begin(drawMode, format);
		
		buildingDrawMode = drawMode;
		building = true;
	}

	@Override
	public GSBasicRenderer3D vert(float x, float y, float z) {
		builder.vertex(matrixStack.last().pose(), x, y, z);
		return this;
	}

	@Override
	public GSBasicRenderer3D color(float r, float g, float b, float a) {
		builder.color(r, g, b, a);
		return this;
	}

	@Override
	public GSBasicRenderer3D tex(float u, float v) {
		builder.uv(u, v);
		return this;
	}

	@Override
	public GSBasicRenderer3D next() {
		builder.endVertex();
		return this;
	}
	
	@Override
	public void finish() {
		if (!building)
			throw new IllegalStateException("Not building!");
		
		Tesselator.getInstance().end();
		building = false;
	}
}
