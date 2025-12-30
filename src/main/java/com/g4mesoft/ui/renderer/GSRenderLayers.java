package com.g4mesoft.ui.renderer;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.LayeringTransform;
import net.minecraft.client.render.OutputTarget;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.RenderSetup;

public final class GSRenderLayers {

	public static final OutputTarget TRANSLUCENT_TARGET = new OutputTarget(
		"gs_ui_translucent_target",
		() -> MinecraftClient.getInstance().worldRenderer.getTranslucentFramebuffer()
	);
	
	public static final RenderLayer GUI = RenderLayer.of(
		"gs_gui",
		RenderSetup.builder(GSRenderPipelines.GUI)
			.expectedBufferSize(786432)
			.build()
	);
	
	public static final RenderLayer POSITION_COLOR_QUADS = RenderLayer.of(
		"gs_ui_position_color_quads",
		RenderSetup.builder(GSRenderPipelines.POSITION_COLOR_QUADS)
			.expectedBufferSize(1536)
			.outputTarget(TRANSLUCENT_TARGET)
			.build()
	);

	public static final RenderLayer POSITION_COLOR_QUADS_NO_DEPTH = RenderLayer.of(
		"gs_ui_position_color_quads",
		RenderSetup.builder(GSRenderPipelines.POSITION_COLOR_QUADS_NO_DEPTH)
			.expectedBufferSize(1536)
			.outputTarget(TRANSLUCENT_TARGET)
			.build()
	);
	
	public static final RenderLayer POSITION_COLOR_NORMAL_LINE_WIDTH_LINES = RenderLayer.of(
		"gs_ui_position_color_lines",
		RenderSetup.builder(GSRenderPipelines.POSITION_COLOR_NORMAL_LINE_WIDTH_LINES)
			.expectedBufferSize(1536)
			.layeringTransform(LayeringTransform.VIEW_OFFSET_Z_LAYERING)
			.outputTarget(TRANSLUCENT_TARGET)
			.build()
	);
	
	private GSRenderLayers() {
	}
}
