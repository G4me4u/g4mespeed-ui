package com.g4mesoft.ui.mixin.client;

import java.util.Properties;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import com.g4mesoft.ui.access.client.GSILanguageManagerAccess;

import net.minecraft.locale.LanguageManager;

@Mixin(LanguageManager.class)
public class GSLanguageManagerMixin implements GSILanguageManagerAccess {

	@Shadow private Properties translations;

	@Override
	public boolean gs_hasTranslation(String key) {
		return translations.containsKey(key);
	}
}
