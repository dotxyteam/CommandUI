package xy.command.ui.util;

import java.io.File;
import java.io.FilenameFilter;

public class CountingFilenameFilter implements FilenameFilter {
		private int filteredCount = 0;
		private FilenameFilter delegate;

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

