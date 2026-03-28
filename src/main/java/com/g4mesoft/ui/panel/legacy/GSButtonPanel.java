package com.g4mesoft.ui.panel.legacy;

import com.g4mesoft.ui.panel.GSIActionListener;
import com.g4mesoft.ui.panel.event.GSIKeyListener;
import com.g4mesoft.ui.panel.event.GSKeyEvent;
import com.g4mesoft.ui.renderer.GSBasicRenderer2D;
import com.g4mesoft.ui.renderer.GSIRenderer2D;
import com.g4mesoft.ui.util.GSTextUtil;

import net.minecraft.client.gui.components.WidgetSprites;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

public class GSButtonPanel extends GSAbstractTextActionPanel implements GSIKeyListener {

    private static final WidgetSprites TEXTURES = new WidgetSprites(
		new ResourceLocation("widget/button"),
		new ResourceLocation("widget/button_disabled"),
		new ResourceLocation("widget/button_highlighted")
    );
	
	public static final int BUTTON_HEIGHT = 20;

	public GSButtonPanel(String text, GSIActionListener listener) {
		this(GSTextUtil.literal(text), listener);
	}
	
	public GSButtonPanel(Component text, GSIActionListener listener) {
		super(text, listener);
	
		addKeyEventListener(this);
	}
	
	public void setPreferredBounds(int x, int y, int width) {
		setBounds(x, y, width, BUTTON_HEIGHT);
	}

	@Override
	protected void renderBackground(GSIRenderer2D renderer, boolean hovered) {
		if (!(renderer instanceof GSBasicRenderer2D))
			throw new IllegalStateException("Only GSBasicRenderer2D is supported.");
		
		GSBasicRenderer2D br = (GSBasicRenderer2D)renderer;
		
        br.legacyDrawGuiTexture(TEXTURES.get(isEnabled(), hovered), 0, 0, width, height);
	}

	@Override
	protected void onClicked(int mouseX, int mouseY) {
		dispatchActionPerformedEvent();
		playClickSound();
	}
	
	@Override
	public void keyPressed(GSKeyEvent event) {
		if (isEnabled() && !event.isRepeating()) {
			switch (event.getKeyCode()) {
			case GSKeyEvent.KEY_ENTER:
			case GSKeyEvent.KEY_KP_ENTER:
			case GSKeyEvent.KEY_SPACE:
				dispatchActionPerformedEvent();
				playClickSound();
				event.consume();
				break;
			}
		}
	}
}
