package com.g4mesoft.ui.mixin.client;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.g4mesoft.ui.access.client.GSIBufferBuilderAccess;
import com.g4mesoft.ui.renderer.GSClipAdjuster;
import com.g4mesoft.ui.renderer.GSClipRect;
import com.mojang.blaze3d.vertex.BufferBuilder;

@Mixin(BufferBuilder.class)
public class GSBufferBuilderMixin implements GSIBufferBuilderAccess {

	@Shadow private boolean building;
	
	@Shadow private int vertexCount;
	
	@Shadow private double offsetX;
	@Shadow private double offsetY;
	@Shadow private double offsetZ;
	
	@Unique
	private final GSClipAdjuster gs_adjuster = new GSClipAdjuster();

	@Inject(
		method = "nextVertex", 
		at = @At("RETURN")
	)
	public void onNextReturn(CallbackInfo ci) {
		if (building && (vertexCount & 0x3 /* % 4 */) == 0)
			gs_adjuster.clipPreviousShape((BufferBuilder)(Object)this);
	}
	
	@Override
	public void gs_pushClip(float x0, float y0, float x1, float y1) {
		gs_pushClip(new GSClipRect(x0, y0, x1, y1));
	}

	@Override
	public void gs_pushClip(GSClipRect clip) {
		if (building)
			throw new IllegalStateException("Buffer Builder is building.");

		gs_adjuster.pushClip(clip);
	}
	
	@Override
	public GSClipRect gs_popClip() {
		return gs_adjuster.popClip();
	}

	@Override
	public GSClipRect gs_getClip() {
		return gs_adjuster.getClip();
	}

	@Override
	public int gs_getVertexCount() {
		return vertexCount;
	}

	@Override
	public void gs_setVertexCount(int vertexCount) {
		this.vertexCount = vertexCount;
	}

	@Override
	public void gs_clipPreviousShape() {
		if (building && vertexCount >= 4)
			gs_adjuster.clipPreviousShape((BufferBuilder)(Object)this);
	}
}
