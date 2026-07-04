package com.g4mesoft.ui.renderer;

import java.util.function.Consumer;

import com.mojang.blaze3d.PrimitiveTopology;
import com.mojang.blaze3d.vertex.VertexFormat;

import net.minecraft.client.renderer.rendertype.RenderType;

public interface GSIRenderCollector3D {

	public static final PrimitiveTopology LINES          = PrimitiveTopology.LINES;
	public static final PrimitiveTopology LINE_STRIP     = PrimitiveTopology.DEBUG_LINE_STRIP;
	public static final PrimitiveTopology TRIANGLES      = PrimitiveTopology.TRIANGLES;
	public static final PrimitiveTopology TRIANGLE_STRIP = PrimitiveTopology.TRIANGLE_STRIP;
	public static final PrimitiveTopology QUADS          = PrimitiveTopology.QUADS;

	public void submit(PrimitiveTopology drawMode, VertexFormat format, Consumer<GSIRenderer3D> builder);
	
	public void submit(RenderType renderType, Consumer<GSIRenderer3D> builder);

}
