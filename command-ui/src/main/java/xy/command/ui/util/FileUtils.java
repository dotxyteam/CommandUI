package xy.command.ui.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;

public class FileUtils {

	public static String read(File file) throws Exception {
		return new String(readBinary(file));
	}

	public static byte[] readBinary(File file) throws Exception {
		InputStream in = null;
		try {
			in = new FileInputStream(file);
			long length = file.length();
			byte[] bytes = new byte[(int) length];
			int offset = 0;
			int numRead = 0;
			while (offset < bytes.length
					&& (numRead = in.read(bytes, offset, bytes.length - offset)) >= 0) {
				offset += numRead;
			}
			if (offset < bytes.length) {
				throw new IOException("Could not completely read file");
			}
			in.close();
			return bytes;
		} catch (IOException e) {
			throw new Exception("Unable to read file : '"
					+ file.getAbsolutePath() + "': " + e.getMessage(), e);
		} finally {
			if (in != null) {
				try {
					in.close();
				} catch (IOException e) {
				}
			}
		}
	}

	public static void write(File file, String text, boolean append)
			throws Exception {
		writeBinary(file, text.getBytes(), append);
	}

	public static void writeBinary(File file, byte[] bytes, boolean append)
			throws Exception {
		FileOutputStream out = null;
		try {
			out = new FileOutputStream(file);
			out.write(bytes);
			out.flush();
			out.close();
		} catch (IOException e) {
			throw new Exception("Unable to write file : '"
					+ file.getAbsolutePath() + "': " + e.getMessage(), e);
		} finally {
			if (out != null) {
				try {
					out.close();
				} catch (IOException e) {
				}
			}
		}

	}

	
	public static String removeFileNameExtension(String fileName) {
		String extension = getFileNameExtension(fileName);
		if (extension.length() > 0) {
			return fileName.substring(0,
					fileName.length() - ("." + extension).length());
		} else {
			return fileName;
		}
	}

	public static String getFileNameExtension(String fileName) {
		int lastDotIndex = fileName.lastIndexOf(".");
		if (lastDotIndex == -1) {
			return "";
		} else {
			return fileName.substring(lastDotIndex + 1);
		}
	}

	public static void copy(File src, File dst) throws Exception {
		copy(src, dst, true);
	}

	public static void copy(File src, File dst, boolean recusrsively)
			throws Exception {
		copy(src, dst, recusrsively, null);
	}

	public static void copy(File src, File dst, boolean recusrsively,
			FilenameFilter filenameFilter) throws Exception {
		try {
			if (src.isDirectory()) {
				mkDir(dst);
				if (recusrsively) {
					for (File srcChild : src.listFiles(filenameFilter)) {
						copy(srcChild, new File(dst, srcChild.getName()),
								recusrsively, filenameFilter);
					}
				}
			} else if (src.isFile()) {
				writeBinary(dst, readBinary(src), false);
			} else {
				throw new Exception("File not found: '" + src + "'", null);
			}
		} catch (Exception e) {
			throw new Exception("Unable to copy resource: '"
					+ src.getAbsolutePath() + "' > '" + dst.getAbsolutePath()
					+ "': " + e.getMessage(), e);
		}
	}

	public static void mkDir(File dir) throws Exception {
		if (dir.isDirectory()) {
			return;
		}
		final boolean success;
		try {
			success = dir.mkdir();
		} catch (Exception e) {
			throw new Exception("Failed to create directory: '"
					+ dir.getAbsolutePath() + "': " + e.getMessage(), e);
		}
		if (!success) {
			throw new Exception("Unable to create directory: '"
					+ dir.getAbsolutePath() + "'", null);
		}
	}

	public static String getRelativePath(File child, File ancestor) {
		if (!FileUtils.isAncestor(ancestor, child)) {
			return null;
		}
		try {
			return child.getCanonicalPath().substring(
					ancestor.getCanonicalPath().length() + 1);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	public static boolean canonicallyEquals(File file1, File file2) {
		try {
			return file1.getCanonicalFile().equals(file2.getCanonicalFile());
		} catch (IOException e) {
			throw new RuntimeException(e.toString(), e);
		}
	}

	public static File getCanonicalParent(File file) {
		try {
			return file.getCanonicalFile().getParentFile();
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	public static void delete(File file) throws Exception {
		delete(file, null);
	}

	public static void delete(File file, final FilenameFilter filter)
			throws Exception {
		if (file.isDirectory()) {
			CountingFilenameFilter countingFilter = (filter != null) ? new CountingFilenameFilter(
					filter) : null;
			for (File childFile : file.listFiles(countingFilter)) {
				delete(childFile, countingFilter);
			}
			if ((countingFilter != null)
					&& countingFilter.getFilteredCount() > 0) {
				return;
			}
		}
		boolean success;
		try {
			success = file.delete();
		} catch (Exception e) {
			throw new Exception("Failed to delete resource: '"
					+ file.getAbsolutePath() + "'" + e.getMessage(), e);
		}
		if (!success) {
			throw new Exception("Unable to delete resource: '"
					+ file.getAbsolutePath() + "'", null);
		}
	}

	public static void rename(File file, String destFileName) throws Exception {
		try {
			if (new File(destFileName).getParent() != null) {
				throw new Exception(
						"Destination file name is not is not a local name: '"
								+ destFileName + "'");
			}
			File destFile = new File(file.getParent(), destFileName);
			boolean success = file.renameTo(destFile);
			if (!success) {
				throw new Exception("System error");
			}
		} catch (Exception e) {
			throw new Exception("Failed to rename resource: '"
					+ file.getAbsolutePath() + "' to '" + destFileName + "': "
					+ e.getMessage(), e);
		}
	}

	public static boolean hasFileNameExtension(String fileName,
			String[] extensions) {
		for (String ext : extensions) {
			if (ext.toLowerCase().equals(
					getFileNameExtension(fileName).toLowerCase())) {
				return true;
			}
		}
		return false;
	}

	public static boolean isAncestor(File ancestor, File file) {
		File mayBeAncestor = getCanonicalParent(file);
		while (true) {
			if (mayBeAncestor == null) {
				return false;
			}
			if (canonicallyEquals(mayBeAncestor, ancestor)) {
				return true;
			}
			mayBeAncestor = getCanonicalParent(mayBeAncestor);
		}
	}

}
