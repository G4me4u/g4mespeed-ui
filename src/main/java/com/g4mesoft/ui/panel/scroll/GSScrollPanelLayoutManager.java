package com.g4mesoft.ui.panel.scroll;

import com.g4mesoft.ui.panel.GSDimension;
import com.g4mesoft.ui.panel.GSILayoutManager;
import com.g4mesoft.ui.panel.GSILayoutProperty;
import com.g4mesoft.ui.panel.GSPanel;
import com.g4mesoft.ui.panel.GSParentPanel;

public class GSScrollPanelLayoutManager implements GSILayoutManager {

	@Override
	public GSDimension getMinimumSize(GSParentPanel parent) {
		return computeSize(parent, GSPanel.MINIMUM_SIZE);
	}

	@Override
	public GSDimension getPreferredSize(GSParentPanel parent) {
		return computeSize(parent, GSPanel.PREFERRED_SIZE);
	}
	
	private GSDimension computeSize(GSParentPanel parent, GSILayoutProperty<GSDimension> sizeProperty) {
		GSScrollPanel scrollPanel = (GSScrollPanel)parent;
		
		GSViewport contentViewport = scrollPanel.getContentViewport();
		GSDimension contentSize = contentViewport.getProperty(sizeProperty);
		long wl = contentSize.getWidth(), hl = contentSize.getHeight();
		
		GSViewport columnHeaderViewport = scrollPanel.getColumnHeaderViewport();
		if (!columnHeaderViewport.isEmpty()) {
			GSDimension columnHeaderSize = columnHeaderViewport.getProperty(sizeProperty);
			if (columnHeaderSize.getWidth() > wl)
				wl = columnHeaderSize.getWidth();
			hl += columnHeaderSize.getHeight();
		}

		GSViewport rowHeaderViewport = scrollPanel.getRowHeaderViewport();
		if (!rowHeaderViewport.isEmpty()) {
			GSDimension rowHeaderSize = rowHeaderViewport.getProperty(sizeProperty);
			wl += rowHeaderSize.getWidth();
			if (rowHeaderSize.getHeight() > hl)
				hl = rowHeaderSize.getHeight();
		}
		
		GSDimension scrollableSize = GSDimension.ZERO;
		GSPanel content = contentViewport.getContent();
		if (content != null) {
			// Retrieve the size allocated by the viewport.
			scrollableSize = content.getProperty(sizeProperty);
		}

		GSEScrollBarPolicy vsbp = scrollPanel.getVerticalScrollBarPolicy();
		if (vsbp == GSEScrollBarPolicy.SCROLLBAR_ALWAYS ||
				(vsbp == GSEScrollBarPolicy.SCROLLBAR_AS_NEEDED &&
				scrollableSize.getHeight() > contentSize.getHeight())) {
			GSScrollBar verticalScrollBar = scrollPanel.getVerticalScrollBar();
			GSDimension vsbSize = verticalScrollBar.getProperty(sizeProperty);
			wl += vsbSize.getWidth();
			if (vsbSize.getHeight() > hl)
				hl = vsbSize.getHeight();
		}

		GSEScrollBarPolicy hsbp = scrollPanel.getHorizontalScrollBarPolicy();
		if (hsbp == GSEScrollBarPolicy.SCROLLBAR_ALWAYS ||
				(hsbp == GSEScrollBarPolicy.SCROLLBAR_AS_NEEDED &&
				scrollableSize.getWidth() > contentSize.getWidth())) {
			GSScrollBar horizontalScrollBar = scrollPanel.getHorizontalScrollBar();
			GSDimension hsbSize = horizontalScrollBar.getProperty(sizeProperty);
			if (hsbSize.getWidth() > wl)
				wl = hsbSize.getWidth();
			hl += hsbSize.getHeight();
		}
		
		int w = (int)Math.min(wl, Integer.MAX_VALUE);
		int h = (int)Math.min(hl, Integer.MAX_VALUE);
		
		return new GSDimension(w, h);
	}

	@Override
	public void layoutChildren(GSParentPanel parent) {
		GSScrollPanel scrollPanel = (GSScrollPanel)parent;
		
		GSViewport contentViewport = scrollPanel.getContentViewport();
		GSViewport columnHeaderViewport = scrollPanel.getColumnHeaderViewport();
		GSViewport rowHeaderViewport = scrollPanel.getRowHeaderViewport();

		GSPanel content = contentViewport.getContent();
		GSPanel columnHeader = columnHeaderViewport.getContent();
		GSPanel rowHeader = rowHeaderViewport.getContent();
		
		int availW = parent.getWidth();
		int availH = parent.getHeight();
		
		boolean chPresent = columnHeader != null;
		boolean rhPresent = rowHeader != null;
		
		int chPrefW, chh;
		if (chPresent) {
			GSDimension size = columnHeader.getProperty(GSPanel.PREFERRED_SIZE);
			chPrefW = size.getWidth();
			chh = Math.min(availH, size.getHeight());
			availH -= chh;
		} else {
			chPrefW = chh = 0;
		}
		
		int rhw, rhPrefH;
		if (rhPresent) {
			GSDimension size = rowHeader.getProperty(GSPanel.PREFERRED_SIZE);
			rhw = Math.min(availW, size.getWidth());
			rhPrefH = size.getHeight();
			availW -= rhw;
		} else {
			rhw = rhPrefH = 0;
		}

		GSDimension contentPrefSize = (content != null) ? content.getProperty(GSPanel.PREFERRED_SIZE) : GSDimension.ZERO;
		
		GSScrollBar verticalScrollBar = scrollPanel.getVerticalScrollBar();
		GSScrollBar horizontalScrollBar = scrollPanel.getHorizontalScrollBar();

		GSDimension vsbPrefSize = verticalScrollBar.getProperty(GSPanel.PREFERRED_SIZE);
		GSDimension hsbPrefSize = horizontalScrollBar.getProperty(GSPanel.PREFERRED_SIZE);

		int scrollableW = Math.max(contentPrefSize.getWidth(), chPrefW);
		int scrollableH = Math.max(contentPrefSize.getHeight(), rhPrefH);
		
		boolean vsbNeeded;
		switch (scrollPanel.getVerticalScrollBarPolicy()) {
		case SCROLLBAR_ALWAYS:
			vsbNeeded = true;
			break;
		case SCROLLBAR_AS_NEEDED:
		default:
			vsbNeeded = (scrollableH > availH);
			break;
		case SCROLLBAR_NEVER:
			vsbNeeded = false;
			break;
		}

		if (vsbNeeded)
			availW -= vsbPrefSize.getWidth();
		
		boolean hsbNeeded;
		switch (scrollPanel.getHorizontalScrollBarPolicy()) {
		case SCROLLBAR_ALWAYS:
			hsbNeeded = true;
			break;
		case SCROLLBAR_AS_NEEDED:
		default:
			hsbNeeded = (scrollableW > availW);
			break;
		case SCROLLBAR_NEVER:
			hsbNeeded = false;
			break;
		}
		
		if (hsbNeeded) {
			availH -= hsbPrefSize.getHeight();

			if (!vsbNeeded && scrollPanel.getVerticalScrollBarPolicy() == GSEScrollBarPolicy.SCROLLBAR_AS_NEEDED) {
				// Check if we have to add the vertical scroll bar with
				// the updated height from the horizontal scroll bar.
				if (scrollableH > availH) {
					vsbNeeded = true;
					availW -= vsbPrefSize.getWidth();
				}
			}
		}
		
		availW = Math.max(availW, 0);
		availH = Math.max(availH, 0);
		
		// Add and set bounds of the panels in the scroll panel.
		
		contentViewport.setBounds(rhw, chh, availW, availH);
		
		if (chPresent) {
			ensureAdded(parent, columnHeaderViewport);
			columnHeaderViewport.setBounds(rhw, 0, availW, chh);
		} else {
			ensureRemoved(parent, columnHeaderViewport);
		}
		
		if (rhPresent) {
			ensureAdded(parent, rowHeaderViewport);
			rowHeaderViewport.setBounds(0, chh, rhw, availH);
		} else {
			ensureRemoved(parent, rowHeaderViewport);
		}
		
		if (vsbNeeded) {
			ensureAdded(parent, verticalScrollBar);
			verticalScrollBar.setBounds(rhw + availW, chh, vsbPrefSize.getWidth(), availH);
		} else {
			ensureRemoved(parent, verticalScrollBar);
		}
		verticalScrollBar.getModel().setScrollInterval(0.0f, Math.max(0.0f, scrollableH - availH));
		
		if (hsbNeeded) {
			ensureAdded(parent, horizontalScrollBar);
			horizontalScrollBar.setBounds(rhw, chh + availH, availW, hsbPrefSize.getHeight());
		} else {
			ensureRemoved(parent, horizontalScrollBar);
		}
		horizontalScrollBar.getModel().setScrollInterval(0.0f, Math.max(0.0f, scrollableW - availW));
		
		// Add corners if they should be visible
		
		GSPanel topLeftCorner = scrollPanel.getTopLeftCorner();
		if (topLeftCorner != null) {
			if (chPresent && rhPresent) {
				ensureAdded(parent, topLeftCorner);
				topLeftCorner.setBounds(0, 0, rhw, chh);
			} else {
				ensureRemoved(parent, topLeftCorner);
			}
		}

		GSPanel topRightCorner = scrollPanel.getTopRightCorner();
		if (topRightCorner != null) {
			if (chPresent && vsbNeeded) {
				ensureAdded(parent, topRightCorner);
				topRightCorner.setBounds(rhw + availW, 0, vsbPrefSize.getWidth(), chh);
			} else {
				ensureRemoved(parent, topRightCorner);
			}
		}

		GSPanel bottomLeftCorner = scrollPanel.getBottomLeftCorner();
		if (bottomLeftCorner != null) {
			if (rhPresent && hsbNeeded) {
				ensureAdded(parent, bottomLeftCorner);
				bottomLeftCorner.setBounds(0, chh + availH, rhw, hsbPrefSize.getHeight());
			} else {
				ensureRemoved(parent, bottomLeftCorner);
			}
		}

		GSPanel bottomRightCorner = scrollPanel.getBottomRightCorner();
		if (bottomRightCorner != null) {
			if (vsbNeeded && hsbNeeded) {
				ensureAdded(parent, bottomRightCorner);
				bottomRightCorner.setBounds(rhw + availW, chh + availH, vsbPrefSize.getWidth(), hsbPrefSize.getHeight());
			} else {
				ensureRemoved(parent, bottomRightCorner);
			}
		}
	}
	
	private void ensureAdded(GSParentPanel parent, GSPanel panel) {
		if (!panel.isAdded())
			parent.add(panel);
	}

	private void ensureRemoved(GSParentPanel parent, GSPanel panel) {
		if (panel.isAdded())
			parent.remove(panel);
	}
}
