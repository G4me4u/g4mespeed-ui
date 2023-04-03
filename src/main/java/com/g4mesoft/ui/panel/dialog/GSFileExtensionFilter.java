package com.g4mesoft.ui.panel.dialog;

import java.nio.file.Path;

import com.g4mesoft.ui.util.GSPathUtil;

import net.minecraft.text.LiteralText;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;

public class GSFileExtensionFilter implements GSIFileNameFilter {

	private static final Text ALL_FILES_TEXT = new TranslatableText("panel.file.allFiles");
	private static final Text DESC_SEPARATOR = new LiteralText(" - ");
	
	private final String[] fileExts;
	private final Text[] options;

	public GSFileExtensionFilter() {
		this(new String[0]);
	}

	public GSFileExtensionFilter(String[] fileExts) {
		this(fileExts, null);
	}
	
	public GSFileExtensionFilter(String[] fileExts, Text[] descs) {
		// Note: intentional null-pointer exception
		int extCount = fileExts.length;
		// Append the All Files extension
		this.fileExts = new String[extCount + 1];
		this.fileExts[0] = null;
		for (int i = 0; i < extCount; i++) {
			if (fileExts[i] == null)
				throw new IllegalArgumentException("fileExts[" + i + "] is null!");
			this.fileExts[i + 1] = fileExts[i];
		}
		// Construct the file options
		options = new Text[extCount + 1];
		options[0] = ALL_FILES_TEXT;
		for (int i = 0; i < extCount; i++) {
			MutableText option = new LiteralText(fileExts[i]);
			if (descs != null && descs[i] != null)
				option = option.append(DESC_SEPARATOR).append(descs[i]);
			options[i + 1] = option;
		}
	}

	@Override
	public boolean filter(Path path, int option) {
		if (option < 0 || option >= options.length)
			throw new IndexOutOfBoundsException("Index out of bounds: " + option);
		if (GSPathUtil.isRegularFile(path)) {
			String ext = fileExts[option];
			if (ext == null) {
				// Represents the All Files option
				return true;
			}
			String fileExt = GSPathUtil.getFileExtension(path);
			return ext.equalsIgnoreCase(fileExt);
		}
		// Directories and other files are always included.
		return true;
	}

	@Override
	public Text[] getOptions() {
		return options;
	}
	
	@Override
	public int getDefaultOption() {
		return Math.min(1, options.length - 1);
	}
}
