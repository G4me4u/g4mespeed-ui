package com.g4mesoft.ui.panel;

public enum GSEFileDialogMode {

	OPEN(0, "open"),
	SAVE(1, "save");

	private final int index;
	private final String name;
	
	private GSEFileDialogMode(int index, String name) {
		this.index = index;
		this.name = name;
	}
	
	public int getIndex() {
		return index;
	}

	public String getName() {
		return name;
	}
}
