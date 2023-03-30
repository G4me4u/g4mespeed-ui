package com.g4mesoft.ui.panel.table;

import com.g4mesoft.ui.panel.GSDimension;
import com.g4mesoft.ui.panel.GSPanel;
import com.g4mesoft.ui.panel.GSRectangle;
import com.g4mesoft.ui.panel.cell.GSCellContext;
import com.g4mesoft.ui.panel.scroll.GSIScrollable;
import com.g4mesoft.ui.renderer.GSIRenderer2D;
import com.g4mesoft.ui.util.GSColorUtil;

public class GSTableColumnHeaderPanel extends GSPanel implements GSIScrollable {

	private final GSTablePanel table;

	public GSTableColumnHeaderPanel(GSTablePanel table) {
		this.table = table;
	}
	
	@Override
	public void render(GSIRenderer2D renderer) {
		super.render(renderer);
		
		GSRectangle clipBounds = renderer.getClipBounds()
				.intersection(0, 0, width, height);
		
		drawBackground(renderer, clipBounds);
		drawHeaders(renderer, clipBounds);
	}
	
	private GSCellContext initCellContext(GSCellContext context, int columnIndex, GSRectangle bounds) {
		context = table.initCellContext(context, columnIndex, GSTablePanel.INVALID_HEADER_INDEX, bounds);
		context.backgroundColor = GSColorUtil.darker(context.backgroundColor);
		return context;
	}
	
	private void drawBackground(GSIRenderer2D renderer, GSRectangle clipBounds) {
		int tbgc = table.isEnabled() ? table.getBackgroundColor() :
			                           table.getDisabledBackgroundColor();
		if (GSColorUtil.unpackA(tbgc) != 0x00) {
			renderer.fillRect(clipBounds.x, clipBounds.y, clipBounds.width,
					clipBounds.height, GSColorUtil.darker(tbgc));
		}
		// Check if we have a selection and draw its background
		if (table.getColumnSelectionPolicy() != GSEHeaderSelectionPolicy.DISABLED &&
				table.hasSelection()) {
			GSRectangle sb = new GSRectangle(0, 0, 0, height);
			table.computeColumnSelectionBounds(sb);
			int sc = GSColorUtil.darker(table.getSelectionBackgroundColor());
			renderer.fillRect(sb.x, sb.y, sb.width, sb.height, sc);
		}
	}
	
	private void drawHeaders(GSIRenderer2D renderer, GSRectangle clipBounds) {
		GSITableModel model = table.getModel();
		GSCellContext context = new GSCellContext();
		GSRectangle bounds = new GSRectangle();
		bounds.x = table.getVerticalBorderWidth();
		bounds.y = clipBounds.y;
		bounds.height = clipBounds.height;
		for (int c = 0; c < model.getColumnCount() && bounds.x < clipBounds.x + clipBounds.width; c++) {
			GSITableColumn column = model.getColumn(c);
			bounds.width = column.getWidth();
			if (bounds.x + bounds.width >= clipBounds.x) {
				initCellContext(context, c, bounds);
				renderHeader(renderer, column.getHeaderValue(), context);
			}
			bounds.x += bounds.width + table.getVerticalBorderWidth();
		}
	}
	
	private <T> void renderHeader(GSIRenderer2D renderer, T value, GSCellContext context) {
		table.getCellRenderer(value).render(renderer, value, context);
	}
	
	@Override
	protected GSDimension calculateMinimumSize() {
		return GSTableLayoutManager.getColumnHeaderSize(table, false, false);
	}

	@Override
	protected GSDimension calculatePreferredSize() {
		return GSTableLayoutManager.getColumnHeaderSize(table, true, false);
	}

	@Override
	public GSDimension getMinimumScrollableSize() {
		return GSTableLayoutManager.getColumnHeaderSize(table, false, true);
	}

	@Override
	public GSDimension getPreferredScrollableSize() {
		return GSTableLayoutManager.getColumnHeaderSize(table, true, true);
	}
	
	@Override
	public boolean isScrollableWidthFilled() {
		return true;
	}
}
