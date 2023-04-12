package com.g4mesoft.ui.util;

import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.InvalidPathException;
import java.nio.file.Path;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;
import java.util.Objects;

import net.minecraft.text.Text;

public final class GSPathUtil {

	/* Used for file size to display text conversion */
	private static final String[] FILE_SIZE_KEYS = {
		"panel.file.size.bytes", // 1 B
		"panel.file.size.kib",   // 1024^1 B
		"panel.file.size.mib",   // 1024^2 B
		"panel.file.size.gib",   // 1024^3 B
		"panel.file.size.tib",   // 1024^4 B
		"panel.file.size.pib",   // 1024^5 B
		"panel.file.size.eib"    // 1024^6 B
		// larger is > 2^64 B
	};
	private static final DecimalFormat FILE_SIZE_FORMAT =
			new DecimalFormat("0.##", new DecimalFormatSymbols(Locale.US));
	private static final int ONE_KIBIBYTE_LOG2 = 10; // 2^10 = 1024

	private GSPathUtil() {
	}

	/**
	 * Checks whether the given path is considered hidden. This is implementation
	 * specific, and depends on the platform. This method behaves different than
	 * the corresponding {@link Files#isHidden(Path)} method in that, if the path
	 * represents a root directory, then the path is never hidden. Likewise, if
	 * there is insufficient permission, then the path is also considered hidden.
	 * 
	 * @param path - the path to be checked
	 * 
	 * @return True, if the given path is considered hidden, false otherwise.
	 */
	public static boolean isHidden(Path path) {
		if (isRoot(path)) {
			// java.nio seems to think that the root directories
			// are hidden, even though they are accessible...
			return false;
		}
		try {
			return Files.isHidden(path);
		} catch (IOException | SecurityException ignore) {
		}
		// Insufficient permission. Consider as hidden.
		return true;
	}
	
	/**
	 * Checks whether the given path consists only of a root component.
	 * 
	 * @param path - the path to be checked.
	 * 
	 * @return True, if the given path is root, false otherwise.
	 */
	public static boolean isRoot(Path path) {
		return path.getNameCount() == 0;
	}
	
	/**
	 * Checks whether the given path exists. In case there is insufficient
	 * permission, {@code false} will be returned.
	 * 
	 * @param path - the path to be checked
	 * 
	 * @return True, if a file or directory at the given path exists.
	 */
	public static boolean exists(Path path) {
		try {
			return Files.exists(path);
		} catch (SecurityException ignore) {
		}
		return false;
	}
	
	/**
	 * Checks whether the given path is a directory. In case there is
	 * insufficient permission, {@code false} will be returned.
	 * 
	 * @param path - the path to be checked
	 * 
	 * @return True, if the given path is a directory.
	 */
	public static boolean isDirectory(Path path) {
		try {
			return Files.isDirectory(path);
		} catch (SecurityException ignore) {
		}
		return false;
	}

	/**
	 * Checks whether the given path is a file. In case there is insufficient
	 * permission, {@code false} will be returned.
	 * 
	 * @param path - the path to be checked
	 * 
	 * @return True, if the given path is a regular file.
	 */
	public static boolean isRegularFile(Path path) {
		try {
			return Files.isRegularFile(path);
		} catch (SecurityException ignore) {
		}
		return false;
	}
	
	/**
	 * Converts the given size into human-readable text. The returned value
	 * is the representation in the largest unit such that the value is at
	 * least 1.0 unit. The supported units are B, KB, MB, GB, TB, PB, and EB,
	 * which are each powers of 1024. E.g. when given {@code size = 100}, the
	 * text "100 B" is returned. Likewise, when given {@code size = 2000}, the
	 * text "1.95 KB" is returned.
	 * 
	 * @param size - the size which is converted to human readable text
	 * 
	 * @return The human readable representation of the given size.
	 */
	public static Text getSizeAsText(long size) {
		int i = 0;
		for ( ; i < FILE_SIZE_KEYS.length; i++) {
			if ((size >> (ONE_KIBIBYTE_LOG2 * (i + 1))) == 0L) {
				// The current unit is the last to fit size
				break;
			}
		}
		// Convert to two-decimal precision
		double value = (double)size / (1L << (ONE_KIBIBYTE_LOG2 * i));
		return Text.translatable(FILE_SIZE_KEYS[i],
				FILE_SIZE_FORMAT.format(value));
	}
	
	/**
	 * @return The home directory of the user specified by the
	 *         {@code user.home} system property.
	 */
	public static Path getHome() {
		String home = System.getProperty("user.home");
		if (home != null) {
			try {
				return getPath(home);
			} catch (InvalidPathException ignore) {
				// User home is an invalid path
			}
		}
		return null;
	}
	
	/**
	 * Computes the file extension of the file represented by the given path.
	 * Note that this does not check if the path is a regular file, however,
	 * this should be ensures prior to invoking this method. If this is not
	 * ensured, then the returned value of this method is undefined. See
	 * {@link #getFileExtension(String)} for more details.
	 * 
	 * @param path - the path of the file whose extension is returned.
	 * 
	 * @return The file extension of the given path, or null if there is none
	 */
	public static String getFileExtension(Path path) {
		return getFileExtension(getName(path));
	}
	
	/**
	 * Computes the file extension of the given file name, i.e. the substring
	 * following the last '.' (dot) character. If there are no characters
	 * following the last dot character, then the empty string is returned. If
	 * the file name does not contain a dot character, then the result is
	 * {@code null}.
	 * 
	 * @param fileName - the name of the file whose extension is returned.
	 * 
	 * @return The file extension of the given name, or null if there is none
	 */
	public static String getFileExtension(String fileName) {
		int extIndex = fileName.lastIndexOf('.');
		if (extIndex != -1)
			return fileName.substring(extIndex + 1);
		return null;
	}
	
	/**
	 * Computes the path of the given {@code path} with the given new extension.
	 * If the given path already has an extension, its extension is stripped,
	 * and replaced by the given file extension. If the path already has the
	 * given extension, then {@code path} is returned.
	 * <br><br>
	 * After an invocation of this method, the {@link #getFileExtension(Path)}
	 * method will return the updated extension.
	 * 
	 * @param path - the path whose extension should be changed
	 * @param fileExt - the new extension of the given path
	 * 
	 * @return A path with the original parent directory and file name as the
	 *         given {@code path} but with the updated extension.
	 */
	public static Path withFileExtension(Path path, String fileExt) {
		String fileName = getName(path);
		// Extract current extension (similar to above)
		int dotIndex = (fileExt == null) ?
				fileName.indexOf('.') : fileName.lastIndexOf('.');
		String curFileExt = null;
		if (dotIndex != -1)
			curFileExt = fileName.substring(dotIndex + 1);
		if (Objects.equals(fileExt, curFileExt)) {
			// Already matches current extension
			return path;
		}
		// Extend with new extension
		String nFileName;
		if (fileExt == null) {
			//assert(dotIndex != -1)
			// Remove everything including the dot
			nFileName = fileName.substring(0, dotIndex);
		} else if (fileExt.isEmpty()) {
			if (dotIndex != -1) {
				// Remove everything after the dot
				nFileName = fileName.substring(0, dotIndex + 1);
			} else {
				// Append a dot to the name
				nFileName = fileName + ".";
			}
		} else {
			if (dotIndex != -1) {
				// Replace everything after dot with the
				// expected extension.
				nFileName = fileName.substring(0, dotIndex + 1) + fileExt;
			} else {
				// Add the entire extension with dot
				nFileName = fileName + "." + fileExt;
			}
		}
		return path.resolveSibling(nFileName);
	}
	
	/**
	 * Retrieves the name of the given file or directory. This is equivalent
	 * to the relative path from the parent directory.
	 * 
	 * @param path - the path whose last name to retrieve
	 * 
	 * @return The last name of the given path.
	 */
	public static String getName(Path path) {
		// Note: intentional null-pointer exception
		Path name = path.getFileName();
		return (name != null) ? name.toString() : path.toString();
	}
	
	/**
	 * Converts the given string parts into the corresponding path. An
	 * invocation of this method is equivalent to the following snippet:
	 * <pre>
	 *     FileSystems.getDefault().getPath(first, more);
	 * </pre>
	 * 
	 * @param first - the path string or initial part of the path string
	 * @param more - additional strings to be joined to form the path string
	 * 
	 * @return The path representation of the given string parts.
	 * @throws InvalidPathException - if a path could not be constructed from
	 *                                the given strings.
	 * @see java.nio.file.FileSystem#getPath(String, String...)
	 */
	public static Path getPath(String first, String... more) {
		return FileSystems.getDefault().getPath(first, more);
	}
}
