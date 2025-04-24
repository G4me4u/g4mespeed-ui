package com.g4mesoft.ui.renderer;

import com.mojang.blaze3d.pipeline.RenderPipeline;
import com.mojang.blaze3d.platform.DepthTestFunction;
import com.mojang.blaze3d.vertex.VertexFormat;

import net.minecraft.client.gl.RenderPipelines;
import net.minecraft.client.render.VertexFormats;

public final class GSRenderPipelines {
	
	public static final RenderPipeline POSITION_COLOR_QUADS = RenderPipeline.builder(RenderPipelines.POSITION_COLOR_SNIPPET)
			.withLocation("gs_ui/position_color_quads")
			.withVertexFormat(VertexFormats.POSITION_COLOR, VertexFormat.DrawMode.QUADS)
			.build();
	
	public static final RenderPipeline POSITION_COLOR_QUADS_NO_DEPTH = RenderPipeline.builder(RenderPipelines.POSITION_COLOR_SNIPPET)
			.withLocation("gs_ui/position_color_quads")
			.withDepthTestFunction(DepthTestFunction.NO_DEPTH_TEST)
			.withVertexFormat(VertexFormats.POSITION_COLOR, VertexFormat.DrawMode.QUADS)
			.build();

	public static final RenderPipeline POSITION_COLOR_LINES = RenderPipeline.builder(RenderPipelines.RENDERTYPE_LINES_SNIPPET)
			.withLocation("gs_ui/position_color_lines")
			.withVertexFormat(VertexFormats.POSITION_COLOR, VertexFormat.DrawMode.LINES)
			.withCull(false)
			.build();
	
	private GSRenderPipelines() {
	}
}
