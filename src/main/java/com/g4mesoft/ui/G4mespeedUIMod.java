package com.g4mesoft.ui;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.g4mesoft.ui.renderer.GSIRenderable3D;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.loader.api.FabricLoader;

public class G4mespeedUIMod implements ModInitializer {

	public static final Logger GSUI_LOGGER = LogManager.getLogger("G4mespeed UI");
	
	private static final List<GSIRenderable3D> renderables = new ArrayList<>();
	private static boolean sodiumLoaded;
	
	@Override
	public void onInitialize() {
		GSUI_LOGGER.info("G4mespeed UI initialized!");

		sodiumLoaded = FabricLoader.getInstance().isModLoaded("sodium");
		if (sodiumLoaded)
			GSUI_LOGGER.info("Sodium is detected");
	}

	/**
	 * Adds the given renderable to the list of renderables.
	 * 
	 * @param renderable - the renderable to add
	 * 
	 * @implNote Not thread safe! Must be invoked from the main client thread.
	 */
	public static void addRenderable(GSIRenderable3D renderable) {
		renderables.add(renderable);
	}

	/**
	 * Removes the given renderable from the list of renderables.
	 * 
	 * @param renderable - the renderable to remove
	 * 
	 * @implNote Not thread safe! Must be invoked from the main client thread.
	 */
	public static void removeRenderable(GSIRenderable3D renderable) {
		renderables.remove(renderable);
	}
	
	/**
	 * @return an immutable collection of all currently registered renderables.
	 * 
	 * @implNote Not thread safe! Must be invoked from the main client thread.
	 */
	public static Collection<GSIRenderable3D> getRenderables() {
		return Collections.unmodifiableCollection(renderables);
	}
	
	/**
	 * @return True, if sodium is present. False otherwise.
	 */
	public static boolean isSodiumLoaded() {
		return sodiumLoaded;
	}
}
