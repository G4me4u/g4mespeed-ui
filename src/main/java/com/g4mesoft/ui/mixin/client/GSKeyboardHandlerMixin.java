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

import net.minecraft.client.KeyboardHandler;
import net.minecraft.client.Minecraft;
import net.minecraft.client.input.KeyEvent;

@Mixin(KeyboardHandler.class)
public class GSKeyboardHandlerMixin implements GSIKeyboardAccess {
	
	@Shadow @Final private Minecraft minecraft;
	
	@Unique
	private boolean gs_prevEventRepeating;
	
	@Inject(
		method = "keyPress(JILnet/minecraft/client/input/KeyEvent;)V",
		at = @At("HEAD")
	)
	private void onOnKey(long windowHandle, int action, KeyEvent input, CallbackInfo ci) {
		if (windowHandle == minecraft.getWindow().handle())
			gs_prevEventRepeating = (action == GLFW.GLFW_REPEAT);
	}
	
	@Override
	public boolean gs_isPreviousEventRepeating() {
		return gs_prevEventRepeating;
	}
}
