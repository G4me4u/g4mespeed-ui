package com.g4mesoft.ui.mixin.client;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.g4mesoft.ui.access.client.GSIMouseHandlerAccess;
import com.g4mesoft.ui.panel.GSScreen;

import net.minecraft.client.Minecraft;
import net.minecraft.client.MouseHandler;
import net.minecraft.client.gui.GuiEventListener;
import net.minecraft.client.gui.screen.Screen;

@Mixin(MouseHandler.class)
public class GSMouseHandlerMixin implements GSIMouseHandlerAccess {

	@Shadow @Final private Minecraft minecraft;

	@Unique
	private int gs_prevEventModifiers;
	@Unique
	private float gs_prevEventScrollX;
	
	@Unique
	private double gs_prevMouseX;
	@Unique
	private double gs_prevMouseY;

	@Inject(
		method="onPress(JIII)V",
		at = @At("HEAD")
	)
	private void onOnPress(long windowHandle, int button, int action, int mods, CallbackInfo ci) {
		if (windowHandle == minecraft.window.getWindow())
			gs_prevEventModifiers = mods;
	}
	
	@Inject(
		method="onScroll",
		at = @At("HEAD")
	)
	private void onOnScroll(long windowHandle, double scrollX, double scrollY, CallbackInfo ci) {
		if (windowHandle == minecraft.window.getWindow())
			gs_prevEventScrollX = (float)(scrollX * minecraft.options.discreteMouseScroll);
	}

	@Inject(
		method="onMove",
		at = @At("HEAD")
	)
	private void onOnMove(long windowHandle, double xpos, double ypos, CallbackInfo ci) {
		if (windowHandle == minecraft.window.getWindow()) {
			gs_prevMouseX = xpos * minecraft.window.getGuiScaledWidth() / minecraft.window.getScreenWidth();
			gs_prevMouseY = ypos * minecraft.window.getGuiScaledHeight() / minecraft.window.getScreenHeight();
			if (minecraft.screen instanceof GSScreen) {
				GuiEventListener guiEventListener = minecraft.screen;
				Screen.wrapScreenError(() -> ((GSScreen)minecraft.screen).mouseMoved(gs_prevMouseX, gs_prevMouseY),
						"mouseMoved event handler", guiEventListener.getClass().getCanonicalName());
			}
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

	@Override
	public double gs_getPreviousMouseX() {
		return gs_prevMouseX;
	}

	@Override
	public double gs_getPreviousMouseY() {
		return gs_prevMouseY;
	}
}
