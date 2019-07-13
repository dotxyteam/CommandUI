package xy.command.ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Toolkit;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.lang.reflect.InvocationTargetException;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextPane;
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
import xy.reflect.ui.util.ReflectionUIError;
import javax.swing.JTextField;

public class CommandMonitoringDialog extends JDialog {

	protected static final long serialVersionUID = 1L;

	protected final JPanel contentPanel = new JPanel();
	protected Thread commandThread;
	protected JButton killOrCloseButton;
	protected JTextPane logTextControl;
	protected JCheckBox autoScrollControl;
	protected File workingDir;
	protected boolean killed = false;
	protected JPanel commandPanel;
	protected JTextField commandTextControl;
	protected JButton runButton;

	public static void main(String[] args) {
		try {

			CommandMonitoringDialog dialog = new CommandMonitoringDialog(null, "ping -n 10 localhost", null);
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
		this.workingDir = workingDir;

		setTitle("Command Execution");
		setIconImage(new BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB));
		setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
		setSize(400, 300);
		setLocationRelativeTo(null);
		getContentPane().setLayout(new BorderLayout());
		contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		getContentPane().add(contentPanel, BorderLayout.CENTER);
		contentPanel.setLayout(new BorderLayout(0, 0));
		{
			logTextControl = new JTextPane();
			logTextControl.setEditable(false);
			contentPanel.add(new JScrollPane(logTextControl));
		}
		{

			JPanel buttonPane = new JPanel();
			getContentPane().add(buttonPane, BorderLayout.SOUTH);
			buttonPane.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));
			{
				killOrCloseButton = new JButton();
				killOrCloseButton.setText("Close");
				killOrCloseButton.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						killOrClose();
					}
				});
				{
					{
						autoScrollControl = new JCheckBox("Auto-Scroll");
						autoScrollControl.setSelected(true);
						autoScrollControl.addActionListener(new ActionListener() {
							@Override
							public void actionPerformed(ActionEvent e) {
								onAutoScrollChange();
							}
						});
						buttonPane.add(autoScrollControl);
						onAutoScrollChange();
					}
				}
				killOrCloseButton.setActionCommand("");
				buttonPane.add(killOrCloseButton);
				getRootPane().setDefaultButton(killOrCloseButton);
			}
		}
		{
			commandPanel = new JPanel();
			getContentPane().add(commandPanel, BorderLayout.NORTH);
			commandPanel.setLayout(new BorderLayout(0, 0));
			{
				runButton = new JButton("Run");
				commandPanel.add(runButton, BorderLayout.WEST);
				runButton.addActionListener(new ActionListener() {
					@Override
					public void actionPerformed(ActionEvent e) {
						launchCommand();
					}
				});
			}
			{
				commandTextControl = new JTextField();
				commandPanel.add(commandTextControl, BorderLayout.CENTER);
			}
		}
		limitLines();
		if (command != null) {
			commandTextControl.setText(command);
			launchCommand();
		}
	}

	protected void onAutoScrollChange() {
		DefaultCaret caret = (DefaultCaret) logTextControl.getCaret();
		caret.setUpdatePolicy(autoScrollControl.isSelected() ? DefaultCaret.ALWAYS_UPDATE : DefaultCaret.NEVER_UPDATE);
	}

	protected void limitLines() {
		logTextControl.getDocument().addDocumentListener(new DocumentListener() {

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

			protected void update() {
				SwingUtilities.invokeLater(new Runnable() {
					@Override
					public void run() {
						limitLines();
					}
				});
			}

			protected void limitLines() {
				Document document = logTextControl.getDocument();
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
		if ((commandThread != null) && commandThread.isAlive()) {
			kill();
		}
		super.dispose();

	}

	protected int getMaximumlineCount() {
		return 100;
	}

	protected void launchCommand() {
		killOrCloseButton.setText("Kill");
		runButton.setEnabled(false);
		commandTextControl.setEnabled(false);
		logTextControl.setText("");
		commandThread = new Thread("Executing: " + commandTextControl.getText()) {
			@Override
			public void run() {
				try {
					CommandUIUtils.runCommand(commandTextControl.getText(), true,
							new DocumentOutputStream(logTextControl.getDocument(), getTextAttributes(Color.BLACK)),
							new DocumentOutputStream(logTextControl.getDocument(), getTextAttributes(Color.BLUE)),
							workingDir);
					if (!killed) {
						write("\n<Terminated>\n", getTextAttributes(Color.GREEN.darker().darker()));
					}
				} catch (final Throwable t) {
					if (!killed) {
						write("\n<An error occured>:\n" + new ReflectionUIError(t), getTextAttributes(Color.RED));
					}
				}
				SwingUtilities.invokeLater(new Runnable() {
					@Override
					public void run() {
						commandTerminated();
					}
				});
			}
		};
		commandThread.start();
	}

	protected void commandTerminated() {
		killOrCloseButton.setText("Close");
		runButton.setEnabled(true);
		commandTextControl.setEnabled(true);
	}

	protected AttributeSet getTextAttributes(Color color) {

		SimpleAttributeSet attributes = new SimpleAttributeSet();

		StyleConstants.setForeground(attributes, color);

		return attributes;

	}

	protected void killOrClose() {
		if ((commandThread != null) && commandThread.isAlive()) {
			kill();
		} else {
			CommandMonitoringDialog.this.dispose();
		}
	}

	protected void kill() {
		write("\n\n<Kill>\n\n", getTextAttributes(Color.RED));
		killed = true;
		commandThread.interrupt();
	}

	protected void write(final String string, final AttributeSet textAttributes) {
		Runnable runnable = new Runnable() {
			@Override
			public void run() {
				Document doc = logTextControl.getDocument();
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
		textArea.setPreferredSize(new Dimension(screenSize.width / 2, screenSize.height / 2));
		textArea.setEditable(false);
		JOptionPane.showMessageDialog(CommandMonitoringDialog.this, new JScrollPane(textArea), "An error Ocuured",
				JOptionPane.ERROR_MESSAGE);
	}

}
