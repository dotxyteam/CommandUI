package xy.command.ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Toolkit;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.lang.reflect.InvocationTargetException;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextPane;
import javax.swing.JToggleButton;
import javax.swing.SwingUtilities;
import javax.swing.border.EmptyBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultCaret;
import javax.swing.text.Document;
import javax.swing.text.Element;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;

import xy.command.ui.component.DocumentOutputStream;
import xy.command.ui.util.CommandUIUtils;
import xy.reflect.ui.util.ReflectionUIUtils;

public class CommandMonitoringDialog extends JDialog {

	private static final long serialVersionUID = 1L;

	private final JPanel contentPanel = new JPanel();

	private String command;

	private Thread commandThread;

	private JButton killOrCloseButton;

	private JTextPane textControl;

	private JToggleButton autoScrollButton;

	private File workingDir;

	private boolean killed = false;

	public static void main(String[] args) {

		try {

			CommandMonitoringDialog dialog = new CommandMonitoringDialog(null,

			"ping -n 100 localhost", null);

			dialog.setVisible(true);

		} catch (Exception e) {

			e.printStackTrace();

		}

	}

	/**
	 * 
	 * Create the dialog.
	 */

	public CommandMonitoringDialog(Window owner, String command, File workingDir) {

		super(owner);
		this.command = command;
		this.workingDir = workingDir;

		setTitle("Command Execution Monitoring");
		setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
		setBounds(100, 100, 450, 300);
		getContentPane().setLayout(new BorderLayout());
		contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		getContentPane().add(contentPanel, BorderLayout.CENTER);
		contentPanel.setLayout(new BorderLayout(0, 0));
		{
			textControl = new JTextPane();
			textControl.setEditable(false);
			contentPanel.add(new JScrollPane(textControl));
		}
		{

			JPanel buttonPane = new JPanel();
			getContentPane().add(buttonPane, BorderLayout.NORTH);
			buttonPane.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));
			{
				killOrCloseButton = new JButton();
				killOrCloseButton.setText("Kill");
				killOrCloseButton.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						killOrClose();
					}
				});
				{
					autoScrollButton = new JToggleButton("Auto-Scroll");
					autoScrollButton.setSelected(true);
					autoScrollButton.addActionListener(new ActionListener() {
						@Override
						public void actionPerformed(ActionEvent e) {
							onAutoScrollChange();
						}
					});
					onAutoScrollChange();
					buttonPane.add(autoScrollButton);
				}
				killOrCloseButton.setActionCommand("");
				buttonPane.add(killOrCloseButton);
				getRootPane().setDefaultButton(killOrCloseButton);
			}
		}
		limitLines();
		launchCommand();
	}

	protected void onAutoScrollChange() {
		DefaultCaret caret = (DefaultCaret) textControl.getCaret();
		caret.setUpdatePolicy(autoScrollButton.isSelected() ? DefaultCaret.ALWAYS_UPDATE
				: DefaultCaret.NEVER_UPDATE);
	}

	protected void limitLines() {
		textControl.getDocument().addDocumentListener(new DocumentListener() {

			@Override
			public void insertUpdate(DocumentEvent e) {
				update();
			}

			@Override
			public void removeUpdate(DocumentEvent e) {
				update();
			}

			@Override
			public void changedUpdate(DocumentEvent e) {
				update();
			}

			private void update() {
				SwingUtilities.invokeLater(new Runnable() {
					@Override
					public void run() {
						limitLines();
					}
				});
			}

			private void limitLines() {
				Document document = textControl.getDocument();
				Element root = document.getDefaultRootElement();
				while (root.getElementCount() > getMaximumlineCount()) {
					Element line = root.getElement(0);
					int end = line.getEndOffset();
					try {
						document.remove(0, end);
					} catch (BadLocationException ble) {
						System.out.println(ble);
					}
				}
			}
		});
	}

	@Override
	public void dispose() {
		if (commandThread.isAlive()) {
			commandThread.interrupt();
		}
		super.dispose();

	}

	protected int getMaximumlineCount() {
		return 100;
	}

	protected void launchCommand() {

		commandThread = new Thread("Executing: " + command) {
			@Override
			public void run() {
				killOrCloseButton.setText("Kill");
				write("Executing: " + command + "...\n",
						getTextAttributes(Color.BLACK));
				try {
					CommandUIUtils.runCommand(command, true,
							new DocumentOutputStream(textControl.getDocument(),
									getTextAttributes(Color.BLACK)),
							new DocumentOutputStream(textControl.getDocument(),
									getTextAttributes(Color.BLUE)), workingDir);
					if (!killed) {
						write("\n<Terminated>\n", getTextAttributes(Color.GREEN
								.darker().darker()));
					}
				} catch (final Throwable t) {
					if (!killed) {
						write("\n<An error occured>:\n"
								+ ReflectionUIUtils.getPrintedStackTrace(t),
								getTextAttributes(Color.RED));
					}
				}
				killOrCloseButton.setText("Close");

			}

		};

		commandThread.start();

	}

	protected AttributeSet getTextAttributes(Color color) {

		SimpleAttributeSet attributes = new SimpleAttributeSet();

		StyleConstants.setForeground(attributes, color);

		return attributes;

	}

	protected void killOrClose() {
		if (commandThread.isAlive()) {
			write("\n\n<Kill>\n\n", getTextAttributes(Color.RED));
			killed = true;
			commandThread.interrupt();
		} else {
			CommandMonitoringDialog.this.dispose();
		}
	}

	protected void write(final String string, final AttributeSet textAttributes) {
		Runnable runnable = new Runnable() {
			@Override
			public void run() {
				Document doc = textControl.getDocument();
				try {
					doc.insertString(doc.getLength(), string, textAttributes);
				} catch (BadLocationException e) {
					throw new AssertionError(e);
				}
			}
		};
		if (SwingUtilities.isEventDispatchThread()) {
			runnable.run();
		} else {
			try {
				SwingUtilities.invokeAndWait(runnable);
			} catch (InterruptedException e) {
				throw new AssertionError(e);
			} catch (InvocationTargetException e) {
				throw new AssertionError(e);
			}
		}
	}

	protected void showError(String errorMsg) {
		JTextArea textArea = new JTextArea(errorMsg);
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		textArea.setPreferredSize(new Dimension(screenSize.width / 2,
				screenSize.height / 2));
		textArea.setEditable(false);
		JOptionPane.showMessageDialog(CommandMonitoringDialog.this,
				new JScrollPane(textArea), "An error Ocuured",
				JOptionPane.ERROR_MESSAGE);
	}

}
