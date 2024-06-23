package com.g4mesoft.ui.renderer;

import org.joml.Quaternionf;

import com.mojang.blaze3d.systems.RenderSystem;

import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.BufferRenderer;
import net.minecraft.client.render.BuiltBuffer;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.VertexFormat;
import net.minecraft.client.render.VertexFormat.DrawMode;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.client.util.math.MatrixStack;

public class GSBasicRenderer3D implements GSIRenderer3D {

	private Tessellator tessellator;
	private MatrixStack matrixStack;
	
	private DrawMode buildingDrawMode;
	private BufferBuilder currBuilder;
	
	public void begin(Tessellator tessellator, MatrixStack matrixStack) {
		this.tessellator = tessellator;
		this.matrixStack = matrixStack;
	}
	
	public void end() {
		if (isBuilding())
			throw new IllegalStateException("Renderer is still building");
		
		matrixStack = null;
		tessellator = null;
	}
	
	@Override
	public void pushMatrix() {
		matrixStack.push();
	}

	@Override
	public void popMatrix() {
		matrixStack.pop();
	}

	@Override
	public void translate(float tx, float ty, float tz) {
		matrixStack.translate(tx, ty, tz);
	}

	@Override
	public void rotate(float rx, float ry, float rz) {
		matrixStack.multiply(new Quaternionf().rotateXYZ(rx, ry, rz));
	}

	@Override
	public void scale(float sx, float sy, float sz) {
		matrixStack.scale(sx, sy, sz);
	}
	
	@Override
	public void fillCuboid(float x0, float y0, float z0,
	                       float x1, float y1, float z1,
	                       float r, float g, float b, float a) {
		
		if (isBuilding() && buildingDrawMode != DrawMode.QUADS)
			throw new IllegalStateException("Building quads is required!");
		
		boolean wasBuilding = isBuilding();
		if (!wasBuilding)
			build(DrawMode.QUADS, VertexFormats.POSITION_COLOR);
		
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
		
		if (isBuilding() && buildingDrawMode != DrawMode.LINES)
			throw new IllegalStateException("Building lines is required!");
		
		boolean wasBuilding = isBuilding();
		if (!wasBuilding)
			build(DrawMode.LINES, VertexFormats.POSITION_COLOR);
		
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
	public void build(DrawMode drawMode, VertexFormat format) {
		if (isBuilding())
			throw new IllegalStateException("Already building!");
		
		if (format == VertexFormats.POSITION) {
			RenderSystem.setShader(GameRenderer::getPositionProgram);
		} else if (format == VertexFormats.POSITION_COLOR) {
			RenderSystem.setShader(GameRenderer::getPositionColorProgram);
		} else if (format == VertexFormats.POSITION_COLOR_LIGHT) {
			RenderSystem.setShader(GameRenderer::getPositionColorLightmapProgram);
		} else if (format == VertexFormats.POSITION_COLOR_TEXTURE_LIGHT) {
			RenderSystem.setShader(GameRenderer::getPositionColorTexLightmapProgram);
		} else if (format == VertexFormats.POSITION_COLOR_TEXTURE_LIGHT_NORMAL) {
			RenderSystem.setShader(GameRenderer::getRenderTypeSolidProgram);
		} else if (format == VertexFormats.POSITION_COLOR_TEXTURE_OVERLAY_LIGHT_NORMAL) {			
			RenderSystem.setShader(GameRenderer::getRenderTypeEntitySolidProgram);
		} else if (format == VertexFormats.POSITION_TEXTURE) {
			RenderSystem.setShader(GameRenderer::getPositionTexProgram);
		} else if (format == VertexFormats.POSITION_TEXTURE_COLOR) {
			RenderSystem.setShader(GameRenderer::getPositionTexColorProgram);
		} else if (format == VertexFormats.POSITION_TEXTURE_COLOR_LIGHT) {
			RenderSystem.setShader(GameRenderer::getParticleProgram);			
		} else if (format == VertexFormats.POSITION_TEXTURE_COLOR_NORMAL) {
			RenderSystem.setShader(GameRenderer::getRenderTypeCloudsProgram);
		} else {
			throw new IllegalArgumentException("Unsupported vertex format!");
		}
		
		currBuilder = tessellator.begin(drawMode, format);
		buildingDrawMode = drawMode;
	}

	@Override
	public GSBasicRenderer3D vert(float x, float y, float z) {
		currBuilder.vertex(matrixStack.peek().getPositionMatrix(), x, y, z);
		return this;
	}

	@Override
	public GSBasicRenderer3D color(float r, float g, float b, float a) {
		currBuilder.color(r, g, b, a);
		return this;
	}

	@Override
	public GSBasicRenderer3D tex(float u, float v) {
		currBuilder.texture(u, v);
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
		
		BuiltBuffer buffer = currBuilder.endNullable();
		if (buffer != null) {
			// Simply draw with the current program.
	        BufferRenderer.drawWithGlobalProgram(buffer);
		}
		currBuilder = null;
	}
	
	@Override
	public boolean isBuilding() {
		return currBuilder != null;
	}
}
