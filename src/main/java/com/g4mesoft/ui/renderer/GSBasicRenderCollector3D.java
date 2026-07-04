package com.g4mesoft.ui.renderer;

import java.util.function.Consumer;

import com.mojang.blaze3d.PrimitiveTopology;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexFormat;

import net.minecraft.client.renderer.SubmitNodeStorage;
import net.minecraft.client.renderer.feature.CustomFeatureRenderer;
import net.minecraft.client.renderer.rendertype.RenderType;

public class GSBasicRenderCollector3D implements GSIRenderCollector3D {

	private final SubmitNodeStorage nodeStorage;
	private final PoseStack.Pose dummyPose;

	public GSBasicRenderCollector3D(SubmitNodeStorage nodeStorage) {
		if (nodeStorage == null)
			throw new NullPointerException("nodeCollector is null");

		this.nodeStorage = nodeStorage;
		this.dummyPose = new PoseStack.Pose();
	}

	@Override
	public void submit(PrimitiveTopology drawMode, VertexFormat format, Consumer<GSIRenderer3D> builder) {
		if (drawMode == QUADS && format == DefaultVertexFormat.POSITION_COLOR) {
			submit(GSRenderLayers.POSITION_COLOR_QUADS, builder);
		} else if (drawMode == LINES && format == DefaultVertexFormat.POSITION_COLOR_NORMAL_LINE_WIDTH) {
			submit(GSRenderLayers.POSITION_COLOR_NORMAL_LINE_WIDTH_LINES, builder);
		} else {
			throw new IllegalArgumentException("Unsupported draw mode and vertex format!");
		}
	}

	@Override
	public void submit(RenderType renderType, Consumer<GSIRenderer3D> builder) {
		nodeStorage.order(0).alwaysOnTop.submit(new CustomFeatureRenderer.Submit(dummyPose, renderType, (_, buffer) -> {
			builder.accept(new GSBasicRenderer3D(renderType, new PoseStack(), buffer));
		}));
	}
}
