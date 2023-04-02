package com.g4mesoft.ui.panel;

import java.nio.file.Path;

import net.minecraft.text.Text;

public interface GSIFileNameFilter {

	public boolean filter(Path path, int option);
	
	public Text[] getOptions();
	
}
