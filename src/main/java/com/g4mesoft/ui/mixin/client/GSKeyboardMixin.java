package com.g4mesoft.ui.mixin.client;

import org.lwjgl.glfw.GLFW;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.g4mesoft.ui.access.client.GSIKeyboardAccess;

import net.minecraft.client.Keyboard;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.input.KeyInput;

@Mixin(Keyboard.class)
public class GSKeyboardMixin implements GSIKeyboardAccess {
	
	@Shadow @Final private MinecraftClient client;
	
	@Unique
	private boolean gs_prevEventRepeating;
	
	@Inject(
		method = "onKey(JILnet/minecraft/client/input/KeyInput;)V",
		at = @At("HEAD")
	)
	private void onOnKey(long windowHandle, int action, KeyInput input, CallbackInfo ci) {
		if (windowHandle == client.getWindow().getHandle())
			gs_prevEventRepeating = (action == GLFW.GLFW_REPEAT);
	}
	
	@Override
	public boolean gs_isPreviousEventRepeating() {
		return gs_prevEventRepeating;
	}
}
