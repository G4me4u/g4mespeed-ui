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
	@Unique
	private float gs_prevEventScrollX;

	@Inject(
		method="onMouseButton(JIII)V",
		at = @At("HEAD")
	)
	private void onMouseEvent(long windowHandle, int button, int action, int mods, CallbackInfo ci) {
		if (windowHandle == client.getWindow().getHandle())
			gs_prevEventModifiers = mods;
	}
	
	@Inject(
		method="onMouseScroll",
		at = @At("HEAD")
	)
	private void onOnMouseScroll(long windowHandle, double scrollX, double scrollY, CallbackInfo ci) {
		if (windowHandle == client.getWindow().getHandle()) {
			gs_prevEventScrollX = (float)(client.options.discreteMouseScroll ? Math.signum(scrollX) : scrollX);
			gs_prevEventScrollX *= client.options.mouseWheelSensitivity;
		}
	}
	
	@Override
	public int gs_getPreviousEventModifiers() {
		return gs_prevEventModifiers;
	}

	@Override
	public double gs_getPreviousEventScrollX() {
		return gs_prevEventScrollX;
	}
}
