package com.g4mesoft.ui.panel;

import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;

import com.g4mesoft.ui.panel.event.GSEvent;
import com.g4mesoft.ui.panel.event.GSEventDispatcher;
import com.g4mesoft.ui.renderer.GSBasicRenderer2D;
import com.g4mesoft.ui.renderer.GSIRenderer2D;
import com.g4mesoft.ui.util.GSMathUtil;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.vertex.Tessellator;

import net.minecraft.client.gui.screen.Screen;

public final class GSScreen extends Screen {

	private final GSRootPanel rootPanel;
	
	private boolean visible;

	// Mouse (unscaled) event position.
	private int prevMouseDraggedEventX;
	private int prevMouseDraggedEventY;
	// Mouse (scaled) position.
	private int prevMouseX;
	private int prevMouseY;
	// Mouse active button used for dragging.
	private int currentActiveMouseButton;
	private int pressedMouseButtonCount;
	
	GSScreen() {
		rootPanel = new GSRootPanel();
		
		visible = false;
	}

	@Override
	public void init() {
		super.init();

		prevMouseDraggedEventX = prevMouseDraggedEventY = Integer.MIN_VALUE;
		prevMouseX = prevMouseY = Integer.MIN_VALUE;
		pressedMouseButtonCount = 0;
		currentActiveMouseButton = -1;
		
		Keyboard.enableRepeatEvents(true);
		rootPanel.setBounds(0, 0, width, height);
		
		setVisibleImpl(true);
	}
	
	@Override
	public void removed() {
		super.removed();

		Keyboard.enableRepeatEvents(false);
		
		setVisibleImpl(false);
	}
	
	private void setVisibleImpl(boolean visible) {
		if (visible != this.visible) {
			this.visible = visible;
			rootPanel.setVisible(visible);
		}
	}
	
	@Override
	public void render(int mouseX, int mouseY, float partialTicks) {
		// Execute scheduled tasks (validate panels etc.)
		// before rendering.
		GSPanelContext.executeScheduledTasks();
		
		GlStateManager.disableTexture();
		GlStateManager.disableAlphaTest();
		GlStateManager.shadeModel(GL11.GL_SMOOTH);
		GlStateManager.enableBlend();
		GlStateManager.blendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA,
				GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
		GlStateManager.color4f(1.0f, 1.0f, 1.0f, 1.0f);
		
		GSIRenderer2D renderer = GSPanelContext.getRenderer();
		
		((GSBasicRenderer2D)renderer).begin(Tessellator.getInstance().getBuilder(),
				mouseX, mouseY, width, height);
		
		rootPanel.preRender(renderer);
		rootPanel.render(renderer);
		rootPanel.postRender(renderer);
		
		((GSBasicRenderer2D)renderer).end();
		
		GlStateManager.disableBlend();
		GlStateManager.shadeModel(GL11.GL_FLAT);
		GlStateManager.enableAlphaTest();
		GlStateManager.enableTexture();
	}

	private int getModifiers() {
	    int modifiers = 0;
	    if (Keyboard.isKeyDown(Keyboard.KEY_LSHIFT) || Keyboard.isKeyDown(Keyboard.KEY_RSHIFT))
	        modifiers |= GSEvent.MODIFIER_SHIFT;
	    // Control
	    if (Keyboard.isKeyDown(Keyboard.KEY_LCONTROL) || Keyboard.isKeyDown(Keyboard.KEY_RCONTROL))
	        modifiers |= GSEvent.MODIFIER_CONTROL;
	    // Alt / Options
	    if (Keyboard.isKeyDown(Keyboard.KEY_LMENU) || Keyboard.isKeyDown(Keyboard.KEY_RMENU))
	        modifiers |= GSEvent.MODIFIER_ALT;
	    // Windows / Command
	    if (Keyboard.isKeyDown(Keyboard.KEY_LMETA) || Keyboard.isKeyDown(Keyboard.KEY_RMETA))
	        modifiers |= GSEvent.MODIFIER_SUPER;
	    // Caps Lock
	    if (Keyboard.isKeyDown(Keyboard.KEY_CAPITAL))
	        modifiers |= GSEvent.MODIFIER_CAPS_LOCK;
	    // Num Lock
	    if (Keyboard.isKeyDown(Keyboard.KEY_NUMLOCK))
	        modifiers |= GSEvent.MODIFIER_NUM_LOCK;
	    return modifiers;
	}
	
	@Override
	public void handleKeyboard() {
		GSEventDispatcher dispatcher = GSPanelContext.getEventDispatcher();
		int keyCode = Keyboard.getEventKey();
		if (keyCode != Keyboard.KEY_NONE) {
			int modifiers = getModifiers();
			if (Keyboard.getEventKeyState()) {
				// Press/Repeat.
				if (Keyboard.isRepeatEvent()) {
					dispatcher.keyRepeated(keyCode, keyCode, modifiers);
				} else {
					dispatcher.keyPressed(keyCode, keyCode, modifiers);
				}
			} else {
				// Release.
				dispatcher.keyReleased(keyCode, keyCode, modifiers);
			}
		}
		int codePoint = (int)Keyboard.getEventCharacter();
		if (codePoint != Keyboard.CHAR_NONE) {
			// Typed.
			dispatcher.keyTyped(codePoint);
		}
		// Note: handles full screen, etc.
		super.handleKeyboard();
	}

	@Override
	protected void keyPressed(char c, int keyCode) {
		// Do nothing.
	}

	@Override
	public void handleMouse() {
		GSEventDispatcher dispatcher = GSPanelContext.getEventDispatcher();
		// Translate eventX and eventY relative to top-left.
		int eventX = Mouse.getEventX();
		int eventY = minecraft.height - 1 - Mouse.getEventY();
		// Compute scaled mouseX and mouseY.
		int mouseX = (int)((double)eventX * width / minecraft.width);
		int mouseY = (int)((double)eventY * height / minecraft.height);
		int button = Mouse.getEventButton();
		if (button != -1) {
			// Press/Release.
			if (Mouse.getEventButtonState()) {
				pressedMouseButtonCount++;
				// Note: on touch screen we only allow one pressed button.
				if (!minecraft.options.touchscreen || pressedMouseButtonCount == 0) {
					dispatcher.mousePressed(button, mouseX, mouseY, getModifiers());
					currentActiveMouseButton = button;
				}
			} else {
				pressedMouseButtonCount = Math.max(pressedMouseButtonCount - 1, 0);
				// Note: on touch screen we only allow one pressed button.
				if (!minecraft.options.touchscreen || pressedMouseButtonCount == 0) {
					dispatcher.mouseReleased(button, mouseX, mouseY, getModifiers());
					currentActiveMouseButton = -1;
					prevMouseDraggedEventX = prevMouseDraggedEventY = Integer.MIN_VALUE;
				}
			}
		}
		// Scroll.
		int scrollY = Mouse.getEventDWheel();
		if (scrollY != 0) {
			// Scroll seems to be fixed to -120 to 120. Translate to -1, 1.
			scrollY = GSMathUtil.clamp(scrollY, -1, 1);
			// Note: horizontal scroll is not supported in LWJGL 2.
			dispatcher.mouseScroll(mouseX, mouseY, 0.0f, scrollY);
		}
		// Move.
		if (mouseX != prevMouseX || mouseY != prevMouseY) {
			dispatcher.mouseMoved(mouseX, mouseY);
			// Dragged.
			if (prevMouseDraggedEventX != Integer.MIN_VALUE && currentActiveMouseButton != -1) {
				int unscaledDeltaX = eventX - prevMouseDraggedEventX;
				int unscaledDeltaY = eventY - prevMouseDraggedEventY;
				float deltaX = (float)((double)unscaledDeltaX * width / minecraft.width);
				float deltaY = (float)((double)unscaledDeltaY * height / minecraft.height);
				dispatcher.mouseDragged(currentActiveMouseButton, mouseX, mouseY, deltaX, deltaY);
			}
			prevMouseDraggedEventX = eventX;
			prevMouseDraggedEventY = eventY;
		}
		prevMouseX = mouseX;
		prevMouseY = mouseY;
		// Does nothing, but might be injected by others.
		super.handleMouse();
	}

	@Override
	protected void mouseClicked(int mouseX, int mouseY, int mouseButton) {
		// Do nothing.
	}

	@Override
	protected void mouseReleased(int mouseX, int mouseY, int mouseButton) {
		// Do nothing.
	}

	public GSRootPanel getRootPanel() {
		return rootPanel;
	}
}
