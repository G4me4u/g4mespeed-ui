package com.g4mesoft.ui.renderer;

import com.mojang.blaze3d.vertex.VertexFormat;
import com.mojang.blaze3d.vertex.VertexFormat.Mode;

public abstract interface GSIRenderer {

	public static final Mode LINES          = Mode.LINES;
	public static final Mode LINE_STRIP     = Mode.LINE_STRIP;
	public static final Mode TRIANGLES      = Mode.TRIANGLES;
	public static final Mode TRIANGLE_STRIP = Mode.TRIANGLE_STRIP;
	public static final Mode QUADS          = Mode.QUADS;
	
	public void build(Mode drawMode, VertexFormat format);

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
	
}
