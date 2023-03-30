package com.g4mesoft.ui.panel.dropdown;

import com.g4mesoft.ui.panel.GSDimension;
import com.g4mesoft.ui.panel.GSECursorType;
import com.g4mesoft.ui.panel.GSIActionListener;
import com.g4mesoft.ui.panel.GSIcon;
import com.g4mesoft.ui.panel.GSPanel;
import com.g4mesoft.ui.panel.GSPanelContext;
import com.g4mesoft.ui.panel.GSRectangle;
import com.g4mesoft.ui.panel.event.GSIMouseListener;
import com.g4mesoft.ui.panel.event.GSMouseEvent;
import com.g4mesoft.ui.renderer.GSIRenderer2D;

import net.minecraft.text.Text;

public class GSDropdownAction extends GSDropdownItem implements GSIMouseListener {

	private final GSIcon icon;
	private final Text title;
	private final GSIActionListener listener;
	
	public GSDropdownAction(Text text, GSIActionListener listener) {
		this(null, text, listener);
	}
	
	public GSDropdownAction(GSIcon icon, Text title, GSIActionListener listener) {
		this.icon = icon;
		this.title = title;
		this.listener = listener;
		
		addMouseEventListener(this);
	}

	@Override
	public void render(GSIRenderer2D renderer) {
		super.render(renderer);

		// Layout of an action is:
		// -------------------------
		// | I | TEXT          |   |
		// -------------------------
		
		boolean hovered = renderer.isMouseInside(0, 0, width, height);
		
		if (isEnabled() && hovered)
			renderer.fillRect(0, 0, width, height, HOVERED_BACKGROUND_COLOR);

		if (icon != null) {
			// Force allowed icon size to that predefined in GSDropdownItem
			int iy = Math.max((height - ICON_SIZE) / 2, 0);
			renderIcon(renderer, PADDING, iy, ICON_SIZE, ICON_SIZE);
		}
	
		// Center drawn text on y-axis
		int ty = Math.max((height - renderer.getTextHeight() + 1) / 2, 0);
		int textColor = isEnabled() ? (hovered ? HOVERED_TEXT_COLOR : TEXT_COLOR) : DISABLED_TEXT_COLOR;
		renderTitle(renderer, PADDING + ICON_SIZE + ICON_MARGIN, ty, textColor);
	}
	
	protected void renderIcon(GSIRenderer2D renderer, int x, int y, int w, int h) {
		icon.render(renderer, new GSRectangle(x, y, w, h));
	}
	
	protected void renderTitle(GSIRenderer2D renderer, int tx, int ty, int textColor) {
		renderer.drawText(title, tx, ty, textColor, false);
	}

	@Override
	protected GSDimension calculatePreferredSize() {
		GSIRenderer2D renderer = GSPanelContext.getRenderer();

		int w = 0;
		// Icon size and margin (leftmost element)
		w += ICON_SIZE + ICON_MARGIN;
		// Add estimated title width
		w += Math.round(renderer.getTextWidth(title));
		// Add equal amount of padding to the right for balance
		w += ICON_MARGIN + ICON_SIZE;
		
		int h = Math.max(ICON_SIZE, renderer.getTextHeight());
		
		return new GSDimension(w + 2 * PADDING, h + 2 * PADDING);
	}
	
	@Override
	public void mouseReleased(GSMouseEvent event) {
		if (event.getButton() == GSMouseEvent.BUTTON_LEFT) {
			int mx = event.getX();
			int my = event.getY();
			// Ensure mouse position is still inside the panel
			// to allow the user to "cancel" actions.
			if (mx >= 0 && my >= 0 && mx < width && my < height) {
				performAction();
				event.consume();
			}
		}
	}
	
	private void performAction() {
		dispatchActionPerformedEvent();

		GSPanel parent = getParent();
		if (parent instanceof GSDropdown)
			((GSDropdown)parent).onActionPerformed();
	}
	
	protected void dispatchActionPerformedEvent() {
		if (listener != null)
			listener.actionPerformed();
	}
	
	public Text getText() {
		return title;
	}
	
	@Override
	public GSECursorType getCursor() {
		return isEnabled() ? GSECursorType.HAND : super.getCursor();
	}
}
