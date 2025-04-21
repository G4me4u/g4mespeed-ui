package com.g4mesoft.ui.panel.field;

import com.g4mesoft.ui.panel.GSDimension;
import com.g4mesoft.ui.panel.GSEIconAlignment;
import com.g4mesoft.ui.panel.GSETextAlignment;
import com.g4mesoft.ui.panel.GSIcon;
import com.g4mesoft.ui.panel.GSPanel;
import com.g4mesoft.ui.panel.GSPanelUtil;
import com.g4mesoft.ui.renderer.GSIRenderer2D;
import com.g4mesoft.ui.renderer.text.GSText;
import com.g4mesoft.ui.util.GSColorUtil;
import com.g4mesoft.ui.util.GSTextUtil;

public class GSTextLabel extends GSPanel {

	private static final int DEFAULT_BACKGROUND_COLOR = 0x00000000;
	private static final int DEFAULT_TEXT_COLOR = 0xFFCCCCCC;
	private static final int DEFAULT_ICON_SPACING = 2;
	
	private GSIcon icon;
	private GSText text;

	private GSEIconAlignment iconAlignment;
	private GSETextAlignment textAlignment;
	
	private int backgroundColor;
	private int textColor;
	private int iconSpacing;
	
	public GSTextLabel(String text) {
		this(null, text);
	}

	public GSTextLabel(GSIcon icon, String text) {
		this(icon, GSTextUtil.literal(text));
	}

	public GSTextLabel(GSText text) {
		this(null, text);
	}

	public GSTextLabel(GSIcon icon) {
		this(icon, (GSText)null);
	}
	
	public GSTextLabel(GSIcon icon, GSText text) {
		this.icon = icon;
		this.text = text;
		
		iconAlignment = GSEIconAlignment.LEFT;
		textAlignment = GSETextAlignment.CENTER;
		
		backgroundColor = DEFAULT_BACKGROUND_COLOR;
		textColor = DEFAULT_TEXT_COLOR;
		iconSpacing = DEFAULT_ICON_SPACING;
	}
	
	@Override
	public void render(GSIRenderer2D renderer) {
		super.render(renderer);

		if (GSColorUtil.unpackA(backgroundColor) != 0x00)
			renderer.fillRect(0, 0, width, height, backgroundColor);

		GSPanelUtil.drawLabel(renderer, icon, iconSpacing, text,
				textColor, true, iconAlignment, textAlignment, 0, 0, width, height);
	}
	
	@Override
	protected GSDimension calculatePreferredSize() {
		return GSPanelUtil.labelPreferredSize(icon, text, DEFAULT_ICON_SPACING);
	}

	public GSIcon getIcon() {
		return icon;
	}
	
	public void setIcon(GSIcon icon) {
		this.icon = icon;
	}
	
	public GSText getText() {
		return text;
	}

	public void setText(String text) {
		setText((text != null) ? GSTextUtil.literal(text) : null);
	}
	
	public void setText(GSText text) {
		this.text = text;
	}

	public GSEIconAlignment getIconAlignment() {
		return iconAlignment;
	}
	
	public void setIconAlignment(GSEIconAlignment iconAlignment) {
		if (iconAlignment == null)
			throw new IllegalArgumentException("iconAlignment is null");
		this.iconAlignment = iconAlignment;
	}

	public GSETextAlignment getTextAlignment() {
		return textAlignment;
	}
	
	public void setTextAlignment(GSETextAlignment textAlignment) {
		if (textAlignment == null)
			throw new IllegalArgumentException("textAlignment is null");
		this.textAlignment = textAlignment;
	}
	
	public int getBackgroundColor() {
		return backgroundColor;
	}
	
	public void setBackgroundColor(int backgroundColor) {
		this.backgroundColor = backgroundColor;
	}

	public int getTextColor() {
		return textColor;
	}
	
	public void setTextColor(int textColor) {
		this.textColor = textColor;
	}

	public int getIconSpacing() {
		return iconSpacing;
	}
	
	public void setIconSpacing(int iconSpacing) {
		if (iconSpacing < 0)
			throw new IllegalArgumentException("iconSpacing must be non-negative!");
		this.iconSpacing = iconSpacing;
	}
}
