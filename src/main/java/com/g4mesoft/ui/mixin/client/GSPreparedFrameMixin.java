package com.g4mesoft.ui.mixin.client;

import java.util.List;
import java.util.Map;
import java.util.Objects;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import com.g4mesoft.ui.access.client.GSIPreparedFrameAccess;
import com.g4mesoft.ui.access.client.GSISubmitNodeCollectionAccess;

import net.minecraft.client.renderer.SubmitNodeCollection;
import net.minecraft.client.renderer.SubmitNodeStorage;
import net.minecraft.client.renderer.feature.FeatureFrameContext;
import net.minecraft.client.renderer.feature.FeatureRenderDispatcher;
import net.minecraft.client.renderer.feature.phase.FeatureRenderPhase;

@Mixin(FeatureRenderDispatcher.PreparedFrame.class)
public abstract class GSPreparedFrameMixin implements GSIPreparedFrameAccess {

	@Shadow private FeatureFrameContext context;
	@Shadow private SubmitNodeStorage submitNodeStorage;
	@Shadow @Final private Map<FeatureRenderPhase<?>, List<?>> groupsByPhase;

	@Shadow
	protected abstract void executePhase(final FeatureRenderPhase<?> phase, final FeatureFrameContext context);

	@Override
	public boolean gs_hasTransparentLast() {
		SubmitNodeStorage storage = Objects.requireNonNull(this.submitNodeStorage);

		for (SubmitNodeCollection collection : storage.getSubmitsPerOrder().values()) {
			if (!this.groupsByPhase.getOrDefault(((GSISubmitNodeCollectionAccess)collection).gs_getTransparentLastPhase(), List.of()).isEmpty())
				return true;
		}
		return false;
	}

	@Override
	public void gs_executeTransparentLast() {
		FeatureFrameContext context = Objects.requireNonNull(this.context);
		SubmitNodeStorage storage = Objects.requireNonNull(this.submitNodeStorage);

		for (SubmitNodeCollection collection : storage.getSubmitsPerOrder().values()) {
			this.executePhase(((GSISubmitNodeCollectionAccess)collection).gs_getTransparentLastPhase(), context);
		}
	}
}
