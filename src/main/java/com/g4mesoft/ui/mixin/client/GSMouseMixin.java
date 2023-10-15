package com.g4mesoft.ui.mixin.client;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.g4mesoft.ui.access.client.GSIMouseAccess;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.Mouse;

@Mixin(Mouse.class)
public class GSMouseMixin implements GSIMouseAccess {

	@Shadow @Final private MinecraftClient client;

	@Unique
	private int gs_prevEventModifiers;

	@Inject(
		method="onMouseButton(JIII)V",
		at = @At("HEAD")
	)
	private void onMouseEvent(long windowHandle, int button, int action, int mods, CallbackInfo ci) {
		if (windowHandle == client.getWindow().getHandle())
			gs_prevEventModifiers = mods;
	}
	
	@Override
	public int gs_getPreviousEventModifiers() {
		return gs_prevEventModifiers;
	}
}
