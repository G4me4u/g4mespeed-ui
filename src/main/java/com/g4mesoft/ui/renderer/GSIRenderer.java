package com.g4mesoft.ui.renderer;

import com.mojang.blaze3d.vertex.VertexFormat;
import com.mojang.blaze3d.vertex.VertexFormat.DrawMode;

import net.minecraft.client.render.RenderLayer;

public abstract interface GSIRenderer {

	public static final DrawMode LINES          = DrawMode.LINES;
	public static final DrawMode LINE_STRIP     = DrawMode.LINE_STRIP;
	public static final DrawMode TRIANGLES      = DrawMode.TRIANGLES;
	public static final DrawMode TRIANGLE_STRIP = DrawMode.TRIANGLE_STRIP;
	public static final DrawMode QUADS          = DrawMode.QUADS;
	
	public void build(DrawMode drawMode, VertexFormat format);
	
	public void build(RenderLayer layer);
	
	public GSIRenderer vert(float x, float y, float z);
	
	default public GSIRenderer color(int color) {
		float a = ((color >>> 24) & 0xFF) / 255.0f;
		float r = ((color >>> 16) & 0xFF) / 255.0f;
		float g = ((color >>>  8) & 0xFF) / 255.0f;
		float b = ((color       ) & 0xFF) / 255.0f;
		
		return color(r, g, b, a);
	}

	default public GSIRenderer color(float r, float g, float b) {
		return color(r, g, b, 1.0f);
	}

	public GSIRenderer color(float r, float g, float b, float a);

	public GSIRenderer tex(float u, float v);

	public GSIRenderer next();
	
	public void finish();
	
	public boolean isBuilding();
	
	public boolean isBuilding(DrawMode drawMode, VertexFormat format);
	
}
