package com.g4mesoft.ui.renderer;

import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.RenderPhase;

public final class GSRenderLayers {

	private static final int BUFFER_SIZE = 1536;
	
	public static final RenderLayer POSITION_COLOR_QUADS = RenderLayer.of(
			"gs_ui_position_color_quads",
			BUFFER_SIZE,
			false,
			true,
			GSRenderPipelines.POSITION_COLOR_QUADS,
			RenderLayer.MultiPhaseParameters.builder()
					.target(RenderPhase.TRANSLUCENT_TARGET)
					.build(false)
	);

	public static final RenderLayer POSITION_COLOR_QUADS_NO_DEPTH = RenderLayer.of(
			"gs_ui_position_color_quads",
			BUFFER_SIZE,
			false,
			true,
			GSRenderPipelines.POSITION_COLOR_QUADS_NO_DEPTH,
			RenderLayer.MultiPhaseParameters.builder()
					.target(RenderPhase.TRANSLUCENT_TARGET)
					.build(false)
	);
	
	public static final RenderLayer POSITION_COLOR_LINES = RenderLayer.of(
			"gs_ui_position_color_lines",
			BUFFER_SIZE,
			false,
			true,
			GSRenderPipelines.POSITION_COLOR_LINES,
			RenderLayer.MultiPhaseParameters.builder()
					.target(RenderPhase.TRANSLUCENT_TARGET)
					.build(false)
	);
	
	private GSRenderLayers() {
	}	
}
