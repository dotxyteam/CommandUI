package xy.command.ui.util;

import java.io.File;
import java.io.FilenameFilter;

/**
 * Proxy of {@link FilenameFilter} that just counts files and folders accepted
 * by the specified base object.
 * 
 * @author olitank
 *
 */
public class CountingFilenameFilter implements FilenameFilter {
	protected int filteredCount = 0;
	protected FilenameFilter delegate;

	public CountingFilenameFilter(FilenameFilter delegate) {
		this.delegate = delegate;
	}

	public int getFilteredCount() {
		return filteredCount;
	}

	public FilenameFilter getDelegate() {
		return delegate;
	}

	@Override
	public boolean accept(File dir, String name) {
		boolean result = delegate.accept(dir, name);
		if (!result) {
			filteredCount++;
		}
		return result;
	}
}
