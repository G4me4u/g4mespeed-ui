package com.g4mesoft.ui.mixin.client;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import net.minecraft.locale.Language;

@Mixin(Language.class)
public interface GSILanguageAccessor {

	@Accessor("INSTANCE")
	public static Language gs_getInstance() {
		throw new AssertionError();
	}
}
