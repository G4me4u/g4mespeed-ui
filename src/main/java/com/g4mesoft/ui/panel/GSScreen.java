package com.g4mesoft.ui.panel;

import com.g4mesoft.ui.access.client.GSIKeyboardAccess;
import com.g4mesoft.ui.renderer.GSBasicRenderer2D;
import com.g4mesoft.ui.renderer.GSIRenderer2D;

import net.minecraft.client.gui.Click;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.input.CharInput;
import net.minecraft.client.input.KeyInput;
import net.minecraft.client.util.NarratorManager;

final class GSScreen extends Screen {

	private final GSRootPanel rootPanel;

	private boolean visible;
	
	GSScreen() {
		super(NarratorManager.EMPTY);
	
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
	public void renderBackground(DrawContext context, int mouseX, int mouseY, float deltaTicks) {
		// do nothing.
	}
	
	@Override
	public void render(DrawContext context, int mouseX, int mouseY, float partialTicks) {
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
	public boolean mouseClicked(Click click, boolean doubled) {
		GSPanelContext.getEventDispatcher().mousePressed(click.button(), (float)click.x(), (float)click.y(), click.modifiers());
		return true;
	}

	@Override
	public boolean mouseReleased(Click click) {
		GSPanelContext.getEventDispatcher().mouseReleased(click.button(), (float)click.x(), (float)click.y(), click.modifiers());
		return true;
	}

	@Override
	public boolean mouseDragged(Click click, double deltaX, double deltaY) {
		GSPanelContext.getEventDispatcher().mouseDragged(click.button(), (float)click.x(), (float)click.y(), (float)deltaX, (float)deltaY);
		return true;
	}

	@Override
	public boolean mouseScrolled(double mouseX, double mouseY, double scrollX, double scrollY) {
		GSPanelContext.getEventDispatcher().mouseScroll((float)mouseX, (float)mouseY, (float)scrollX, (float)scrollY);
		return true;
	}

	@Override
	public boolean keyPressed(KeyInput input) {
		if (((GSIKeyboardAccess)client.keyboard).gs_isPreviousEventRepeating()) {
			GSPanelContext.getEventDispatcher().keyRepeated(input.key(), input.scancode(), input.modifiers());
		} else {
			GSPanelContext.getEventDispatcher().keyPressed(input.key(), input.scancode(), input.modifiers());
		}
		return true;
	}

	@Override
	public boolean keyReleased(KeyInput input) {
		GSPanelContext.getEventDispatcher().keyReleased(input.key(), input.scancode(), input.modifiers());
		return true;
	}

	@Override
	public boolean charTyped(CharInput input) {
		GSPanelContext.getEventDispatcher().keyTyped(input.codepoint());
		return true;
	}

	public GSRootPanel getRootPanel() {
		return rootPanel;
	}
}
