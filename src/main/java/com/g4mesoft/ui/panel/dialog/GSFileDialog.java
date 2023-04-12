package com.g4mesoft.ui.panel.dialog;

import java.io.File;
import java.io.IOException;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.InvalidPathException;
import java.nio.file.Path;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.g4mesoft.ui.panel.GSDimension;
import com.g4mesoft.ui.panel.GSEAnchor;
import com.g4mesoft.ui.panel.GSEFill;
import com.g4mesoft.ui.panel.GSEIconAlignment;
import com.g4mesoft.ui.panel.GSEPopupPlacement;
import com.g4mesoft.ui.panel.GSETextAlignment;
import com.g4mesoft.ui.panel.GSGridLayoutManager;
import com.g4mesoft.ui.panel.GSIActionListener;
import com.g4mesoft.ui.panel.GSIcon;
import com.g4mesoft.ui.panel.GSMargin;
import com.g4mesoft.ui.panel.GSPanel;
import com.g4mesoft.ui.panel.GSPanelContext;
import com.g4mesoft.ui.panel.GSPanelUtil;
import com.g4mesoft.ui.panel.GSParentPanel;
import com.g4mesoft.ui.panel.GSPopup;
import com.g4mesoft.ui.panel.button.GSButton;
import com.g4mesoft.ui.panel.cell.GSCellContext;
import com.g4mesoft.ui.panel.cell.GSICellRenderer;
import com.g4mesoft.ui.panel.dropdown.GSBasicDropdownListModel;
import com.g4mesoft.ui.panel.dropdown.GSDropdownList;
import com.g4mesoft.ui.panel.event.GSButtonStrokeBuilder;
import com.g4mesoft.ui.panel.event.GSFocusEvent;
import com.g4mesoft.ui.panel.event.GSIButtonStroke;
import com.g4mesoft.ui.panel.event.GSIFocusEventListener;
import com.g4mesoft.ui.panel.event.GSKeyEvent;
import com.g4mesoft.ui.panel.event.GSMouseEvent;
import com.g4mesoft.ui.panel.field.GSTextField;
import com.g4mesoft.ui.panel.field.GSTextLabel;
import com.g4mesoft.ui.panel.scroll.GSScrollPanel;
import com.g4mesoft.ui.panel.table.GSBasicTableModel;
import com.g4mesoft.ui.panel.table.GSEHeaderSelectionPolicy;
import com.g4mesoft.ui.panel.table.GSIHeaderSelectionListener;
import com.g4mesoft.ui.panel.table.GSIHeaderSelectionModel;
import com.g4mesoft.ui.panel.table.GSITableColumn;
import com.g4mesoft.ui.panel.table.GSITableModel;
import com.g4mesoft.ui.panel.table.GSTablePanel;
import com.g4mesoft.ui.renderer.GSIRenderer2D;
import com.g4mesoft.ui.util.GSPathUtil;
import com.google.common.base.Objects;

import net.minecraft.client.MinecraftClient;
import net.minecraft.text.Text;

public class GSFileDialog extends GSParentPanel implements GSIHeaderSelectionListener {

	/* Height of the rows in the file table */
	private static final int ROW_HEIGHT = 16;
	/* Indices pointing to the column of each of the titles */
	private static final int NAME_COLUMN_INDEX;
	private static final int MODIFIED_COLUMN_INDEX;
	private static final int TYPE_COLUMN_INDEX;
	private static final int SIZE_COLUMN_INDEX;
	/* Titles for the elements shown in the file table */
	private static final Text[] TABLE_TITLES;
	/* Minimum sizes of each of the columns in the file table */
	private static final int[] TABLE_WIDTHS;
	
	private static final Text[] CONFIRM_TEXTS;
	private static final Text CANCEL_TEXT       = translatable("cancel");
	private static final Text FILE_NAME_TEXT    = translatable("fileName");
	private static final Text CURRENT_PATH_TEXT = translatable("currentPath");
	
	private static final GSIcon ROOT_DIRECTORY_ICON = GSPanelContext.getIcon( 0, 72, 12, 12);
	private static final GSIcon DIRECTORY_ICON      = GSPanelContext.getIcon(12, 72, 12, 12);
	private static final GSIcon UNKNOWN_FILE_ICON   = GSPanelContext.getIcon( 0, 84, 12, 12);

	/* The icon and name that show as the 'go to parent directory' row. */
	private static final GSIcon GO_TO_PARENT_ICON = GSPanelContext.getIcon(24, 72, 12, 12);
	private static final GSFileName GO_TO_PARENT_NAME = new GSFileName("..", GO_TO_PARENT_ICON);
	
	private static final Text DIRECTORY_TYPE    = translatable("directoryType");
	private static final Text UNKNOWN_FILE_TYPE = translatable("unknownType");
	
	private static final int FILE_ICON_SPACING = 5;
	private static final GSMargin OUTER_MARGIN = new GSMargin(5);
	private static final GSMargin FIELD_MARGIN = new GSMargin(3);

	private static final GSIButtonStroke BACKWARD_STROKE = GSButtonStrokeBuilder.get()
			.key(GSKeyEvent.KEY_BACKSPACE).mouse(GSMouseEvent.BUTTON_4).build();
	private static final GSIButtonStroke FORWARD_STROKE = GSButtonStrokeBuilder.get()
			.mouse(GSMouseEvent.BUTTON_5).build();
	
	private static final Map<String, GSIcon> extensionToFileIcon;
	
	/* Compute title column indices */
	static {
		int titleCount = 0;
		// Table title order is specified by order of these.
		NAME_COLUMN_INDEX     = titleCount++;
		MODIFIED_COLUMN_INDEX = titleCount++;
		TYPE_COLUMN_INDEX     = titleCount++;
		SIZE_COLUMN_INDEX     = titleCount++;
		// Compute array with table titles
		TABLE_TITLES = new Text[titleCount];
		TABLE_TITLES[NAME_COLUMN_INDEX]     = translatable("name");
		TABLE_TITLES[MODIFIED_COLUMN_INDEX] = translatable("modified");
		TABLE_TITLES[TYPE_COLUMN_INDEX]     = translatable("type");
		TABLE_TITLES[SIZE_COLUMN_INDEX]     = translatable("size");
		// Compute array with table widths
		TABLE_WIDTHS = new int[titleCount];
		TABLE_WIDTHS[NAME_COLUMN_INDEX]     = 150;
		TABLE_WIDTHS[MODIFIED_COLUMN_INDEX] = 120;
		TABLE_WIDTHS[TYPE_COLUMN_INDEX]     = 80;
		TABLE_WIDTHS[SIZE_COLUMN_INDEX]     = 50;
		// Asset type value text
		GSEFileDialogMode[] modes = GSEFileDialogMode.values();
		CONFIRM_TEXTS = new Text[modes.length];
		for (GSEFileDialogMode mode : modes)
			CONFIRM_TEXTS[mode.getIndex()] = translatable("mode." + mode.getName());
		// Initialize default file icons
		extensionToFileIcon = new HashMap<>();
		initDefaultFileIcons();
	}
	
	private static Text translatable(String key, Object... args) {
		return Text.translatable("panel.fileDialog." + key, args);
	}
	
	private Path directory;

	private GSEFileDialogMode mode;
	private GSEFileDialogSelectionMode selectionMode;
	
	private final GSTextField pathField;
	private final GSTextField nameField;
	
	private GSIFileNameFilter fileNameFilter;
	private final GSDropdownList<Text> filterField;
	
	private final GSTablePanel fileTable;
	
	private final GSButton confirmButton;
	private final GSButton cancelButton;
	
	private boolean rootDirectories;
	private List<Path> paths;
	
	private Path selectedPath;
	private boolean canceled;
	
	private List<GSIActionListener> actionListeners;

	public GSFileDialog() {
		this(getDefaultDirectory());
	}

	public GSFileDialog(File directory) {
		this(directory.toPath());
	}
	
	public GSFileDialog(Path directory) {
		this.directory = directory;
		this.mode = GSEFileDialogMode.OPEN;
		this.selectionMode = GSEFileDialogSelectionMode.FILES_ONLY;
		
		pathField = new GSTextField();
		nameField = new GSTextField();
		
		fileNameFilter = new GSFileExtensionFilter();
		filterField = new GSDropdownList<>(fileNameFilter.getOptions());
		filterField.setEmptySelectionAllowed(false);
		filterField.setSelectedIndex(fileNameFilter.getDefaultOption());
		
		fileTable = new GSTablePanel();
		fileTable.setColumnSelectionPolicy(GSEHeaderSelectionPolicy.DISABLED);
		fileTable.setRowSelectionPolicy(GSEHeaderSelectionPolicy.SINGLE_SELECTION);
		fileTable.setCellRenderer(GSFileName.class, GSFileNameCellRenderer.INSTANCE);
		fileTable.setMinimumRowHeight(ROW_HEIGHT);
		fileTable.setColumnHeaderTextAlignment(GSETextAlignment.LEFT);
		fileTable.setBorderWidth(0, 1);
		fileTable.getRowSelectionModel().addListener(this);
		
		confirmButton = new GSButton(CONFIRM_TEXTS[mode.getIndex()]);
		cancelButton = new GSButton(CANCEL_TEXT);
		
		actionListeners = new ArrayList<>();
		
		reloadDirectory();
		
		initLayout();
		initEventListeners();
	}
	
	private void initLayout() {
		getLayout()
			.set(GSGridLayoutManager.MARGIN, OUTER_MARGIN);
		
		setLayoutManager(new GSGridLayoutManager());
		
		GSTextLabel pathLabel = new GSTextLabel(CURRENT_PATH_TEXT);
		pathLabel.getLayout()
			.set(GSGridLayoutManager.GRID_X, 0)
			.set(GSGridLayoutManager.GRID_Y, 0)
			.set(GSGridLayoutManager.ANCHOR, GSEAnchor.EAST)
			.set(GSGridLayoutManager.MARGIN, FIELD_MARGIN);
		add(pathLabel);
		
		pathField.getLayout()
			.set(GSGridLayoutManager.GRID_X, 1)
			.set(GSGridLayoutManager.GRID_Y, 0)
			.set(GSGridLayoutManager.GRID_WIDTH, 3)
			.set(GSGridLayoutManager.FILL, GSEFill.HORIZONTAL)
			.set(GSGridLayoutManager.MARGIN, FIELD_MARGIN)
			/* width based solely on table width */
			.set(GSGridLayoutManager.PREFERRED_WIDTH, 0);
		add(pathField);
		
		GSScrollPanel scrollPanel = new GSScrollPanel(fileTable);
		scrollPanel.getLayout()
			.set(GSGridLayoutManager.GRID_X, 0)
			.set(GSGridLayoutManager.GRID_Y, 1)
			.set(GSGridLayoutManager.GRID_WIDTH, 4)
			.set(GSGridLayoutManager.WEIGHT_X, 1.0f)
			.set(GSGridLayoutManager.WEIGHT_Y, 1.0f)
			.set(GSGridLayoutManager.FILL, GSEFill.BOTH)
			.set(GSGridLayoutManager.MARGIN, FIELD_MARGIN);
		add(scrollPanel);
	
		GSTextLabel nameLabel = new GSTextLabel(FILE_NAME_TEXT);
		nameLabel.getLayout()
			.set(GSGridLayoutManager.GRID_X, 0)
			.set(GSGridLayoutManager.GRID_Y, 2)
			.set(GSGridLayoutManager.ANCHOR, GSEAnchor.EAST)
			.set(GSGridLayoutManager.MARGIN, FIELD_MARGIN);
		add(nameLabel);

		nameField.getLayout()
			.set(GSGridLayoutManager.GRID_X, 1)
			.set(GSGridLayoutManager.GRID_Y, 2)
			.set(GSGridLayoutManager.WEIGHT_X, 1.0f)
			.set(GSGridLayoutManager.FILL, GSEFill.HORIZONTAL)
			.set(GSGridLayoutManager.MARGIN, FIELD_MARGIN)
			/* width based solely on table width */
			.set(GSGridLayoutManager.PREFERRED_WIDTH, 0);
		add(nameField);

		filterField.getLayout()
			.set(GSGridLayoutManager.GRID_X, 2)
			.set(GSGridLayoutManager.GRID_Y, 2)
			.set(GSGridLayoutManager.GRID_WIDTH, 2)
			.set(GSGridLayoutManager.FILL, GSEFill.HORIZONTAL)
			.set(GSGridLayoutManager.MARGIN, FIELD_MARGIN);
		add(filterField);
		
		confirmButton.getLayout()
			.set(GSGridLayoutManager.GRID_X, 2)
			.set(GSGridLayoutManager.GRID_Y, 3)
			.set(GSGridLayoutManager.FILL, GSEFill.HORIZONTAL)
			.set(GSGridLayoutManager.MARGIN, FIELD_MARGIN);
		add(confirmButton);

		cancelButton.getLayout()
			.set(GSGridLayoutManager.GRID_X, 3)
			.set(GSGridLayoutManager.GRID_Y, 3)
			.set(GSGridLayoutManager.FILL, GSEFill.HORIZONTAL)
			.set(GSGridLayoutManager.MARGIN, FIELD_MARGIN);
		add(cancelButton);
	}
	
	private void initEventListeners() {
		cancelButton.addActionListener(this::cancel);
		confirmButton.addActionListener(this::confirm);
		pathField.addActionListener(() -> {
			setDirectory(pathField.getText());
		});
		filterField.addChangeListener(this::reloadDirectory);
		fileTable.addActionListener(() -> {
			if (selectedPath == null || GSPathUtil.isDirectory(selectedPath)) {
				setDirectory(selectedPath);
			} else {
				confirm(selectedPath);
			}
		});
		fileTable.putButtonStroke(BACKWARD_STROKE, () -> {
			if (directory != null)
				setDirectory(directory.getParent());
		});
		fileTable.putButtonStroke(FORWARD_STROKE, () -> {
			setDirectory(selectedPath);
		});
		// Default focus to the file table.
		addFocusEventListener(new GSIFocusEventListener() {
			@Override
			public void focusGained(GSFocusEvent event) {
				fileTable.requestFocus();
			}
		});
	}
	
	private void cancel() {
		finish(null, true);
	}

	private void confirm() {
		confirm(resolvePath());
	}
	
	private void confirm(Path path) {
		if (path != null) {
			if (matchesSelectionMode(path)) {
				finish(fileNameFilter.resolve(path,
						filterField.getSelectedIndex()), false);
			} else if (GSPathUtil.isDirectory(path)) {
				// Instead of confirming and closing, we open the
				// selected directory.
				setDirectory(path);
			}
		}
	}
	
	private void finish(Path path, boolean canceled) {
		this.selectedPath = path;
		this.canceled = canceled;
		close();
		dispatchActionPerformedEvent();
	}
	
	private Path resolvePath() {
		String str = nameField.getText();
		if (str.isEmpty()) {
			// Nothing to resolve, fallback to selected path.
			return selectedPath;
		}
		try {
			Path path = GSPathUtil.getPath(str);
			if (directory != null) {
				// There are several cases. Either path is absolute
				// in which case resolve will result path. Otherwise,
				// the method will attempt to join the two paths.
				return directory.resolve(path);
			}
			// Here, the path must always be absolute, since we are
			// listing the root directories.
			return path.isAbsolute() ? path : null;
		} catch (InvalidPathException ignore) {
		}
		// Invalid name
		return null;
	}
	
	private void close() {
		GSPopup popup = GSPanelUtil.getPopup(this);
		if (popup != null)
			popup.hide();
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
	
	public GSEFileDialogMode getMode() {
		return mode;
	}
	
	public void setMode(GSEFileDialogMode mode) {
		if (mode == null)
			throw new IllegalArgumentException("mode is null!");
		if (mode != this.mode) {
			this.mode = mode;
			onModeChanged();
		}
	}
	
	private void onModeChanged() {
		confirmButton.setText(CONFIRM_TEXTS[mode.getIndex()]);
	}
	
	public GSEFileDialogSelectionMode getSelectionMode() {
		return selectionMode;
	}
	
	public void setSelectionMode(GSEFileDialogSelectionMode selectionMode) {
		if (selectionMode == null)
			throw new IllegalArgumentException("selectionMode is null!");
		if (selectionMode != this.selectionMode) {
			this.selectionMode = selectionMode;
			onSelectionModeChanged();
		}
	}

	private void onSelectionModeChanged() {
		reloadDirectory();
	}

	public void setFileNameFilter(GSIFileNameFilter filter) {
		if (filter == null)
			throw new IllegalArgumentException("filter is null");
		this.fileNameFilter = filter;
		// Update the options shown in the dropdown
		int prevSelection = filterField.getSelectedIndex();
		filterField.setModel(new GSBasicDropdownListModel<>(filter.getOptions()));
		filterField.setSelectedIndex(fileNameFilter.getDefaultOption());
		if (prevSelection != filterField.getSelectedIndex()) {
			// Selection did not change (and thus, the listener
			// did not reload the directory).
			reloadDirectory();
		}
	}
	
	public void setDirectory(String str) {
		if (str == null || str.isEmpty()) {
			// Go to root directory.
			setDirectory((Path)null);
			return;
		}
		Path path = null;
		try {
			path = GSPathUtil.getPath(str);
		} catch (InvalidPathException ignore) {
		}
		// Ensure that the path exists
		if (path != null && GSPathUtil.isDirectory(path)) {
			setDirectory(path);
		} else {
			// Do not change directory, but update path.
			reloadDirectory();
		}
	}
	
	public void setDirectory(Path directory) {
		if (directory != null && !GSPathUtil.isDirectory(directory)) {
			// Only support directories (or null for roots)
			return;
		}
		if (!Objects.equal(directory, this.directory)) {
			Path prevDirectory = this.directory;
			this.directory = directory;
			reloadDirectory();
			// Update selection such that the user is
			// not confused going between directories.
			if (prevDirectory != null)
				attemptToSelect(prevDirectory);
		}
	}
	
	private void reloadDirectory() {
		rootDirectories = (directory == null);
		// Retrieve appropriate iterator over files
		Iterator<Path> itr;
		if (rootDirectories) {
			FileSystem fileSystem = FileSystems.getDefault();
			itr = fileSystem.getRootDirectories().iterator();
		} else {
			try {
				itr = Files.list(directory).iterator();
			} catch (IOException | SecurityException ignore) {
				itr = Collections.emptyIterator();
			}
		}
		paths = collectAndSortFiles(itr);
		
		fileTable.setModel(createTableModel());
		if (directory == null) {
			pathField.setText("");
		} else {
			pathField.setText(directory.toAbsolutePath().toString());
		}
		GSPanelUtil.setScroll(fileTable, 0, 0);
	}
	
	private List<Path> collectAndSortFiles(Iterator<Path> itr) {
		List<Path> result = new ArrayList<>();
		// Collect non-hidden files from iterator
		while (itr.hasNext()) {
			Path path = itr.next();
			if (filterPath(path))
				result.add(path);
		}
		// Sort such that directories are first, and
		// then by alphabetical order of names.
		Collections.sort(result, (p1, p2) -> {
			boolean d1 = GSPathUtil.isDirectory(p1);
			boolean d2 = GSPathUtil.isDirectory(p2);
			if (d1 != d2) {
				// Directories first
				return d1 ? -1 : 1;
			}
			String n1 = GSPathUtil.getName(p1);
			String n2 = GSPathUtil.getName(p2);
			// Otherwise sort names alphabetically
			return n1.compareTo(n2);
		});
		return result;
	}

	private boolean filterPath(Path path) {
		if (GSPathUtil.isHidden(path)) {
			// File is hidden by the system.
			return false;
		}
		if (selectionMode == GSEFileDialogSelectionMode.DIRECTORIES_ONLY &&
				!GSPathUtil.isDirectory(path)) {
			// Does not match selection mode
			return false;
		}
		// Remaining files are filtered by fileName filter
		return fileNameFilter.filter(path, filterField.getSelectedIndex());
	}
	
	private void attemptToSelect(Path path) {
		// Attempt to select directory if it is related to
		// the current directory.
		if (directory == null || path.startsWith(directory)) {
			for (int i = 0; i < paths.size(); i++) {
				if (path.startsWith(paths.get(i))) {
					// Update selection to common path
					selectPathIndex(i);
					break;
				}
			}
		}
	}
	
	private void selectPathIndex(int index) {
		int r;
		if (paths.isEmpty()) {
			r = GSIHeaderSelectionModel.INVALID_SELECTION;
		} else {
			r = rootDirectories ? index : (index + 1);
		}
		fileTable.setSelectedRow(r);
		fileTable.scrollToSelectionLead();
	}
	
	public Path getSelectedPath() {
		return selectedPath;
	}
	
	public boolean isCanceled() {
		return canceled;
	}
	
	private boolean matchesSelectionMode(Path path) {
		if (mode == GSEFileDialogMode.SAVE && !GSPathUtil.exists(path)) {
			// Special case for always matching.
			return true;
		}
		// Otherwise the file has to exist and have
		// the type specified by selection mode.
		switch (selectionMode) {
		case FILES_ONLY:
			return GSPathUtil.isRegularFile(path);
		case DIRECTORIES_ONLY:
			return GSPathUtil.isDirectory(path);
		case FILES_AND_DIRECTORIES:
			return true;
		}
		throw new IllegalStateException("Unknown selection mode");
	}
	
	private GSITableModel createTableModel() {
		int rowCount = rootDirectories ? paths.size() : (paths.size() + 1);
		GSITableModel model = new GSBasicTableModel(TABLE_TITLES.length, rowCount);
		for (int c = 0; c < TABLE_TITLES.length; c++) {
			GSITableColumn column = model.getColumn(c);
			column.setHeaderValue(TABLE_TITLES[c]);
			column.setMinimumWidth(TABLE_WIDTHS[c]);
			// Only the size column is right-aligned
			column.setTextAlignment(c == SIZE_COLUMN_INDEX ?
					GSETextAlignment.RIGHT : GSETextAlignment.LEFT);
		}
		
		int r = 0;
		if (!rootDirectories)
			model.setCellValue(NAME_COLUMN_INDEX, r++, GO_TO_PARENT_NAME);
		for (Path path : paths) {
			model.setCellValue(NAME_COLUMN_INDEX, r, new GSFileName(path, rootDirectories));
			BasicFileAttributes attribs = null;
			try {
				attribs = Files.readAttributes(path, BasicFileAttributes.class);
			} catch (IOException | SecurityException ignore) {
			}
			if (attribs != null) {
				model.setCellValue(MODIFIED_COLUMN_INDEX, r, attribs.lastModifiedTime().toInstant());
				model.setCellValue(TYPE_COLUMN_INDEX, r, getFileTypeAsText(path, attribs));
				model.setCellValue(SIZE_COLUMN_INDEX, r, getFileSizeAsText(path, attribs));
			} else {
				model.setCellValue(TYPE_COLUMN_INDEX, r, UNKNOWN_FILE_TYPE);
			}
			r++;
		}
		return model;
	}
	
	private static Text getFileTypeAsText(Path path, BasicFileAttributes attribs) {
		if (attribs.isDirectory()) {
			return DIRECTORY_TYPE;
		} else if (attribs.isRegularFile()) {
			String fileExt = GSPathUtil.getFileExtension(path);
			if (fileExt != null)
				return translatable("regularFile", fileExt.toUpperCase());
		}
		return UNKNOWN_FILE_TYPE;
	}
	
	private static Text getFileSizeAsText(Path path, BasicFileAttributes attribs) {
		if (!attribs.isRegularFile()) {
			// Only supported for regular files
			return null;
		}
		return GSPathUtil.getSizeAsText(attribs.size());
	}
	
	public static Path getDefaultDirectory() {
		Path home = GSPathUtil.getHome();
		if (home != null) {
			// Note: preferably we would retrieve the desktop of
			//       the user, but this might not exist in the home
			//       directory in case it was moved.
			return home;
		}
		// Fallback to the game directory (%appdata%/.minecraft on windows)
		MinecraftClient client = MinecraftClient.getInstance();
		return (client != null) ? client.runDirectory.toPath() : null;
	}
	
	@Override
	public void selectionChanged(int firstIndex, int lastIndex) {
		int selectedRow = fileTable.getRowSelectionModel().getIntervalMin();
		if (selectedRow != GSIHeaderSelectionModel.INVALID_SELECTION) {
			// Set filename field accordingly
			int pathIndex = rootDirectories ? selectedRow : (selectedRow - 1);
			if (pathIndex >= 0 && pathIndex < paths.size()) {
				selectedPath = paths.get(pathIndex);
				nameField.setText(GSPathUtil.getName(selectedPath));
			} else {
				selectedPath = (directory != null) ? directory.getParent() : null;
				nameField.setText("");
			}
		} else {
			selectedPath = null;
			nameField.setText("");
		}
		onSelectedPathChanged();
	}
	
	private void onSelectedPathChanged() {
		if (selectedPath != null && !matchesSelectionMode(selectedPath)
				&& GSPathUtil.isDirectory(selectedPath)) {
			// Update the confirm button text to reflect opening
			// the selected path as the next directory.
			confirmButton.setText(CONFIRM_TEXTS[GSEFileDialogMode.OPEN.getIndex()]);
		} else {
			// Update the text back to the file dialog mode.
			onModeChanged();
		}
	}
	
	private static void initDefaultFileIcons() {
		// Capture & Playback asset files
		addFileIcon("gsa", GSPanelContext.getIcon(12, 84, 12, 12));
		// Text and document files
		addFileIcons(new String[] {
			"txt", "doc", "docx", "xls",
			"xlsx", "odt", "ods"
		}, GSPanelContext.getIcon(24, 84, 12, 12));
		// Compressed file archives
		addFileIcons(new String[] {
			"zip", "7z", "rar", "tar",
			"gz"
		}, GSPanelContext.getIcon(36, 84, 12, 12));
		// Binary files
		addFileIcons(new String[] {
			"bin", "iso", "dat"
		}, GSPanelContext.getIcon(48, 84, 12, 12));
		// Image files
		addFileIcons(new String[] {
			"png", "jpg", "jpeg", "bmp",
			"gif", "tif", "tiff"
		}, GSPanelContext.getIcon(60, 84, 12, 12));
		// PDF files
		addFileIcon("pdf", GSPanelContext.getIcon(72, 84, 12, 12));
		// Configuration files
		addFileIcons(new String[] {
			"cfg", "conf", "config", "ini"
		}, GSPanelContext.getIcon(0, 96, 12, 12));
		// Arduino project icon
		addFileIcon("ino", GSPanelContext.getIcon(12, 96, 12, 12));
		// HTML files
		addFileIcons(new String[] {
			"html", "htm", "xhtml", "xhtm",
			"shtml", "shtm", "dhtml", "dhtm"
		}, GSPanelContext.getIcon(24, 96, 12, 12));
		// JSON files
		addFileIcon("json", GSPanelContext.getIcon(36, 96, 12, 12));
		// Video files
		addFileIcons(new String[] {
			"mp4", "mov", "wmv", "avi",
			"mkv", "flv", "f4v", "webm",
			"vob", "ogv"
		}, GSPanelContext.getIcon(48, 96, 12, 12));
	}

	public static boolean addFileIcons(String[] fileExts, GSIcon icon) {
		if (fileExts == null)
			throw new IllegalArgumentException("fileExts is null");
		boolean success = true;
		for (String fileExt : fileExts) {
			if (!addFileIcon(fileExt, icon))
				success = false;
		}
		return success;
	}

	public static boolean addFileIcon(String fileExt, GSIcon icon) {
		if (fileExt == null || fileExt.isEmpty())
			throw new IllegalArgumentException("fileExt is null or empty");
		if (icon == null)
			throw new IllegalArgumentException("icon is null");
		// File extensions are lower-case
		fileExt = fileExt.toLowerCase();
		// Ensure that file does not already have an icon.
		if (!extensionToFileIcon.containsKey(fileExt)) {
			extensionToFileIcon.put(fileExt, icon);
			return true;
		}
		return false;
	}
	
	public static GSIcon getFileIcon(String fileExt) {
		// Note: intentional null-pointer exception
		GSIcon icon = extensionToFileIcon.get(fileExt.toLowerCase());
		return (icon != null) ? icon : UNKNOWN_FILE_ICON;
	}

	/**
	 * Shows an open file dialog, starting in the home directory of the user.
	 * See {@link #showDialog(GSPanel, Path, GSEFileDialogMode)} for more
	 * info on how to retrieve the result from the dialog.
	 * 
	 * @param source - the panel responsible for this dialog
	 * 
	 * @return The instance of the file dialog that is shown.
	 * 
	 * @see #showDialog(GSPanel, Path, GSEFileDialogMode)
	 */
	public static GSFileDialog showOpenDialog(GSPanel source) {
		return showOpenDialog(source, getDefaultDirectory());
	}
	
	/**
	 * Shows an open file dialog, starting in the given directory. See
	 * {@link #showDialog(GSPanel, Path, GSEFileDialogMode)} for more info
	 * on how to retrieve the result from the dialog.
	 * 
	 * @param source - the panel responsible for this dialog
	 * @param directory - the starting directory of the dialog
	 * 
	 * @return The instance of the file dialog that is shown.
	 * 
	 * @see #showDialog(GSPanel, Path, GSEFileDialogMode)
	 */
	public static GSFileDialog showOpenDialog(GSPanel source, Path directory) {
		return showDialog(source, directory, GSEFileDialogMode.OPEN);
	}

	/**
	 * Shows a save file dialog, starting in the home directory of the user.
	 * See {@link #showDialog(GSPanel, Path, GSEFileDialogMode)} for more
	 * info on how to retrieve the result from the dialog.
	 * 
	 * @param source - the panel responsible for this dialog
	 * 
	 * @return The instance of the file dialog that is shown.
	 * 
	 * @see #showDialog(GSPanel, Path, GSEFileDialogMode)
	 */
	public static GSFileDialog showSaveDialog(GSPanel source) {
		return showSaveDialog(source, getDefaultDirectory());
	}

	/**
	 * Shows a save file dialog, starting in the given directory. See
	 * {@link #showDialog(GSPanel, Path, GSEFileDialogMode)} for more info.
	 * 
	 * @param source - the panel responsible for this dialog
	 * @param directory - the starting directory of the dialog
	 * 
	 * @return The instance of the file dialog that is shown.
	 * 
	 * @see #showDialog(GSPanel, Path, GSEFileDialogMode)
	 */
	public static GSFileDialog showSaveDialog(GSPanel source, Path directory) {
		return showDialog(source, directory, GSEFileDialogMode.SAVE);
	}
	
	/**
	 * Shows a file dialog with the given mode, where the user can choose a
	 * file, starting in the given directory. The instance that is returned
	 * can be used to further customize the filters applied to the dialog.
	 * In order to obtain the result of the dialog, the following code can
	 * be used:
	 * <pre>
	 * GSFileDialog dialog = GSFileDialog.showDialog(...);
	 * dialog.addActionListener(() -> {
	 *     if (dialog.isCanceled()) {
	 *         // user pressed cancel
	 *     } else {
	 *         Path path = dialog.getSelectedPath();
	 *         // user chose path
	 *     }
	 * );
	 * </pre>
	 * The methods {@link #setSelectionMode(GSEFileDialogSelectionMode)},
	 * and {@link #setFileNameFilter(GSIFileNameFilter)} are useful for
	 * specifying which types of files the user can select. Note that the
	 * file name filter might modify the name of the selected path upon
	 * confirmation depending on implementation.
	 * 
	 * @param source - the panel responsible for this dialog
	 * @param directory - the starting directory of the dialog
	 * @param mode - the dialog mode
	 * 
	 * @return The instance of the file dialog that is shown.
	 */
	public static GSFileDialog showDialog(GSPanel source, Path directory, GSEFileDialogMode mode) {
		GSFileDialog dialog = new GSFileDialog(directory);
		dialog.setMode(mode);
		GSPopup popup = new GSPopup(dialog, true);
		popup.show(source, 0, 0, GSEPopupPlacement.CENTER);
		return dialog;
	}
	
	private static class GSFileName {
		
		private final Text name;
		private final GSIcon icon;
		
		public GSFileName(Path path, boolean isRootDirectory) {
			String name = GSPathUtil.getName(path);
			// Retrieve the icon based on file type
			GSIcon icon = null;
			if (isRootDirectory) {
				icon = ROOT_DIRECTORY_ICON;
			} else if (GSPathUtil.isDirectory(path)) {
				icon = DIRECTORY_ICON;
			} else if (GSPathUtil.isRegularFile(path)) {
				// Icon depends on extension.
				String fileExt = GSPathUtil.getFileExtension(name);
				if (fileExt != null)
					icon = getFileIcon(fileExt);
			}
			this.name = Text.literal(name);
			this.icon = (icon == null) ? UNKNOWN_FILE_ICON : icon;
		}

		public GSFileName(String name, GSIcon icon) {
			this(Text.literal(name), icon);
		}
		
		public GSFileName(Text name, GSIcon icon) {
			this.name = name;
			this.icon = icon;
		}
	}
	
	private static class GSFileNameCellRenderer implements GSICellRenderer<GSFileName> {

		public static final GSFileNameCellRenderer INSTANCE = new GSFileNameCellRenderer();

		private GSFileNameCellRenderer() {
		}
		
		@Override
		public void render(GSIRenderer2D renderer, GSFileName value, GSCellContext context) {
			GSPanelUtil.drawLabel(renderer, value.icon, FILE_ICON_SPACING, value.name, context.textColor,
					false, GSEIconAlignment.LEFT, context.textAlignment, context.bounds);
		}

		@Override
		public GSDimension getMinimumSize(GSFileName value) {
			return GSPanelUtil.labelPreferredSize(value.icon, value.name, FILE_ICON_SPACING);
		}
	}
}
