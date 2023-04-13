package com.g4mesoft.ui.mixin.client;

import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.At.Shift;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.g4mesoft.ui.panel.GSPanelContext;

import net.minecraft.client.MinecraftClient;

@Mixin(MinecraftClient.class)
public class GSMinecraftClientMixin {

	@Inject(
		method = "run",
		at = @At(
			value = "FIELD",
			shift = At.Shift.BEFORE,
			opcode = Opcodes.PUTFIELD,
			target = "Lnet/minecraft/client/MinecraftClient;thread:Ljava/lang/Thread;"
		)
	)
	private void onInit(CallbackInfo ci) {
		GSPanelContext.init((MinecraftClient)(Object)this);
	}
	
	@Inject(
		method = "stop",
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
