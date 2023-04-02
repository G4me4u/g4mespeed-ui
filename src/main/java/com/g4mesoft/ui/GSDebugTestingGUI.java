package com.g4mesoft.ui;

import com.g4mesoft.ui.panel.GSClosableParentPanel;
import com.g4mesoft.ui.panel.GSDimension;
import com.g4mesoft.ui.panel.GSEAnchor;
import com.g4mesoft.ui.panel.GSEFill;
import com.g4mesoft.ui.panel.GSFileDialog;
import com.g4mesoft.ui.panel.GSGridLayoutManager;
import com.g4mesoft.ui.panel.GSPanel;
import com.g4mesoft.ui.panel.GSPanelContext;
import com.g4mesoft.ui.panel.button.GSButton;
import com.g4mesoft.ui.panel.dropdown.GSDropdownList;
import com.g4mesoft.ui.panel.scroll.GSScrollPanel;
import com.g4mesoft.ui.panel.scroll.GSViewport;
import com.g4mesoft.ui.panel.table.GSITableModel;
import com.g4mesoft.ui.panel.table.GSTablePanel;

import net.minecraft.text.LiteralText;

public class GSDebugTestingGUI extends GSClosableParentPanel {

	private static final int COLUMN_COUNT = 10;
	private static final int ROW_COUNT = 20;
	
	private GSPanel previousPanel;
	
	public GSDebugTestingGUI() {
		setLayoutManager(new GSGridLayoutManager());
		
		GSButton button = new GSButton("Reset Table");
		button.getLayout()
			.set(GSGridLayoutManager.GRID_X, 0)
			.set(GSGridLayoutManager.GRID_Y, 1)
			.set(GSGridLayoutManager.ANCHOR, GSEAnchor.CENTER)
			.set(GSGridLayoutManager.FILL, GSEFill.NONE);
		button.addActionListener(this::reset);
		add(button);
		
		GSDropdownList<String> dropdown = new GSDropdownList<>(
			new String[] { 
				"One value",
				"Two values",
				"Three",
				"A",
				"B",
				"C"
			}
		);
		dropdown.getLayout()
			.set(GSGridLayoutManager.GRID_X, 0)
			.set(GSGridLayoutManager.GRID_Y, 2)
			.set(GSGridLayoutManager.ANCHOR, GSEAnchor.CENTER)
			.set(GSGridLayoutManager.FILL, GSEFill.NONE);
		dropdown.addChangeListener(() -> {
			System.out.println(dropdown.getSelectedItem());
		});
		add(dropdown);
		
		GSButton closeButton = new GSButton("Close");
		closeButton.getLayout()
			.set(GSGridLayoutManager.GRID_X, 0)
			.set(GSGridLayoutManager.GRID_Y, 3)
			.set(GSGridLayoutManager.ANCHOR, GSEAnchor.CENTER)
			.set(GSGridLayoutManager.FILL, GSEFill.NONE);
		closeButton.addActionListener(() -> {
			GSPanelContext.openContent(null);
		});
		add(closeButton);
		
		GSButton fileButton = new GSButton("Open file dialog");
		fileButton.getLayout()
			.set(GSGridLayoutManager.GRID_X, 0)
			.set(GSGridLayoutManager.GRID_Y, 4)
			.set(GSGridLayoutManager.ANCHOR, GSEAnchor.CENTER)
			.set(GSGridLayoutManager.FILL, GSEFill.NONE);
		fileButton.addActionListener(() -> {
			GSFileDialog dialog = GSFileDialog.showOpenDialog(null);
			dialog.addActionListener(() -> {
				if (dialog.isCanceled()) {
					System.out.println("File dialog canceled.");
				} else {
					System.out.println("File dialog chose: " + dialog.getSelectedPath());
				}
			});
		});
		add(fileButton);
		
		reset();
	}
	
	private void reset() {
		if (previousPanel != null)
			remove(previousPanel);
		
		GSTablePanel table = new GSTablePanel(COLUMN_COUNT, ROW_COUNT);

		GSITableModel model = table.getModel();
		for (int c = 0; c < model.getColumnCount(); c++)
			model.getColumn(c).setHeaderValue("Column " + (c + 1));
		for (int r = 0; r < model.getRowCount(); r++)
			model.getRow(r).setHeaderValue("Row " + (r + 1));
		table.setPreferredRowCount(15);
		table.setPreferredColumnCount(Integer.MAX_VALUE);
		
		for (int c = 0; c < model.getColumnCount(); c++) {
			for (int r = 0; r < model.getRowCount(); r++)
				model.setCellValue(c, r, new LiteralText(String.format("(%d, %d)", r + 1, c + 1)));
		}

		boolean withScroll = true;
		if (withScroll) {
			previousPanel = new GSScrollPanel(table);
		} else {
			previousPanel = table;
		}

		previousPanel.getLayout()
			.set(GSGridLayoutManager.GRID_X, 0)
			.set(GSGridLayoutManager.GRID_Y, 0)
			.set(GSGridLayoutManager.ANCHOR, GSEAnchor.CENTER)
			.set(GSGridLayoutManager.FILL, GSEFill.NONE);
		add(previousPanel);
		
		boolean printPreferredSize = false;
		if (printPreferredSize) {
			GSPanel parent = table.getParent();
			if (parent instanceof GSViewport) {
				GSDimension vPrefSize = ((GSViewport)parent).getProperty(GSPanel.PREFERRED_SIZE);
				System.out.printf("w: %d, h: %d\n", vPrefSize.getWidth(), vPrefSize.getHeight());
			}
		}
	}
}
