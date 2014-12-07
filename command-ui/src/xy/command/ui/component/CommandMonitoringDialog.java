package xy.command.ui.component;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextPane;
import javax.swing.JToggleButton;
import javax.swing.border.EmptyBorder;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;

import xy.command.ui.util.CommandUIUtils;

public class CommandMonitoringDialog extends JDialog {

	private static final long serialVersionUID = 1L;

	private final JPanel contentPanel = new JPanel();

	private String command;

	private Thread commandThread;

	private JButton killOrCloseButton;

	private JTextPane textControl;

	private JToggleButton autoScrollButton;

	private JScrollPane scrollPane;

	private AutoScrollerToMaximum scrollerToBottom;

	private File workingDir;

	/**
	 * 
	 * Launch the application.
	 */

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

		setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);

		setBounds(100, 100, 450, 300);

		getContentPane().setLayout(new BorderLayout());

		contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));

		getContentPane().add(contentPanel, BorderLayout.CENTER);

		contentPanel.setLayout(new BorderLayout(0, 0));

		{

			textControl = new JTextPane();

			textControl.setEditable(false);

			textControl.getDocument().addDocumentListener(
					new LimitLinesDocumentListener(getMaximumlineCount()));

			contentPanel.add(scrollPane = new JScrollPane(textControl));

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

					buttonPane.add(autoScrollButton);

				}

				killOrCloseButton.setActionCommand("");

				buttonPane.add(killOrCloseButton);

				getRootPane().setDefaultButton(killOrCloseButton);

			}

		}

		scrollerToBottom = new AutoScrollerToMaximum(
				scrollPane.getVerticalScrollBar()) {

			@Override
			public boolean shouldScroll() {

				return autoScrollButton.isSelected() && super.shouldScroll();

			}

		};

		scrollerToBottom.start();

		launchCommand();

	}

	@Override
	public void dispose() {
		if (commandThread.isAlive()) {
			commandThread.interrupt();
		}
		scrollerToBottom.interrupt();
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

				CommandUIUtils.runCommand(command, true,
						new DocumentOutputStream(textControl.getDocument(),
								getTextAttributes(Color.BLACK)),
						new DocumentOutputStream(textControl.getDocument(),
								getTextAttributes(Color.BLUE)), workingDir);

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

			commandThread.interrupt();

			while (commandThread.isAlive()) {

				try {

					Thread.sleep(100);

				} catch (InterruptedException e) {

					throw new AssertionError(e);

				}

			}

			write("\n<Killed>\n", getTextAttributes(Color.RED));

		} else {

			CommandMonitoringDialog.this.dispose();

		}

	}

	protected void write(String string, AttributeSet textAttributes) {

		Document doc = textControl.getDocument();

		try {

			doc.insertString(doc.getLength(), string, textAttributes);

		} catch (BadLocationException e) {

			throw new AssertionError(e);

		}

	}

	protected void showError(String errorMsg) {

		JOptionPane.showMessageDialog(CommandMonitoringDialog.this, errorMsg,

		"An error Ocuured", JOptionPane.ERROR_MESSAGE);

	}

}
