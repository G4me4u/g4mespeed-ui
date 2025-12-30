package com.g4mesoft.ui.renderer;

import com.mojang.blaze3d.pipeline.RenderPipeline;
import com.mojang.blaze3d.platform.DepthTestFunction;
import com.mojang.blaze3d.vertex.VertexFormat;

import net.minecraft.client.gl.RenderPipelines;
import net.minecraft.client.render.VertexFormats;

public final class GSRenderPipelines {
	
	public static final RenderPipeline GUI = RenderPipelines.register(
		RenderPipeline.builder(RenderPipelines.GUI_SNIPPET)
			.withLocation("gs_ui/gui")
			.withDepthTestFunction(DepthTestFunction.NO_DEPTH_TEST)
			.build()
	);
	
	public static final RenderPipeline POSITION_COLOR_QUADS = RenderPipelines.register(
		RenderPipeline.builder(RenderPipelines.POSITION_COLOR_SNIPPET)
			.withLocation("gs_ui/position_color_quads")
			.withVertexFormat(VertexFormats.POSITION_COLOR, VertexFormat.DrawMode.QUADS)
			.build()
	);
	
	public static final RenderPipeline POSITION_COLOR_QUADS_NO_DEPTH = RenderPipelines.register(
		RenderPipeline.builder(RenderPipelines.POSITION_COLOR_SNIPPET)
			.withLocation("gs_ui/position_color_quads")
			.withDepthTestFunction(DepthTestFunction.NO_DEPTH_TEST)
			.withVertexFormat(VertexFormats.POSITION_COLOR, VertexFormat.DrawMode.QUADS)
			.build()
	);

	public static final RenderPipeline POSITION_COLOR_NORMAL_LINE_WIDTH_LINES = RenderPipelines.register(
		RenderPipeline.builder(RenderPipelines.RENDERTYPE_LINES_SNIPPET)
			.withLocation("gs_ui/position_color_normal_line_width_lines")
			.withVertexFormat(VertexFormats.POSITION_COLOR_NORMAL_LINE_WIDTH, VertexFormat.DrawMode.LINES)
			.withCull(false)
			.withDepthWrite(false)
			.build()
	);
	
	private GSRenderPipelines() {
	}
}
