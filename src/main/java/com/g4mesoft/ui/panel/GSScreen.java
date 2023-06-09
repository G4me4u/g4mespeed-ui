package com.g4mesoft.ui.panel;

import org.lwjgl.opengl.GL11;

import com.g4mesoft.ui.access.client.GSIKeyboardAccess;
import com.g4mesoft.ui.access.client.GSIMouseAccess;
import com.g4mesoft.ui.renderer.GSBasicRenderer2D;
import com.g4mesoft.ui.renderer.GSIRenderer2D;
import com.mojang.blaze3d.systems.RenderSystem;

import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.util.NarratorManager;
import net.minecraft.client.util.math.MatrixStack;

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
	
		client.keyboard.setRepeatEvents(true);
		rootPanel.setBounds(0, 0, width, height);
		
		setVisibleImpl(true);
	}
	
	@Override
	public void removed() {
		super.removed();

		client.keyboard.setRepeatEvents(false);

		setVisibleImpl(false);
	}
	
	private void setVisibleImpl(boolean visible) {
		if (visible != this.visible) {
			this.visible = visible;
			rootPanel.setVisible(visible);
		}
	}
	
	@Override
	public void render(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks) {
		// Execute scheduled tasks (validate panels etc.)
		// before rendering.
		GSPanelContext.executeScheduledTasks();
		
		RenderSystem.disableTexture();
		RenderSystem.disableAlphaTest();
		RenderSystem.shadeModel(GL11.GL_SMOOTH);
		RenderSystem.enableBlend();
		RenderSystem.defaultBlendFunc();
		RenderSystem.color4f(1.0f, 1.0f, 1.0f, 1.0f);
		
		GSIRenderer2D renderer = GSPanelContext.getRenderer();
		
		((GSBasicRenderer2D)renderer).begin(Tessellator.getInstance().getBuffer(),
				matrixStack, mouseX, mouseY, width, height);
		
		rootPanel.preRender(renderer);
		rootPanel.render(renderer);
		rootPanel.postRender(renderer);
		
		((GSBasicRenderer2D)renderer).end();
		
		RenderSystem.disableBlend();
		RenderSystem.shadeModel(GL11.GL_FLAT);
		RenderSystem.enableAlphaTest();
		RenderSystem.enableTexture();
	}

	@Override
	public void mouseMoved(double mouseX, double mouseY) {
		GSPanelContext.getEventDispatcher().mouseMoved((float)mouseX, (float)mouseY);
	}

	@Override
	public boolean mouseClicked(double mouseX, double mouseY, int button) {
		int modifiers = ((GSIMouseAccess)client.mouse).gs_getPreviousEventModifiers();
		GSPanelContext.getEventDispatcher().mousePressed(button, (float)mouseX, (float)mouseY, modifiers);
		return true;
	}

	@Override
	public boolean mouseReleased(double mouseX, double mouseY, int button) {
		int modifiers = ((GSIMouseAccess)client.mouse).gs_getPreviousEventModifiers();
		GSPanelContext.getEventDispatcher().mouseReleased(button, (float)mouseX, (float)mouseY, modifiers);
		return true;
	}

	@Override
	public boolean mouseDragged(double mouseX, double mouseY, int button, double deltaX, double deltaY) {
		GSPanelContext.getEventDispatcher().mouseDragged(button, (float)mouseX, (float)mouseY, (float)deltaX, (float)deltaY);
		return true;
	}

	@Override
	public boolean mouseScrolled(double mouseX, double mouseY, double scrollY) {
		float scrollX = (float)((GSIMouseAccess)client.mouse).gs_getPreviousEventScrollX();
		GSPanelContext.getEventDispatcher().mouseScroll((float)mouseX, (float)mouseY, scrollX, (float)scrollY);
		return true;
	}

	@Override
	public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
		if (((GSIKeyboardAccess)client.keyboard).gs_isPreviousEventRepeating()) {
			GSPanelContext.getEventDispatcher().keyRepeated(keyCode, scanCode, modifiers);
		} else {
			GSPanelContext.getEventDispatcher().keyPressed(keyCode, scanCode, modifiers);
		}
		return true;
	}

	@Override
	public boolean keyReleased(int keyCode, int scanCode, int modifiers) {
		GSPanelContext.getEventDispatcher().keyReleased(keyCode, scanCode, modifiers);
		return true;
	}

	@Override
	public boolean charTyped(char chr, int keyCode) {
		GSPanelContext.getEventDispatcher().keyTyped((int)chr);
		return true;
	}

	public GSRootPanel getRootPanel() {
		return rootPanel;
	}
}
