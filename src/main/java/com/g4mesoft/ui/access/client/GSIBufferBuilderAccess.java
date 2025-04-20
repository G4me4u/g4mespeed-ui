package com.g4mesoft.ui.access.client;

import com.g4mesoft.ui.renderer.GSClipRect;

public interface GSIBufferBuilderAccess {

	public void gs_pushClip(float x0, float y0, float x1, float y1);

	public void gs_pushClip(GSClipRect clip);

	public GSClipRect gs_popClip();

	public GSClipRect gs_getClip();

	public int gs_getVertexCount();

	public void gs_setVertexCount(int vertexCount);

	public void gs_clipPreviousShape();
	
}
