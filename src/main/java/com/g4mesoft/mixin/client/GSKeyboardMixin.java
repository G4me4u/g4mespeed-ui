package com.g4mesoft.mixin.client;

import org.lwjgl.glfw.GLFW;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.g4mesoft.GSDebugTestingGUI;
import com.g4mesoft.access.client.GSIKeyboardAccess;
import com.g4mesoft.panel.GSPanelContext;

import net.minecraft.client.Keyboard;
import net.minecraft.client.MinecraftClient;

@Mixin(Keyboard.class)
public class GSKeyboardMixin implements GSIKeyboardAccess {
	
	@Shadow @Final private MinecraftClient client;
	
	@Unique
	private boolean gs_prevEventRepeating;
	
	@Inject(
		method = "onKey(JIIII)V",
		at = @At("HEAD")
	)
	private void onKeyEvent(long windowHandle, int key, int scancode, int action, int mods, CallbackInfo ci) {
		if (windowHandle == client.getWindow().getHandle()) {
			gs_prevEventRepeating = (action == GLFW.GLFW_REPEAT);

			if (key == GLFW.GLFW_KEY_O && (mods & GLFW.GLFW_MOD_CONTROL) != 0)
				GSPanelContext.openContent(new GSDebugTestingGUI());
		}
	}
	
	@Override
	public boolean gs_isPreviousEventRepeating() {
		return gs_prevEventRepeating;
	}
}
