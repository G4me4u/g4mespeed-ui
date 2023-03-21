package com.g4mesoft;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import net.fabricmc.api.ModInitializer;

public class G4mespeedUIMod implements ModInitializer {

	public static final Logger GSUI_LOGGER = LogManager.getLogger("G4mespeed UI");
	
	@Override
	public void onInitialize() {
		GSUI_LOGGER.info("G4mespeed UI ${version} initialized!");
	}
}
