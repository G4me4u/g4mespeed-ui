package com.g4mesoft.ui.panel;

import com.g4mesoft.ui.panel.event.GSILayoutEventListener;
import com.g4mesoft.ui.panel.event.GSLayoutEvent;
import com.g4mesoft.ui.renderer.GSIRenderer2D;
import com.g4mesoft.ui.util.GSColorUtil;

import net.minecraft.client.render.VertexFormats;

public class GSPopup extends GSParentPanel {

	private static final int SHADOW_WIDTH    = 5;
	private static final int SHADOW_OFFSET_X = 1;
	private static final int SHADOW_OFFSET_Y = 2;
	private static final int SHADOW_COLOR = 0x80000000;
	
	private static final int DEFAULT_BACKGROUND_COLOR = 0xFF252526;
	
	protected final GSPanel content;
	protected final boolean stealingFocus;
	
	private boolean hiddenOnFocusLost;
	
	protected GSPanel source;
	private GSEPopupPlacement placement;
	private int relX;
	private int relY;
	private boolean sourceFocusedOnHide;
	
	private int backgroundColor;
	
	private GSILayoutEventListener sourceLayoutListener;

	public GSPopup(GSPanel content) {
		this(content, true);
	}
	
	public GSPopup(GSPanel content, boolean stealingFocus) {
		if (content == null)
			throw new IllegalArgumentException("content is null");
		this.content = content;
		this.stealingFocus =  stealingFocus;
		
		hiddenOnFocusLost = false;

		source = null;
		placement = GSEPopupPlacement.ABSOLUTE;
		relX = relY = 0;
		sourceFocusedOnHide = true;
		
		backgroundColor = DEFAULT_BACKGROUND_COLOR;
		
		sourceLayoutListener = null;
		
		content.addLayoutEventListener(new GSILayoutEventListener() {
			@Override
			public void panelInvalidated(GSLayoutEvent event) {
				GSPopup.this.invalidate();
			}
		});
	}
	
	@Override
	protected void onShown() {
		super.onShown();
		
		if (placement != GSEPopupPlacement.ABSOLUTE && source != null) {
			sourceLayoutListener = new GSSourceLayoutListener();
			source.addLayoutEventListener(sourceLayoutListener);
		}

	}

	@Override
	protected void onHidden() {
		super.onHidden();
		
		if (placement != GSEPopupPlacement.ABSOLUTE && source != null) {
			source.removeLayoutEventListener(sourceLayoutListener);
			sourceLayoutListener = null;
		}
	}
	
	@Override
	public void add(GSPanel panel) {
		throw new UnsupportedOperationException("Popups can only have one child");
	}

	@Override
	public void remove(GSPanel panel) {
		throw new UnsupportedOperationException();
	}
	
	@Override
	protected void layout() {
		updateBounds();
		
		GSMargin margin = content.getProperty(GSLayoutProperties.MARGIN);
		int w = Math.max(0, width - margin.getHorizMargin());
		int h = Math.max(0, height - margin.getVertMargin());
		content.setBounds(margin.left, margin.right, w, h);
	}
	
	@Override
	public GSDimension calculateMinimumSize() {
		return calculateSize(MINIMUM_SIZE);
	}

	@Override
	protected GSDimension calculatePreferredSize() {
		return calculateSize(PREFERRED_SIZE);
	}
	
	private GSDimension calculateSize(GSILayoutProperty<GSDimension> sizeProperty) {
		GSDimension size = content.getProperty(sizeProperty);
		GSMargin margin = content.getProperty(GSLayoutProperties.MARGIN);
		// Limit the popup size to the rootPanel size.
		GSRootPanel rootPanel = GSPanelContext.getRootPanel();
		int w = (int)Math.min(rootPanel.getWidth(), (long)size.getWidth() + margin.getHorizMargin());
		int h = (int)Math.min(rootPanel.getHeight(), (long)size.getHeight() + margin.getVertMargin());
		return new GSDimension(w, h);
	}

	public void show(GSPanel source, GSLocation location) {
		show(source, location, GSEPopupPlacement.ABSOLUTE);
	}

	public void show(GSPanel source, GSLocation location, GSEPopupPlacement placement) {
		show(source, location.getX(), location.getY(), placement);
	}

	public void show(GSPanel source, int x, int y) {
		show(source, x, y, GSEPopupPlacement.ABSOLUTE);
	}

	public void show(GSPanel source, int x, int y, GSEPopupPlacement placement) {
		if (placement == null)
			throw new IllegalArgumentException("placement is null");
		if (getParent() != null)
			return;

		// Update source (root panel if not specified).
		GSRootPanel rootPanel = GSPanelContext.getRootPanel();
		this.source = source != null ? source : rootPanel;
		this.source.incrementPopupCount();
		
		this.placement = placement;
		this.relX = x;
		this.relY = y;

		super.add(content);
	
		rootPanel.add(this, GSRootPanel.POPUP_LAYER);
		GSPanelContext.getEventDispatcher().pushTopPopup(this);
		
		if (isStealingFocus())
			content.requestFocus();
	}
	
	private void updateBounds() {
		GSDimension ps = getProperty(PREFERRED_SIZE);

		int rx = relX, ry = relY;
		if (source != null) {
			GSLocation viewLocation = GSPanelUtil.getViewLocation(source);
			
			int dw = source.getWidth() - ps.getWidth();
			switch (placement) {
			case RELATIVE:
				rx += viewLocation.getX();
				break;
			case NORTHWEST:
			case WEST:
			case SOUTHWEST:
				rx = viewLocation.getX();
			case NORTH:
			case CENTER:
			case SOUTH:
				rx = viewLocation.getX() + dw / 2;
				break;
			case NORTHEAST:
			case EAST:
			case SOUTHEAST:
				rx = viewLocation.getX() + dw;
			case ABSOLUTE:
			default:
				break;
			}

			int dh = source.getHeight() - ps.getHeight();
			switch (placement) {
			case RELATIVE:
				ry += viewLocation.getY();
				break;
			case NORTHWEST:
			case NORTH:
			case NORTHEAST:
				ry = viewLocation.getY();
			case WEST:
			case CENTER:
			case EAST:
				ry = viewLocation.getY() + dh / 2;
				break;
			case SOUTHWEST:
			case SOUTH:
			case SOUTHEAST:
				ry = viewLocation.getY() + dh;
			case ABSOLUTE:
			default:
				break;
			}
		}
		
		setBounds(adjustLocation(rx, ry, ps), ps);
	}
	
	private GSLocation adjustLocation(int x, int y, GSDimension size) {
		GSRootPanel rootPanel = GSPanelContext.getRootPanel();
		if (x + size.getWidth() >= rootPanel.getWidth()) {
			// Force pop-up to be left of right
			x = rootPanel.getWidth() - size.getWidth();
		}
		if (y + size.getHeight() >= rootPanel.getHeight()) {
			// Force pop-up to be above bottom
			y = rootPanel.getHeight() - size.getHeight();
		}
		return new GSLocation(x, y);
	}
	
	public void hide() {
		GSPanelContext.getEventDispatcher().popTopPopup(this);
		
		// Transfer focus before losing it due to being removed from parent.
		if (this.source != null) {
			GSPanel source = this.source;
			this.source = null;
			source.decrementPopupCount();
			if (sourceFocusedOnHide && source.isAdded())
				source.requestFocus();
		}

		super.remove(content);

		GSPanel parent = getParent();
		if (parent != null)
			parent.remove(this);
	}
	
	@Override
	public void render(GSIRenderer2D renderer) {
		renderShadow(renderer);
		renderBackground(renderer);
		
		// Fix issues with text rendering (depth enabled)
		renderer.pushMatrix();
		renderer.translateDepth(0.1f);
		super.render(renderer);
		renderer.popMatrix();
	}
	
	protected void renderShadow(GSIRenderer2D renderer) {
		renderer.pushMatrix();
		// Translate to top-left of shadow
		renderer.translate(SHADOW_OFFSET_X - SHADOW_WIDTH,
		                   SHADOW_OFFSET_Y - SHADOW_WIDTH);
		renderer.build(GSIRenderer2D.QUADS, VertexFormats.POSITION_COLOR);

		int w  = width  - SHADOW_OFFSET_X;
		int h  = height - SHADOW_OFFSET_Y;
		int bx = width  + SHADOW_WIDTH - SHADOW_OFFSET_X;
		int by = height + SHADOW_WIDTH - SHADOW_OFFSET_Y;
		
		// Left, right, top, and bottom shadows
		renderer.fillHGradient( 0, SHADOW_WIDTH, SHADOW_WIDTH, h, 0, SHADOW_COLOR);
		renderer.fillHGradient(bx, SHADOW_WIDTH, SHADOW_WIDTH, h, SHADOW_COLOR, 0);
		renderer.fillVGradient(SHADOW_WIDTH,  0, w, SHADOW_WIDTH, 0, SHADOW_COLOR);
		renderer.fillVGradient(SHADOW_WIDTH, by, w, SHADOW_WIDTH, SHADOW_COLOR, 0);
		
		// Top-left, top-right, bottom-left, bottom-right shadows
		renderer.fillGradient( 0,  0, SHADOW_WIDTH, SHADOW_WIDTH, 0, 0, 0, SHADOW_COLOR, false);
		renderer.fillGradient(bx,  0, SHADOW_WIDTH, SHADOW_WIDTH, 0, 0, SHADOW_COLOR, 0, true);
		renderer.fillGradient( 0, by, SHADOW_WIDTH, SHADOW_WIDTH, 0, SHADOW_COLOR, 0, 0, true);
		renderer.fillGradient(bx, by, SHADOW_WIDTH, SHADOW_WIDTH, SHADOW_COLOR, 0, 0, 0, false);

		renderer.finish();
		renderer.popMatrix();
	}
	
	protected void renderBackground(GSIRenderer2D renderer) {
		if (GSColorUtil.unpackA(backgroundColor) != 0x00)
			renderer.fillRect(0, 0, width, height, backgroundColor);
	}
	
	public boolean isSourceFocusedOnHide() {
		return sourceFocusedOnHide;
	}
	
	public void setSourceFocusedOnHide(boolean focusedOnHide) {
		this.sourceFocusedOnHide = focusedOnHide;
	}
	
	public boolean isStealingFocus() {
		return stealingFocus;
	}
	
	public boolean isHiddenOnFocusLost() {
		return hiddenOnFocusLost;
	}
	
	public void setHiddenOnFocusLost(boolean flag) {
		if (flag != hiddenOnFocusLost) {
			hiddenOnFocusLost = flag;
			
			if (flag && isAdded() && !GSPanelUtil.isFocusWithin(this))
				hide();
		}
	}
	
	public int getBackgroundColor() {
		return backgroundColor;
	}

	public void setBackgroundColor(int backgroundColor) {
		this.backgroundColor = backgroundColor;
	}

	private class GSSourceLayoutListener implements GSILayoutEventListener {

		@Override
		public void panelMoved(GSLayoutEvent event) {
			// TODO(Christian): also capture events further up in the tree
			if (source != null && source.isVisible())
				invalidate();
		}

		@Override
		public void panelResized(GSLayoutEvent event) {
			if (source != null && source.isVisible())
				invalidate();
		}
	}
}
