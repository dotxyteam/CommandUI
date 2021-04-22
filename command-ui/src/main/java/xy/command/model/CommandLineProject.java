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

/**
 * This class allows to specify the structure of a command and its arguments in
 * order to generate GUI wrappers.
 * 
 * @author olitank
 *
 */
public class CommandLineProject extends CommandLine {

	private static final long serialVersionUID = 1L;

	/**
	 * Relative or absolute path of the executable file.
	 */
	public File executablePath = new File("");

	/**
	 * The directory from which the command will be executed.
	 */
	public File executionDir = new File(".");

	/**
	 * Allows to validate the correctness of the properties of this command line
	 * project.
	 * 
	 * @throws Exception If a property is not valid.
	 */
	public void validate() throws Exception {
		super.validate();
		if ((executablePath == null) || (executablePath.getPath().trim().length() == 0)) {
			throw new ValidationError("Enter the executable path");
		}
		if ((executionDir == null) || (executionDir.getPath().trim().length() == 0)) {
			throw new ValidationError("Enter the execution directory");
		}
	}

	/**
	 * Loads a command line project file.
	 * 
	 * @param input The input file.
	 * @throws IOException If an error occurs during the loading process.
	 */
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

	/**
	 * Saves the current command line project to a file.
	 * 
	 * @param output The output file.
	 * @throws IOException If an error occurs during the saving process.
	 */
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

	/**
	 * Loads the command line project from a stream.
	 * 
	 * @param input The input stream.
	 */
	public void loadFromStream(InputStream input) {
		XStream xstream = new XStream();
		CommandLineProject loaded = (CommandLineProject) xstream.fromXML(input);
		title = loaded.title;
		description = loaded.description;
		executablePath = loaded.executablePath;
		executionDir = loaded.executionDir;
		arguments = loaded.arguments;
	}

	/**
	 * Saves the current command line project to a stream.
	 * 
	 * @param output The output stream.
	 * @throws IOException If an error occurs during the saving process.
	 */
	public void saveToStream(OutputStream output) throws IOException {
		XStream xstream = new XStream();
		xstream.toXML(this, output);
	}

	/**
	 * Open a command dialog allowing to test any command.
	 */
	public void openCommandMonitoringDialog() {
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				CommandMonitoringDialog d = new CommandMonitoringDialog(null, null, new File("."));
				d.setVisible(true);
			}
		});
	}

	/**
	 * Generates a set of files allowing to open the GUI specified by the current
	 * project.
	 * 
	 * @param targetDirectory The directory in which the files will be generated.
	 * @throws Exception If an error occurs during the process.
	 */
	public void distribute(File targetDirectory) throws Exception {
		if (CommandLineUI.DEFAULT_EXE_FILE_PATH == null) {
			throw new UnsupportedOperationException("The default executable file is not known");
		}

		File exeFile = new File(CommandLineUI.DEFAULT_EXE_FILE_PATH);
		String fileExtension = FileUtils.getFileNameExtension(exeFile.getName());
		String fileName = title + ((fileExtension.length() > 0) ? ("." + fileExtension) : "");
		File targetExeFile = new File(targetDirectory, fileName);
		FileUtils.copy(exeFile, targetExeFile);

		File exeFileDirectory = exeFile.getAbsoluteFile().getParentFile();
		File guiCustomizationsFile = new File(exeFileDirectory, CommandLineUI.GUI_CUSTOMIZATIONS_FILE_PATH);
		File targetGuiCustomizationsFile = new File(targetDirectory, CommandLineUI.GUI_CUSTOMIZATIONS_FILE_PATH);
		FileUtils.copy(guiCustomizationsFile, targetGuiCustomizationsFile);

		File targetProjectFile = new File(targetExeFile.getPath() + "." + CommandLineUI.PROJECT_FILE_EXTENSION);
		saveToFile(targetProjectFile);
	}

}
