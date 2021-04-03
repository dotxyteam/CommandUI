package xy.command.model;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import javax.swing.SwingUtilities;

import com.thoughtworks.xstream.XStream;

import xy.command.ui.CommandLineUI;
import xy.command.ui.CommandMonitoringDialog;
import xy.command.ui.util.FileUtils;
import xy.command.ui.util.ValidationError;

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
	public void loadFromFile(File input) throws IOException {
		FileInputStream stream = new FileInputStream(input);
		try {
			loadFromStream(stream);
		} finally {
			try {
				stream.close();
			} catch (Exception ignore) {
			}
		}
	}

	// @OnlineHelp("Saves the current command line specification in a file")
	public void saveToFile(File output) throws IOException {
		ByteArrayOutputStream memoryStream = new ByteArrayOutputStream();
		saveToStream(memoryStream);
		FileOutputStream stream = new FileOutputStream(output);
		try {
			stream.write(memoryStream.toByteArray());
		} finally {
			try {
				stream.close();
			} catch (Exception ignore) {
			}
		}
	}

	public void loadFromStream(InputStream input) {
		XStream xstream = new XStream();
		CommandLineProject loaded = (CommandLineProject) xstream.fromXML(input);
		title = loaded.title;
		description = loaded.description;
		executablePath = loaded.executablePath;
		executionDir = loaded.executionDir;
		arguments = loaded.arguments;
	}

	public void saveToStream(OutputStream output) throws IOException {
		XStream xstream = new XStream();
		xstream.toXML(this, output);
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

	public void distribute(File targetDirectory) throws Exception {
		if (CommandLineUI.DEFAULT_EXE_FILE_PATH == null) {
			throw new UnsupportedOperationException("The default executable file is not known");
		}

		File exeFile = new File(CommandLineUI.DEFAULT_EXE_FILE_PATH);
		File targetExeFile = new File(targetDirectory, title + "." + FileUtils.getFileNameExtension(exeFile.getName()));
		FileUtils.copy(exeFile, targetExeFile);

		File exeFileDirectory = exeFile.getAbsoluteFile().getParentFile();
		File guiCustomizationsFile = new File(exeFileDirectory, CommandLineUI.GUI_CUSTOMIZATIONS_FILE_PATH);
		File targetGuiCustomizationsFile = new File(targetDirectory, CommandLineUI.GUI_CUSTOMIZATIONS_FILE_PATH);
		FileUtils.copy(guiCustomizationsFile, targetGuiCustomizationsFile);

		File targetProjectFile = new File(targetExeFile.getPath() + "." + CommandLineUI.PROJECT_FILE_EXTENSION);
		saveToFile(targetProjectFile);
	}

}
