package xy.command.ui.util;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.FlowLayout;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;
import java.util.StringTokenizer;
import java.util.Vector;

import javax.swing.BorderFactory;
import javax.swing.JPanel;

public class CommandUIUtils {

	public static Component flowInLayout(Component c, int flowLayoutAlignment) {
		JPanel result = new JPanel();
		result.setLayout(new FlowLayout(flowLayoutAlignment));
		result.add(c);
		return result;
	}

	public static Component withLabel(Component c, String title) {
		JPanel result = new JPanel();
		result.setLayout(new BorderLayout());
		result.add(c, BorderLayout.CENTER);
		result.setBorder(BorderFactory.createTitledBorder(title));
		return result;
	}

	public static int runCommand(final String command, boolean wait, final OutputStream outReceiver,
			final OutputStream errReceiver, File workingDir) {
		String[] args = splitArguments(command);
		if (args.length == 0) {
			throw new RuntimeException("Executable file not specified");
		}
		final Process process;
		try {
			process = Runtime.getRuntime().exec(args, null, workingDir);
		} catch (IOException e1) {
			throw new AssertionError("Command execution error: " + e1.toString());
		}

		final Thread outputRedirector;
		{
			String reason = "Output Stream Consumption for command: " + command;
			if (outReceiver != null) {
				outputRedirector = redirectStream(process.getInputStream(), outReceiver, reason);
			} else {
				outputRedirector = redirectStream(process.getInputStream(), getNullOutputStream(), reason);
			}
		}

		final Thread errorRedirector;
		{
			String reason = "Error Stream Consumption for command: " + command;
			if (errReceiver != null) {
				errorRedirector = redirectStream(process.getErrorStream(), errReceiver, reason);
			} else {
				errorRedirector = redirectStream(process.getErrorStream(), getNullOutputStream(), reason);
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
				CommandUIUtils.sleep(1000);
				for (Thread thread : new Thread[] { outputRedirector, errorRedirector }) {
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

	protected static OutputStream getNullOutputStream() {
		return new OutputStream() {
			@Override
			public void write(int b) throws IOException {
			}
		};
	}

	public static OutputStream unifyOutputStreams(final OutputStream[] outputStreams) {
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

	public static Thread redirectStream(final InputStream src, final OutputStream dst, String reason) {
		Thread thread = new Thread("Stream Redirector (" + reason + ")") {
			public void run() {
				try {
					while (true) {
						if (src.available() > 0) {
							int b = src.read();
							if (b == -1) {
								break;
							}
							try {
								dst.write(b);
							} catch (Throwable t) {
								throw new RuntimeException(t);
							}
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
					if (e.toString().toLowerCase().contains("stream closed")) {
						return;
					}
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
		for (int i = 0; i < argList.size(); i++) {
			String arg = argList.get(i);
			if (i > 0) {
				result.append(" ");
			}
			result.append(arg);
		}
		return result.toString();
	}

	public static String quoteArgument(String argument) {
		String[] argumentSplitByQuotes = argument.split("\"", -1);
		if (argumentSplitByQuotes.length == 1) {
			return "\"" + argument + "\"";
		} else {
			StringBuilder result = new StringBuilder();
			for (int i = 0; i < argumentSplitByQuotes.length; i++) {
				if (i > 0) {
					result.append("'\"'");
				}
				String elementOfArgumentSplitByQuotes = argumentSplitByQuotes[i];
				result.append(quoteArgument(elementOfArgumentSplitByQuotes));
			}
			return result.toString();
		}
	}

	public static String[] splitArguments(String s) {
		if ((s == null) || (s.length() == 0)) {
			return new String[0];
		}
		final int normal = 0;
		final int inQuote = 1;
		final int inDoubleQuote = 2;
		int state = normal;
		StringTokenizer tok = new StringTokenizer(s, "\"\' ", true);
		Vector<String> v = new Vector<String>();
		StringBuilder current = new StringBuilder();

		while (tok.hasMoreTokens()) {
			String nextTok = tok.nextToken();
			switch (state) {
			case inQuote:
				if ("\'".equals(nextTok)) {
					state = normal;
				} else {
					current.append(nextTok);
				}
				break;
			case inDoubleQuote:
				if ("\"".equals(nextTok)) {
					state = normal;
				} else {
					current.append(nextTok);
				}
				break;
			default:
				if ("\'".equals(nextTok)) {
					state = inQuote;
				} else if ("\"".equals(nextTok)) {
					state = inDoubleQuote;
				} else if (" ".equals(nextTok)) {
					if (current.length() != 0) {
						v.addElement(current.toString());
						current.setLength(0);
					}
				} else {
					current.append(nextTok);
				}
				break;
			}
		}

		if (current.length() != 0) {
			v.addElement(current.toString());
		}

		if ((state == inQuote) || (state == inDoubleQuote)) {
			throw new RuntimeException("unbalanced quotes in " + s);
		}

		String[] args = new String[v.size()];
		v.copyInto(args);
		return args;
	}

}
