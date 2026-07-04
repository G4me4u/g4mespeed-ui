package com.g4mesoft.ui.mixin.client;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.At.Shift;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.g4mesoft.ui.panel.GSPanelContext;

import net.minecraft.client.Minecraft;

@Mixin(value = Minecraft.class, priority = -1000)
public class GSMinecraftMixin {

	@Inject(
		method = "run",
		at = @At("HEAD")
	)
	private void onInit(CallbackInfo ci) {
		GSPanelContext.init((Minecraft)(Object)this);
	}
	
	@Inject(
		method = "exitWorldAndClose",
		at = @At(
			value = "CONSTANT",
			args = "stringValue=Stopping!",
			shift = Shift.AFTER
		)
	)
	private void onClientClose(CallbackInfo ci) {
		GSPanelContext.dispose();
	}
}
