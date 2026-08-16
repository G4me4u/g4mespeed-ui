package com.g4mesoft.ui.mixin.client;

import java.util.ArrayList;
import java.util.List;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;

import com.g4mesoft.ui.access.client.GSISubmitNodeCollectionAccess;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;

import net.minecraft.client.renderer.SubmitNodeCollection;
import net.minecraft.client.renderer.feature.phase.FeatureRenderPhase;
import net.minecraft.client.renderer.feature.phase.SimpleFeatureRenderPhase;

@Mixin(SubmitNodeCollection.class)
public class GSSubmitNodeCollectionMixin implements GSISubmitNodeCollectionAccess {

	@Unique
	public final SimpleFeatureRenderPhase gs_transparentLast = new SimpleFeatureRenderPhase();

	@ModifyExpressionValue(
		method = "<init>",
		at = @At(
			value = "INVOKE",
			target =
				"Ljava/util/List;of(" +
					"[Ljava/lang/Object;" +
				")Ljava/util/List;"
		)
	)
	public List<FeatureRenderPhase<?>> onInitModifyListOf(List<FeatureRenderPhase<?>> originalPhases) {
		List<FeatureRenderPhase<?>> phases = new ArrayList<>(originalPhases.size() + 1);
		phases.addAll(originalPhases);
		phases.add(gs_transparentLast);
		return phases;
	}
	
	@Override
	public SimpleFeatureRenderPhase gs_getTransparentLastPhase() {
		return gs_transparentLast;
	}
}
