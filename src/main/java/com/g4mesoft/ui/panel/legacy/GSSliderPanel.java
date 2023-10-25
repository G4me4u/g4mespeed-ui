package com.g4mesoft.ui.panel.legacy;

import com.g4mesoft.ui.panel.GSIActionListener;
import com.g4mesoft.ui.panel.event.GSMouseEvent;
import com.g4mesoft.ui.renderer.GSBasicRenderer2D;
import com.g4mesoft.ui.renderer.GSIRenderer2D;
import com.g4mesoft.ui.util.GSMathUtil;
import com.g4mesoft.ui.util.GSTextUtil;

import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

public class GSSliderPanel extends GSAbstractTextActionPanel {

    private static final Identifier TEXTURE = new Identifier("widget/slider");
    private static final Identifier HANDLE_TEXTURE = new Identifier("widget/slider_handle");
    private static final Identifier HANDLE_HIGHLIGHTED_TEXTURE = new Identifier("widget/slider_handle_highlighted");
	
	public static final int SLIDER_HEIGHT = 20;
	public static final int MAX_WIDTH = 200;
	
	private float value;
	
	public GSSliderPanel(String text, GSIActionListener listener) {
		this(GSTextUtil.literal(text), listener);
	}

	public GSSliderPanel(Text text, GSIActionListener listener) {
		super(text, listener);
		
		this.value = 0.0f;
	}

	@Override
	protected void renderBackground(GSIRenderer2D renderer, boolean hovered) {
		if (!(renderer instanceof GSBasicRenderer2D))
			throw new IllegalStateException("Only GSBasicRenderer2D is supported.");
		
		GSBasicRenderer2D br = (GSBasicRenderer2D)renderer;
		
		int vx = Math.round(value * (width - 8));
		Identifier handleTex = (isEnabled() && renderer.isMouseInside(0, 0, width, height)) ?
				HANDLE_HIGHLIGHTED_TEXTURE : HANDLE_TEXTURE;

		br.legacyDrawGuiTexture(TEXTURE, 0, 0, width, height);
		br.legacyDrawGuiTexture(handleTex, vx, 0, 8, height);
	}

	@Override
	protected void onClicked(int mouseX, int mouseY) {
		setValue((float)(mouseX - 4) / (width - 8));
		dispatchActionPerformedEvent();
	}
	
	@Override
	public void mouseDragged(GSMouseEvent event) {
		super.mouseDragged(event);
		
		if (isEnabled() && !event.isConsumed() && event.getButton() == GSMouseEvent.BUTTON_LEFT) {
			onClicked(event.getX(), event.getY());
			event.consume();
		}
	}
	
	@Override
	public void mouseReleased(GSMouseEvent event) {
		super.mouseReleased(event);

		if (isEnabled() && !event.isConsumed()) {
			playClickSound();
			event.consume();
		}
	}
	
	public float getValue() {
		return value;
	}
	
	public void setValue(float value) {
		this.value = GSMathUtil.clamp(value, 0.0f, 1.0f);
	}
}
