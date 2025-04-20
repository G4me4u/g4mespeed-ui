package com.g4mesoft.ui.mixin.client;

import org.lwjgl.glfw.GLFW;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.g4mesoft.ui.access.client.GSIKeyboardHandlerAccess;

import net.minecraft.client.KeyboardHandler;
import net.minecraft.client.Minecraft;

@Mixin(KeyboardHandler.class)
public class GSKeyboardHandlerMixin implements GSIKeyboardHandlerAccess {
	
	@Shadow @Final private Minecraft minecraft;
	
	@Unique
	private boolean gs_prevEventRepeating;
	
	@Inject(
		method = "keyPress(JIIII)V",
		at = @At("HEAD")
	)
	private void onKeyPress(long windowHandle, int key, int scancode, int action, int mods, CallbackInfo ci) {
		if (windowHandle == minecraft.window.getWindow())
			gs_prevEventRepeating = (action == GLFW.GLFW_REPEAT);
	}
	
	@Override
	public boolean gs_isPreviousEventRepeating() {
		return gs_prevEventRepeating;
	}
}
