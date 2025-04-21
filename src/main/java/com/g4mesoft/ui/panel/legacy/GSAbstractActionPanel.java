package com.g4mesoft.ui.panel.legacy;

import com.g4mesoft.ui.panel.GSIActionListener;
import com.g4mesoft.ui.panel.GSPanel;
import com.g4mesoft.ui.panel.GSPanelContext;
import com.g4mesoft.ui.panel.button.GSButton;
import com.g4mesoft.ui.panel.event.GSIMouseListener;
import com.g4mesoft.ui.panel.event.GSMouseEvent;

import net.minecraft.client.sound.instance.SimpleSoundInstance;

public abstract class GSAbstractActionPanel extends GSPanel implements GSIMouseListener {

	private final GSIActionListener listener;

	public GSAbstractActionPanel(GSIActionListener listener) {
		this.listener = listener;
	
		addMouseEventListener(this);
	}
	
	protected abstract void onClicked(int mouseX, int mouseY);

	protected void playClickSound() {
		GSPanelContext.playSound(SimpleSoundInstance.of(GSButton.UI_BUTTON_CLICK_SOUND, 1.0F));
	}
	
	@Override
	public void mousePressed(GSMouseEvent event) {
		if (event.getButton() == GSMouseEvent.BUTTON_LEFT) {
			onClicked(event.getX(), event.getY());
			event.consume();
		}
	}

	protected void dispatchActionPerformedEvent() {
		if (listener != null)
			listener.actionPerformed();
	}
}
