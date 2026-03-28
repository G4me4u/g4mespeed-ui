package com.g4mesoft.ui.renderer;

import java.util.Optional;

import com.mojang.blaze3d.pipeline.DepthStencilState;
import com.mojang.blaze3d.pipeline.RenderPipeline;
import com.mojang.blaze3d.platform.CompareOp;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.VertexFormat;

import net.minecraft.client.renderer.RenderPipelines;

public final class GSRenderPipelines {
	
	public static final RenderPipeline GUI = RenderPipelines.register(
		RenderPipeline.builder(RenderPipelines.GUI_SNIPPET)
			.withLocation("gs_ui/gui")
			.withDepthStencilState(Optional.empty())
			.build()
	);
	
	public static final RenderPipeline POSITION_COLOR_QUADS = RenderPipelines.register(
		RenderPipeline.builder(RenderPipelines.DEBUG_FILLED_SNIPPET)
			.withLocation("gs_ui/position_color_quads")
			.withVertexFormat(DefaultVertexFormat.POSITION_COLOR, VertexFormat.Mode.QUADS)
			.build()
	);
	
	public static final RenderPipeline POSITION_COLOR_QUADS_NO_DEPTH = RenderPipelines.register(
		RenderPipeline.builder(RenderPipelines.DEBUG_FILLED_SNIPPET)
			.withLocation("gs_ui/position_color_quads")
			.withDepthStencilState(Optional.empty())
			.withVertexFormat(DefaultVertexFormat.POSITION_COLOR, VertexFormat.Mode.QUADS)
			.build()
	);

	public static final RenderPipeline POSITION_COLOR_NORMAL_LINE_WIDTH_LINES = RenderPipelines.register(
		RenderPipeline.builder(RenderPipelines.LINES_SNIPPET)
			.withLocation("gs_ui/position_color_normal_line_width_lines")
			.withVertexFormat(DefaultVertexFormat.POSITION_COLOR_NORMAL_LINE_WIDTH, VertexFormat.Mode.LINES)
			.withCull(false)
			.withDepthStencilState(new DepthStencilState(CompareOp.LESS_THAN_OR_EQUAL, false))
			.build()
	);
	
	private GSRenderPipelines() {
	}
}
