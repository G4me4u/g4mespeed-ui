package com.g4mesoft.ui.panel.dialog;

import java.nio.file.Path;

import net.minecraft.text.Text;

public interface GSIFileNameFilter {

	public boolean filter(Path path, int option);
	
	public Text[] getOptions();
	
	public int getDefaultOption();

	public Path resolve(Path path, int option);
	
}
