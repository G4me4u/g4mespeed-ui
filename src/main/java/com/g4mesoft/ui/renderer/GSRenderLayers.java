package com.g4mesoft.ui.renderer;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.rendertype.LayeringTransform;
import net.minecraft.client.renderer.rendertype.OutputTarget;
import net.minecraft.client.renderer.rendertype.RenderSetup;
import net.minecraft.client.renderer.rendertype.RenderType;

public final class GSRenderLayers {

	public static final OutputTarget TRANSLUCENT_TARGET = new OutputTarget(
		"gs_ui_translucent_target",
		() -> Minecraft.getInstance().levelRenderer.translucentTarget()
	);
	
	public static final RenderType GUI = RenderType.create(
		"gs_gui",
		RenderSetup.builder(GSRenderPipelines.GUI)
			.createRenderSetup()
	);
	
	public static final RenderType POSITION_COLOR_QUADS = RenderType.create(
		"gs_ui_position_color_quads",
		RenderSetup.builder(GSRenderPipelines.POSITION_COLOR_QUADS)
			.setOutputTarget(TRANSLUCENT_TARGET)
			.createRenderSetup()
	);

	public static final RenderType POSITION_COLOR_QUADS_NO_DEPTH = RenderType.create(
		"gs_ui_position_color_quads",
		RenderSetup.builder(GSRenderPipelines.POSITION_COLOR_QUADS_NO_DEPTH)
			.setOutputTarget(TRANSLUCENT_TARGET)
			.createRenderSetup()
	);
	
	public static final RenderType POSITION_COLOR_NORMAL_LINE_WIDTH_LINES = RenderType.create(
		"gs_ui_position_color_lines",
		RenderSetup.builder(GSRenderPipelines.POSITION_COLOR_NORMAL_LINE_WIDTH_LINES)
			.setLayeringTransform(LayeringTransform.VIEW_OFFSET_Z_LAYERING)
			.setOutputTarget(TRANSLUCENT_TARGET)
			.createRenderSetup()
	);
	
	private GSRenderLayers() {
	}
}
