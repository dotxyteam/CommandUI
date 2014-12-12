package xy.command.ui.util;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JPanel;

import xy.command.model.ArgumentGroup.Layout;

public class CommandUIUtils {

	public static Component flowInLayout(Component c, int flowLayoutAlignment) {
		JPanel result = new JPanel();
		result.setLayout(new FlowLayout(flowLayoutAlignment));
		result.add(c);
		return result;
	}

	public static JPanel createPanel(final Layout layout) {
		return new JPanel() {
			protected static final long serialVersionUID = 1L;
			protected int addComponentCount = 0;
			{
				setLayout(new GridBagLayout());
			}

			@Override
			public Component add(Component comp) {
				GridBagConstraints gc = new GridBagConstraints();
				gc.weightx = 1.0;
				gc.weighty = 1.0;
				if (layout == Layout.COLUMN) {
					gc.gridx = 0;
					gc.gridy = addComponentCount;
					gc.fill = GridBagConstraints.HORIZONTAL;
				} else if (layout == Layout.ROW) {
					gc.gridx = addComponentCount;
					gc.gridy = 0;
					gc.fill = GridBagConstraints.VERTICAL;
				} else {
					throw new AssertionError();
				}
				int SPACING = 5;
				gc.insets = new Insets(SPACING, SPACING, SPACING, SPACING);
				super.add(comp, gc);
				addComponentCount++;
				return comp;
			}
		};
	}

	public static Component withLabel(Component c, String title) {
		JPanel result = new JPanel();
		result.setLayout(new BorderLayout());
		result.add(c, BorderLayout.CENTER);
		result.setBorder(BorderFactory.createTitledBorder(title));
		return result;
	}
	
	
	public static int runCommand(final String command, boolean wait,
			final OutputStream outReceiver, final OutputStream errReceiver, File workingDir) {
		final Process process;
		try {
			process = Runtime.getRuntime().exec(command, null, workingDir);
		} catch (IOException e1) {
			throw new AssertionError("Command execution error: "
					+ e1.toString());
		}

		final Thread outputRedirector;
		{
			String reason = "Output Stream Consumption for command: " + command;
			if (outReceiver != null) {
				outputRedirector = redirectStream(process.getInputStream(),
						outReceiver, reason);
			} else {
				outputRedirector = redirectStream(process.getInputStream(),
						getNullOutputStream(), reason);
			}
		}

		final Thread errorRedirector;
		{
			String reason = "Error Stream Consumption for command: " + command;
			if (errReceiver != null) {
				errorRedirector = redirectStream(process.getErrorStream(),
						errReceiver, reason);
			} else {
				errorRedirector = redirectStream(process.getErrorStream(),
						getNullOutputStream(), reason);
			}
		}

		Thread cleaner = new Thread("Cleaner for command: " + command) {
			@Override
			public void run() {
				try {
					process.waitFor();
				} catch (Exception e) {
					throw new AssertionError(e);
				}
				for (Thread thread : new Thread[] { outputRedirector,
						errorRedirector }) {
					if (thread.isAlive()) {
						thread.interrupt();
						while (thread.isAlive()) {
							relieveCPU();
						}
					}
				}
				try {
					process.getInputStream().close();
					process.getErrorStream().close();
				} catch (IOException e) {
					throw new AssertionError(e);
				}
			}
		};

		cleaner.start();

		if (!wait) {
			return 0;
		}

		try {
			cleaner.join();
		} catch (InterruptedException e) {
			Thread.currentThread().interrupt();
		}

		if (Thread.currentThread().isInterrupted()) {
			process.destroy();
			return 0;
		} else {
			return process.exitValue();
		}
	}

	public static void sleep(long durationMilliseconds) {
		try {
			Thread.sleep(durationMilliseconds);
		} catch (InterruptedException e) {
			Thread.currentThread().interrupt();
		}
	}

	private static OutputStream getNullOutputStream() {
		return new OutputStream() {
			@Override
			public void write(int b) throws IOException {
			}
		};
	}

	public static OutputStream unifyOutputStreams(
			final OutputStream[] outputStreams) {
		return new OutputStream() {

			@Override
			public void write(int b) throws IOException {
				for (OutputStream out : outputStreams) {
					out.write(b);
				}
			}

			@Override
			public void write(byte[] b) throws IOException {
				for (OutputStream out : outputStreams) {
					out.write(b);
				}
			}

			@Override
			public void write(byte[] b, int off, int len) throws IOException {
				for (OutputStream out : outputStreams) {
					out.write(b, off, len);
				}
			}

			@Override
			public void flush() throws IOException {
				for (OutputStream out : outputStreams) {
					out.flush();
				}
			}

			@Override
			public void close() throws IOException {
				for (OutputStream out : outputStreams) {
					out.close();
				}
			}
		};
	}

	public static Thread redirectStream(final InputStream src,
			final OutputStream dst, String reason) {
		Thread thread = new Thread("Stream Redirector (" + reason + ")") {
			public void run() {
				try {
					while (true) {
						if (src.available() > 0) {
							int b = src.read();
							if (b == -1) {
								break;
							}
							dst.write(b);
						} else {
							if (isInterrupted()) {
								if (src.available() == 0) {
									break;
								}
							} else {
								relieveCPU();
							}
						}
					}
				} catch (IOException e) {
					throw new AssertionError(e);
				}
			}
		};
		thread.start();
		return thread;
	}

	public static void relieveCPU() {
		sleep(100);
	}

	public static String formatArgumentList(List<String> argList) {
		StringBuilder result = new StringBuilder();
		for (String arg : argList) {
			result.append(" " + arg);
		}
		return result.toString();
	}


}
