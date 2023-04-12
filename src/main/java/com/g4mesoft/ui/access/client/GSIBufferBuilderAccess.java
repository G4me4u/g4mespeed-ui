package com.g4mesoft.ui.access.client;

import java.nio.ByteBuffer;

import com.g4mesoft.ui.renderer.GSClipRect;

import net.minecraft.client.render.VertexFormat;
import net.minecraft.client.render.VertexFormat.DrawMode;

public interface GSIBufferBuilderAccess {

	public void gs_pushClip(float x0, float y0, float x1, float y1);

	public void gs_pushClip(GSClipRect clip);

	public GSClipRect gs_popClip();

	public GSClipRect gs_getClip();

	public ByteBuffer gs_getByteBuffer();

	public DrawMode gs_getDrawMode();

	public VertexFormat gs_getVertexFormat();

	public int gs_getBatchOffset();

	public int gs_getVertexCount();

	public void gs_setVertexCount(int vertexCount);

	public void gs_setElementOffset(int elementOffset);
	
}
