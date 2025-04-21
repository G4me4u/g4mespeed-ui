package com.g4mesoft.ui.panel.dialog;

import java.nio.file.Path;

import com.g4mesoft.ui.renderer.text.GSText;

public interface GSIFileNameFilter {

	public boolean filter(Path path, int option);
	
	public GSText[] getOptions();
	
	public int getDefaultOption();

	public Path resolve(Path path, int option);
	
}
