package com.g4mesoft.ui.panel.dialog;

import java.nio.file.Path;

import net.minecraft.network.chat.Component;

public interface GSIFileNameFilter {

	public boolean filter(Path path, int option);
	
	public Component[] getOptions();
	
	public int getDefaultOption();

	public Path resolve(Path path, int option);
	
}
