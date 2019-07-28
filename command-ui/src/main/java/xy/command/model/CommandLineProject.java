package xy.command.model;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import javax.swing.SwingUtilities;

import xy.command.ui.CommandMonitoringDialog;
import xy.command.ui.util.ValidationError;

import com.thoughtworks.xstream.XStream;

//@OnlineHelp("Here you can specify and generate a GUI wrapper for your command line tool")
public class CommandLineProject extends CommandLine {

	private static final long serialVersionUID = 1L;

	// @OnlineHelp("Relative or absolute path of the executable file")
	public File executablePath = new File("");

	// @OnlineHelp("The directory from which the command will be executed")
	public File executionDir = new File(".");

	// @Validating
	public void validate() throws Exception {
		super.validate();
		if ((executablePath == null) || (executablePath.getPath().trim().length() == 0)) {
			throw new ValidationError("Enter the executable path");
		}
		if ((executionDir == null) || (executionDir.getPath().trim().length() == 0)) {
			throw new ValidationError("Enter the execution directory");
		}
	}

	// @OnlineHelp("Loads a command line specification file")
	public void loadFromFile(File input) {
		XStream xstream = new XStream();
		CommandLineProject loaded = (CommandLineProject) xstream.fromXML(input);
		title = loaded.title;
		description = loaded.description;
		executablePath = loaded.executablePath;
		executionDir = loaded.executionDir;
		arguments = loaded.arguments;
	}

	// @OnlineHelp("Saves the current command line specification in a file")
	public void saveToFile(File output) throws IOException {
		XStream xstream = new XStream();
		FileWriter fileWriter = new FileWriter(output);
		xstream.toXML(this, fileWriter);
		fileWriter.flush();
		fileWriter.close();
	}

	public void openCommandMonitoringDialog() {
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				CommandMonitoringDialog d = new CommandMonitoringDialog(null, null, new File("."));
				d.setVisible(true);
			}
		});
	}
	
	
}
