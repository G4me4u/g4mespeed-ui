package com.g4mesoft.ui.panel.dialog;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.g4mesoft.ui.panel.GSEAnchor;
import com.g4mesoft.ui.panel.GSEFill;
import com.g4mesoft.ui.panel.GSEPopupPlacement;
import com.g4mesoft.ui.panel.GSGridLayoutManager;
import com.g4mesoft.ui.panel.GSIActionListener;
import com.g4mesoft.ui.panel.GSLayoutProperties;
import com.g4mesoft.ui.panel.GSMargin;
import com.g4mesoft.ui.panel.GSPanel;
import com.g4mesoft.ui.panel.GSParentPanel;
import com.g4mesoft.ui.panel.GSPopup;
import com.g4mesoft.ui.panel.button.GSButton;
import com.g4mesoft.ui.panel.event.GSKeyButtonStroke;
import com.g4mesoft.ui.panel.event.GSKeyEvent;
import com.g4mesoft.ui.panel.field.GSTextLabel;
import com.g4mesoft.ui.util.GSTextUtil;

import net.minecraft.text.Text;

public class GSConfirmDialog extends GSParentPanel {

	public static final GSConfirmOption[] YES_NO_OPTIONS = {
		GSConfirmOption.YES,
		GSConfirmOption.NO
	};
	public static final GSConfirmOption[] YES_CANCEL_OPTIONS = {
			GSConfirmOption.YES,
			GSConfirmOption.CANCEL
	};
	public static final GSConfirmOption[] OK_OPTIONS = {
		GSConfirmOption.OK
	};
	public static final Text ARE_YOU_SURE_TEXT =
			GSTextUtil.translatable("panel.confirmDialog.areYouSure");
	
	private static final GSMargin OUTER_MARGIN   = new GSMargin(5);
	private static final GSMargin ELEMENT_MARGIN = new GSMargin(3);
	private static final int BUTTON_HORIZ_MARGIN = 10;
	
	private final GSTextLabel title;
	private final GSConfirmOption[] options;
	private final GSButton[] buttons;
	private GSPanel content;
	
	private GSConfirmOption selectedOption;

	private List<GSIActionListener> actionListeners;

	public GSConfirmDialog(Text title, GSConfirmOption[] options) {
		this(title, options, null);
	}

	public GSConfirmDialog(Text title, GSConfirmOption[] options, GSPanel content) {
		if (options == null)
			throw new IllegalArgumentException("options is null");
		// Ensure that all options are non-null
		for (GSConfirmOption option : options) {
			if (option == null)
				throw new IllegalArgumentException("options[i] is null");
		}
		this.title = new GSTextLabel(title);
		this.options = Arrays.copyOf(options, options.length);
		// Make sure options are in order left-to-right.
		Arrays.sort(this.options);
		// Prepare buttons
		buttons = new GSButton[this.options.length];
		for (int i = 0; i < this.options.length; i++) {
			GSConfirmOption option = this.options[i];
			buttons[i] = new GSButton(option.getText());
			buttons[i].setHorizontalMargin(BUTTON_HORIZ_MARGIN);
		}
		
		actionListeners = new ArrayList<>();

		setContent(content);
		
		initLayout();
		initEventListeners();
	}
	
	private void initLayout() {
		getLayout()
			.set(GSLayoutProperties.MARGIN, OUTER_MARGIN);
	
		setLayoutManager(new GSGridLayoutManager());
	
		title.getLayout()
			.set(GSGridLayoutManager.GRID_X, 0)
			.set(GSGridLayoutManager.GRID_Y, 0)
			.set(GSGridLayoutManager.WEIGHT_X, 1.0f)
			.set(GSGridLayoutManager.ANCHOR, GSEAnchor.CENTER)
			.set(GSGridLayoutManager.MARGIN, ELEMENT_MARGIN);
		add(title);
		
		GSPanel buttonPanel = new GSParentPanel();
		buttonPanel.getLayout()
			.set(GSGridLayoutManager.GRID_X, 0)
			.set(GSGridLayoutManager.GRID_Y, 2)
			.set(GSGridLayoutManager.WEIGHT_X, 1.0f)
			.set(GSGridLayoutManager.FILL, GSEFill.BOTH);
		initButtonLayout(buttonPanel);
		add(buttonPanel);
	}
	
	private void initButtonLayout(GSPanel buttonPanel) {
		buttonPanel.setLayoutManager(new GSGridLayoutManager());
		
		for (int i = 0, len = buttons.length; i < len; i++) {
			GSButton button = buttons[i];
			GSEConfirmOptionPlacement placement = options[i].getPlacement();
			button.getLayout()
				.set(GSGridLayoutManager.GRID_X, i)
				.set(GSGridLayoutManager.GRID_Y, 0)
				.set(GSGridLayoutManager.MARGIN, ELEMENT_MARGIN)
				.set(GSGridLayoutManager.ANCHOR, toAnchor(placement));
			if (placement != GSEConfirmOptionPlacement.RIGHT) {
				// Insert spacing if next does not have the same placement
				if (i == len - 1 || placement != options[i + 1].getPlacement()) {
					button.getLayout()
						.set(GSGridLayoutManager.WEIGHT_X, 1.0f);
				}
			}
			if (placement != GSEConfirmOptionPlacement.LEFT) {
				// Insert spacing if previous does not have the same placement
				if (i == 0 || placement != options[i - 1].getPlacement()) {
					button.getLayout()
						.set(GSGridLayoutManager.WEIGHT_X, 1.0f);
				}
			}
			buttonPanel.add(button);
		}
	}
	
	private GSEAnchor toAnchor(GSEConfirmOptionPlacement placement) {
		switch (placement) {
		case LEFT:
			return GSEAnchor.WEST;
		case CENTER:
			return GSEAnchor.CENTER;
		case RIGHT:
			return GSEAnchor.EAST;
		}
		throw new IllegalStateException("Unknown placement");
	}
	
	private void initEventListeners() {
		for (int i = 0; i < buttons.length; i++) {
			GSButton button = buttons[i];
			final GSConfirmOption option = this.options[i];
			button.addActionListener(() -> {
				optionClicked(option);
			});
		}
		putButtonStroke(new GSKeyButtonStroke(GSKeyEvent.KEY_ESCAPE), this::cancel);
	}
	
	private void optionClicked(GSConfirmOption option) {
		selectedOption = option;
		close();
		dispatchActionPerformedEvent();
	}
	
	private void cancel() {
		optionClicked(null);
	}
	
	public void close() {
		GSPanel parent = getParent();
		if (parent instanceof GSPopup)
			((GSPopup)parent).hide();
	}
	
	public void addActionListener(GSIActionListener listener) {
		if (listener == null)
			throw new IllegalArgumentException("listener is null");
		actionListeners.add(listener);
	}
	
	public void removeActionListener(GSIActionListener listener) {
		actionListeners.remove(listener);
	}
	
	private void dispatchActionPerformedEvent() {
		actionListeners.forEach(GSIActionListener::actionPerformed);
	}

	public void setTitle(String text) {
		setTitle((text != null) ? GSTextUtil.literal(text) : null);
	}
	
	public void setTitle(Text text) {
		title.setText(text);
	}
	
	public void setContent(GSPanel content) {
		if (this.content != null)
			remove(this.content);
		this.content = content;
		if (content != null) {
			content.getLayout()
				.set(GSGridLayoutManager.GRID_X, 0)
				.set(GSGridLayoutManager.GRID_Y, 1)
				.set(GSGridLayoutManager.WEIGHT_X, 1.0f)
				.set(GSGridLayoutManager.WEIGHT_Y, 1.0f)
				.set(GSGridLayoutManager.ANCHOR, GSEAnchor.CENTER)
				.set(GSGridLayoutManager.FILL, GSEFill.BOTH)
				.set(GSGridLayoutManager.MARGIN, ELEMENT_MARGIN);
			add(content);
		}
	}
	
	public int getOptionCount() {
		return options.length;
	}
	
	public GSConfirmOption getOption(int index) {
		if (index < 0 || index >= options.length)
			throw new IndexOutOfBoundsException("Index out of range: " + index);
		return options[index];
	}
	
	public boolean hasSelection() {
		return selectedOption != null;
	}
	
	public GSConfirmOption getSelectedOption() {
		return selectedOption;
	}
	
	/**
	 * Shows a dialog with a YES and NO option, and a title asking whether
	 * the user is sure about performing the operation which resulted in the
	 * dialog. See {@link #showDialog(GSPanel, Text, GSConfirmOption[]))} for
	 * more info.
	 * 
	 * @param source - the panel responsible for this dialog
	 * 
	 * @return The instance of the confirm dialog that is shown.
	 * 
	 * @see #showDialog(GSPanel, Text, GSConfirmOption[])
	 */
	public static GSConfirmDialog showYesNoDialog(GSPanel source) {
		return showDialog(source, ARE_YOU_SURE_TEXT, OK_OPTIONS);
	}
	
	/**
	 * Shows a dialog with a YES and CANCEL option, and a title asking whether
	 * the user is sure about performing the operation which resulted in the
	 * dialog. See {@link #showDialog(GSPanel, Text, GSConfirmOption[]))} for
	 * more info.
	 * 
	 * @param source - the panel responsible for this dialog
	 * 
	 * @return The instance of the confirm dialog that is shown.
	 * 
	 * @see #showDialog(GSPanel, Text, GSConfirmOption[])
	 */
	public static GSConfirmDialog showYesCancelDialog(GSPanel source) {
		return showDialog(source, ARE_YOU_SURE_TEXT, OK_OPTIONS);
	}
	
	/**
	 * Shows a dialog with an OK option, with the specified title. See
	 * {@link #showDialog(GSPanel, Text, GSConfirmOption[]))} for more info.
	 * 
	 * @param source - the panel responsible for this dialog
	 * @param title - the title of the dialog
	 * 
	 * @return The instance of the confirm dialog that is shown.
	 * 
	 * @see #showDialog(GSPanel, Text, GSConfirmOption[])
	 */
	public static GSConfirmDialog showOkDialog(GSPanel source, Text title) {
		return showDialog(source, title, OK_OPTIONS);
	}
	
	/**
	 * Shows a confirm dialog with the given title, where the user can select
	 * among the given options. The instance that is returned can be used to
	 * further customize the dialog. In order to obtain the result of the dialog,
	 * the following code can be used:
	 * <pre>
	 * GSConfirmDialog dialog = GSConfirmDialog.showDialog(...);
	 * dialog.addActionListener(() -> {
	 *     if (dialog.hasSelection()) {
	 *         GSConfirmOption option = dialog.getSelectedOption();
	 *         // user chose the option
	 *     } else {
	 *         // user pressed escape
	 *     }
	 * );
	 * </pre>
	 * The method {@link #setContent(GSPanel)} is useful for further clarifying
	 * what the user is responding to.
	 * 
	 * @param source - the panel responsible for this dialog
	 * @param title - the title of the dialog
	 * @param options - the options which the user can choose
	 * 
	 * @return The instance of the confirm dialog that is shown.
	 */
	public static GSConfirmDialog showDialog(GSPanel source, Text title, GSConfirmOption[] options) {
		GSConfirmDialog dialog = new GSConfirmDialog(title, options);
		GSPopup popup = new GSPopup(dialog, true);
		popup.show(source, 0, 0, GSEPopupPlacement.CENTER);
		return dialog;
	}
}
