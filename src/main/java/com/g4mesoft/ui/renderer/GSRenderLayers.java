package com.g4mesoft.ui.renderer;

import java.util.OptionalDouble;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gl.Framebuffer;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.RenderPhase;

public final class GSRenderLayers {

	public static final RenderPhase.Target TRANSLUCENT_TARGET = new RenderPhase.Target(
		"gs_ui_translucent_target",
		() -> {
			Framebuffer framebuffer = MinecraftClient.getInstance().worldRenderer.getTranslucentFramebuffer();
			return framebuffer != null ? framebuffer : MinecraftClient.getInstance().getFramebuffer();
		}
	);
	
	public static final RenderLayer.MultiPhase GUI = RenderLayer.of(
		"gs_gui",
		786432,
		GSRenderPipelines.GUI,
		RenderLayer.MultiPhaseParameters.builder()
			.build(false)
	);
	
	public static final RenderLayer POSITION_COLOR_QUADS = RenderLayer.of(
		"gs_ui_position_color_quads",
		1536,
		false,
		true,
		GSRenderPipelines.POSITION_COLOR_QUADS,
		RenderLayer.MultiPhaseParameters.builder()
			.target(TRANSLUCENT_TARGET)
			.build(false)
	);

	public static final RenderLayer POSITION_COLOR_QUADS_NO_DEPTH = RenderLayer.of(
		"gs_ui_position_color_quads",
		1536,
		false,
		true,
		GSRenderPipelines.POSITION_COLOR_QUADS_NO_DEPTH,
		RenderLayer.MultiPhaseParameters.builder()
			.target(TRANSLUCENT_TARGET)
			.build(false)
	);
	
	public static final RenderLayer POSITION_COLOR_LINES = RenderLayer.of(
		"gs_ui_position_color_lines",
		1536,
		false,
		true,
		GSRenderPipelines.POSITION_COLOR_LINES,
		RenderLayer.MultiPhaseParameters.builder()
			.lineWidth(new RenderPhase.LineWidth(OptionalDouble.empty()))
			.target(TRANSLUCENT_TARGET)
			.build(false)
	);
	
	private GSRenderLayers() {
	}
}
