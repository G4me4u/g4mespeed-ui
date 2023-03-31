package com.g4mesoft.ui.panel;

import java.io.File;
import java.io.IOException;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.InvalidPathException;
import java.nio.file.Path;
import java.nio.file.attribute.BasicFileAttributes;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import com.g4mesoft.ui.panel.button.GSButton;
import com.g4mesoft.ui.panel.cell.GSCellContext;
import com.g4mesoft.ui.panel.cell.GSICellRenderer;
import com.g4mesoft.ui.panel.dropdown.GSDropdownList;
import com.g4mesoft.ui.panel.event.GSFocusEvent;
import com.g4mesoft.ui.panel.event.GSIButtonStroke;
import com.g4mesoft.ui.panel.event.GSIFocusEventListener;
import com.g4mesoft.ui.panel.event.GSKeyButtonStroke;
import com.g4mesoft.ui.panel.event.GSKeyEvent;
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
import com.google.common.base.Objects;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterators;

import net.minecraft.client.MinecraftClient;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;

public class GSFileDialog extends GSParentPanel implements GSIHeaderSelectionListener {

	/* Titles for the elements shown in the file table */
	private static final Text NAME_TITLE     = translatable("name");
	private static final Text MODIFIED_TITLE = translatable("modified");
	private static final Text TYPE_TITLE     = translatable("type");
	private static final Text SIZE_TITLE     = translatable("size");
	/* Minimum sizes of each of the columns in the file table */
	private static final int NAME_WIDTH     = 150;
	private static final int MODIFIED_WIDTH = 120;
	private static final int TYPE_WIDTH     = 80;
	private static final int SIZE_WIDTH     = 50;
	/* Height of the rows in the file table */
	private static final int ROW_HEIGHT = 16;
	/* Indices pointing to the column of each of the titles */
	private static final int NAME_COLUMN_INDEX;
	private static final int MODIFIED_COLUMN_INDEX;
	private static final int TYPE_COLUMN_INDEX;
	private static final int SIZE_COLUMN_INDEX;
	private static final Text[] TABLE_TITLES;
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

	/* Used for file size to display text conversion */
	private static final String[] FILE_SIZE_TRANSLATION_KEYS = {
		"panel.fileDialog.size.bytes", // 1 B
		"panel.fileDialog.size.kib",   // 1024^1 B
		"panel.fileDialog.size.mib",   // 1024^2 B
		"panel.fileDialog.size.gib",   // 1024^3 B
		"panel.fileDialog.size.tib",   // 1024^4 B
		"panel.fileDialog.size.pib",   // 1024^5 B
		"panel.fileDialog.size.eib",   // 1024^6 B
		"panel.fileDialog.size.zib",   // 1024^7 B
		"panel.fileDialog.size.yib"    // 1024^8 B
	};
	private static final DecimalFormat FILE_SIZE_DECIMAL_FORMAT =
			new DecimalFormat("0.##", new DecimalFormatSymbols(Locale.US));
	private static final int ONE_KIBIBYTE_LOG2 = 10; // 2^10 = 1024
	
	private static final GSIButtonStroke GO_TO_PARENT_STROKE =
			new GSKeyButtonStroke(GSKeyEvent.KEY_BACKSPACE);
	
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
		TABLE_TITLES[NAME_COLUMN_INDEX]     = NAME_TITLE;
		TABLE_TITLES[MODIFIED_COLUMN_INDEX] = MODIFIED_TITLE;
		TABLE_TITLES[TYPE_COLUMN_INDEX]     = TYPE_TITLE;
		TABLE_TITLES[SIZE_COLUMN_INDEX]     = SIZE_TITLE;
		// Compute array with table widths
		TABLE_WIDTHS = new int[titleCount];
		TABLE_WIDTHS[NAME_COLUMN_INDEX]     = NAME_WIDTH;
		TABLE_WIDTHS[MODIFIED_COLUMN_INDEX] = MODIFIED_WIDTH;
		TABLE_WIDTHS[TYPE_COLUMN_INDEX]     = TYPE_WIDTH;
		TABLE_WIDTHS[SIZE_COLUMN_INDEX]     = SIZE_WIDTH;
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
		return new TranslatableText("panel.fileDialog." + key, args);
	}
	
	private Path directory;

	private GSEFileDialogMode mode;
	private GSEFileDialogSelectionMode selectionMode;
	
	private final GSTextField pathField;
	private final GSTextField nameField;
	private final GSDropdownList<Text> filterField;
	
	private final GSTablePanel fileTable;
	
	private final GSButton confirmButton;
	private final GSButton cancelButton;
	
	private boolean rootDirectories;
	private List<Path> paths;
	
	private Path selectedPath;
	
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
		filterField = new GSDropdownList<>();
		
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
		
		onDirectoryChanged();
		
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
		fileTable.addActionListener(() -> {
			setDirectory(selectedPath);
		});
		fileTable.putButtonStroke(GO_TO_PARENT_STROKE, () -> {
			if (directory != null)
				setDirectory(directory.getParent());
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
		close();
		dispatchActionPerformedEvent();
	}

	private void confirm() {
		// TODO: fix unchecked invocations of Files.isDirectory(...) etc.
		
		Path path = resolvePath();
		if (path != null) {
			if (matchesSelectionMode(path)) {
				close();
				dispatchActionPerformedEvent();
			} else if (Files.isDirectory(path)) {
				// Instead of confirming and closing, we open the
				// selected directory.
				setDirectory(path);
			}
		}
	}
	
	private Path resolvePath() {
		String str = nameField.getText();
		if (str.isEmpty()) {
			// Nothing to resolve, fallback to selected path.
			return selectedPath;
		}
		try {
			Path path = Path.of(str);
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

	public void setSelectionMode(GSEFileDialogSelectionMode selectionMode) {
		if (selectionMode == null)
			throw new IllegalArgumentException("selectionMode is null!");
		if (selectionMode != this.selectionMode) {
			this.selectionMode = selectionMode;
			onSelectionModeChanged();
		}
	}

	private void onSelectionModeChanged() {
		onDirectoryChanged();
	}
	
	public void setDirectory(String str) {
		if (str == null || str.isEmpty()) {
			// Go to root directory.
			setDirectory((Path)null);
			return;
		}
		Path path = null;
		try {
			path = Path.of(str);
		} catch (InvalidPathException ignore) {
		}
		// Ensure that the path exists
		if (path != null && Files.isDirectory(path)) {
			setDirectory(path);
		} else {
			// Do not change directory, but update path.
			onDirectoryChanged();
		}
	}
	
	public void setDirectory(Path directory) {
		if (directory != null && !Files.isDirectory(directory)) {
			// Only support directories (or null for roots)
			return;
		}
		if (!Objects.equal(directory, this.directory)) {
			this.directory = directory;
			onDirectoryChanged();
		}
	}
	
	private void onDirectoryChanged() {
		rootDirectories = (directory == null);
		// Retrieve appropriate iterator over files
		Iterator<Path> itr;
		if (rootDirectories) {
			FileSystem fileSystem = FileSystems.getDefault();
			itr = fileSystem.getRootDirectories().iterator();
		} else {
			try {
				itr = Files.list(directory).iterator();
			} catch (IOException ignore) {
				itr = Collections.emptyIterator();
			}
		}
		// Collect non-hidden files from iterator
		paths = ImmutableList.copyOf(Iterators.filter(itr, this::filterPath));
		
		fileTable.setModel(createTableModel());
		if (directory == null) {
			pathField.setText("");
		} else {
			pathField.setText(directory.toAbsolutePath().toString());
		}
		// Update selection to the first file in directory
		int selection = (rootDirectories || paths.isEmpty()) ? 0 : 1;
		fileTable.setSelectedRows(selection, selection);
		// Reset the table scroll
		GSPanelUtil.setScroll(fileTable, 0, 0);
	}

	private boolean filterPath(Path path) {
		if (isHidden(path)) {
			// File is hidden by the system.
			return false;
		}
		// Only the directories only selection mode restricts visible files.
		if (selectionMode == GSEFileDialogSelectionMode.DIRECTORIES_ONLY)
			return Files.isDirectory(path);
		return true;
	}
	
	private boolean isHidden(Path path) {
		if (rootDirectories) {
			// java.nio seems to think that the root directories
			// are hidden, even though they are accessible...
			return false;
		}
		try {
			return Files.isHidden(path);
		} catch (Throwable ignore) {
		}
		return true;
	}
	
	private boolean matchesSelectionMode(Path path) {
		switch (selectionMode) {
		case FILES_ONLY:
			return Files.isRegularFile(path);
		case DIRECTORIES_ONLY:
			return Files.isDirectory(path);
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
			} catch (IOException e) {
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
			String extension = getFileExtension(path);
			if (extension != null)
				return translatable("regularFile", extension.toUpperCase());
		}
		return UNKNOWN_FILE_TYPE;
	}
	
	private static Text getFileSizeAsText(Path path, BasicFileAttributes attribs) {
		if (!attribs.isRegularFile()) {
			// Only supported for regular files
			return null;
		}
		long size = attribs.size();
		int i = 0;
		for ( ; i < FILE_SIZE_TRANSLATION_KEYS.length; i++) {
			if ((size >> (ONE_KIBIBYTE_LOG2 * (i + 1))) == 0L) {
				// The current unit is the last to fit size
				break;
			}
		}
		// Convert to two-decimal precision
		double value = (double)size / (1L << (ONE_KIBIBYTE_LOG2 * i));
		return new TranslatableText(FILE_SIZE_TRANSLATION_KEYS[i],
				FILE_SIZE_DECIMAL_FORMAT.format(value));
	}
	
	private static String getFileExtension(Path path) {
		return getFileExtension(getFileName(path));
	}
	
	private static String getFileExtension(String fileName) {
		int extIndex = fileName.lastIndexOf('.');
		if (extIndex != -1 && extIndex != fileName.length() - 1)
			return fileName.substring(extIndex + 1);
		return null;
	}
	
	private static String getFileName(Path path) {
		Path name = path.getFileName();
		return (name != null) ? name.toString() : path.toString();
	}
	
	public static Path getDefaultDirectory() {
		String home = System.getProperty("user.home");
		if (home != null) {
			// Note: preferably we would retrieve the desktop of
			//       the user, but this might not exist in the home
			//       directory in case it was moved.
			try {
				return Path.of(home);
			} catch (Throwable ignore) {
				// Missing permission
			}
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
				nameField.setText(getFileName(selectedPath));
			} else {
				selectedPath = (directory != null) ? directory.getParent() : null;
				nameField.setText("");
			}
			onSelectedPathChanged();
		}
	}
	
	private void onSelectedPathChanged() {
		if (selectedPath != null && !matchesSelectionMode(selectedPath)
				&& Files.isDirectory(selectedPath)) {
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
			"bin", "iso"
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

	public static GSFileDialog showOpenDialog(GSPanel source) {
		return showOpenDialog(source, getDefaultDirectory());
	}
	
	public static GSFileDialog showOpenDialog(GSPanel source, Path directory) {
		return showDialog(source, directory, GSEFileDialogMode.OPEN);
	}

	public static GSFileDialog showSaveDialog(GSPanel source) {
		return showSaveDialog(source, getDefaultDirectory());
	}

	public static GSFileDialog showSaveDialog(GSPanel source, Path directory) {
		return showDialog(source, directory, GSEFileDialogMode.SAVE);
	}
	
	private static GSFileDialog showDialog(GSPanel source, Path directory, GSEFileDialogMode mode) {
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
			String name = getFileName(path);
			// Retrieve the icon based on file type
			GSIcon icon = null;
			if (isRootDirectory) {
				icon = ROOT_DIRECTORY_ICON;
			} else if (Files.isDirectory(path)) {
				icon = DIRECTORY_ICON;
			} else if (Files.isRegularFile(path)) {
				// Icon depends on extension.
				String fileExt = getFileExtension(name);
				if (fileExt != null)
					icon = getFileIcon(fileExt);
			}
			this.name = new LiteralText(name);
			this.icon = (icon == null) ? UNKNOWN_FILE_ICON : icon;
		}

		public GSFileName(String name, GSIcon icon) {
			this(new LiteralText(name), icon);
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
