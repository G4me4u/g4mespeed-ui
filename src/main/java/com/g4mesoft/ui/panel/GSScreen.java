package com.g4mesoft.ui.panel;

import com.g4mesoft.ui.access.client.GSIKeyboardAccess;
import com.g4mesoft.ui.renderer.GSBasicRenderer2D;
import com.g4mesoft.ui.renderer.GSIRenderer2D;

import net.minecraft.client.GameNarrator;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.input.CharacterEvent;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.client.input.MouseButtonEvent;

final class GSScreen extends Screen {

	private final GSRootPanel rootPanel;

	private boolean visible;
	
	GSScreen() {
		super(GameNarrator.NO_TITLE);
	
		rootPanel = new GSRootPanel();
		
		visible = false;		
	}

	@Override
	protected void init() {
		super.init();
	
		rootPanel.setBounds(0, 0, width, height);
		
		setVisibleImpl(true);
	}
	
	@Override
	public void removed() {
		super.removed();

		setVisibleImpl(false);
	}
	
	private void setVisibleImpl(boolean visible) {
		if (visible != this.visible) {
			this.visible = visible;
			rootPanel.setVisible(visible);
		}
	}
	
	@Override
	public void renderBackground(GuiGraphics context, int mouseX, int mouseY, float deltaTicks) {
		// do nothing.
	}
	
	@Override
	public void render(GuiGraphics context, int mouseX, int mouseY, float partialTicks) {
		// Execute scheduled tasks (validate panels etc.)
		// before rendering.
		GSPanelContext.executeScheduledTasks();
		
		GSIRenderer2D renderer = GSPanelContext.getRenderer();
		
		((GSBasicRenderer2D)renderer).begin(context, mouseX, mouseY, width, height);
		
		rootPanel.preRender(renderer);
		rootPanel.render(renderer);
		rootPanel.postRender(renderer);
		
		((GSBasicRenderer2D)renderer).end();
	}

	@Override
	public void mouseMoved(double mouseX, double mouseY) {
		GSPanelContext.getEventDispatcher().mouseMoved((float)mouseX, (float)mouseY);
	}

	@Override
	public boolean mouseClicked(MouseButtonEvent click, boolean doubled) {
		GSPanelContext.getEventDispatcher().mousePressed(click.button(), (float)click.x(), (float)click.y(), click.modifiers());
		return true;
	}

	@Override
	public boolean mouseReleased(MouseButtonEvent click) {
		GSPanelContext.getEventDispatcher().mouseReleased(click.button(), (float)click.x(), (float)click.y(), click.modifiers());
		return true;
	}

	@Override
	public boolean mouseDragged(MouseButtonEvent click, double deltaX, double deltaY) {
		GSPanelContext.getEventDispatcher().mouseDragged(click.button(), (float)click.x(), (float)click.y(), (float)deltaX, (float)deltaY);
		return true;
	}

	@Override
	public boolean mouseScrolled(double mouseX, double mouseY, double scrollX, double scrollY) {
		GSPanelContext.getEventDispatcher().mouseScroll((float)mouseX, (float)mouseY, (float)scrollX, (float)scrollY);
		return true;
	}

	@Override
	public boolean keyPressed(KeyEvent input) {
		if (((GSIKeyboardAccess)minecraft.keyboardHandler).gs_isPreviousEventRepeating()) {
			GSPanelContext.getEventDispatcher().keyRepeated(input.key(), input.scancode(), input.modifiers());
		} else {
			GSPanelContext.getEventDispatcher().keyPressed(input.key(), input.scancode(), input.modifiers());
		}
		return true;
	}

	@Override
	public boolean keyReleased(KeyEvent input) {
		GSPanelContext.getEventDispatcher().keyReleased(input.key(), input.scancode(), input.modifiers());
		return true;
	}

	@Override
	public boolean charTyped(CharacterEvent input) {
		GSPanelContext.getEventDispatcher().keyTyped(input.codepoint());
		return true;
	}

	public GSRootPanel getRootPanel() {
		return rootPanel;
	}
}
