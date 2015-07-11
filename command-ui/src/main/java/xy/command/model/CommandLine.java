package xy.command.model;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;
import xy.command.model.instance.CommandLineInstance;
import xy.command.ui.util.ValidationError;
import xy.reflect.ui.info.annotation.Hidden;
import xy.reflect.ui.info.annotation.OnlineHelp;
import xy.reflect.ui.info.annotation.Validating;

import com.thoughtworks.xstream.XStream;

@OnlineHelp("Here you can specify and generate a GUI wrapper for your command line tool")
public class CommandLine {

	@OnlineHelp("Title of the generated command line GUI")
	public String title;

	@OnlineHelp("Description of the generated command line GUI")
	public String description;

	@OnlineHelp("Relative or absolute path of the executable file")
	public File executablePath;

	@OnlineHelp("The directory from which the command will be executed")
	public File executionDir;

	@OnlineHelp("The list of arguments of the command line")
	public List<ArgumentPage> arguments = new ArrayList<ArgumentPage>();

	@Hidden
	public CommandLineInstance createInstance() {
		return new CommandLineInstance(this);
	}

	@Validating
	public void validate() throws Exception {
		if ((title == null) || (title.trim().length() == 0)) {
			throw new ValidationError("Enter the title");
		}
		if ((executablePath == null)
				|| (executablePath.getPath().trim().length() == 0)) {
			throw new ValidationError("Enter the executable path");
		}
		if ((executionDir == null)
				|| (executionDir.getPath().trim().length() == 0)) {
			throw new ValidationError("Enter the execution directory");
		}
	}

	@OnlineHelp("Loads a command line specification file")
	public void loadFromFile(File input) {
		XStream xstream = new XStream();
		CommandLine loaded = (CommandLine) xstream.fromXML(input);
		title = loaded.title;
		description = loaded.description;
		executablePath = loaded.executablePath;
		executionDir = loaded.executionDir;
		arguments = loaded.arguments;
	}

	@OnlineHelp("Saves the current command line specification in a file")
	public void saveToFile(File output) throws IOException {
		XStream xstream = new XStream();
		FileWriter fileWriter = new FileWriter(output);
		xstream.toXML(this, fileWriter);
		fileWriter.flush();
		fileWriter.close();
	}

	public String getUsageText() {
		try {
			StringWriter out = new StringWriter();
			out.write(executablePath.getPath() + " ");
			boolean first = true;
			for (ArgumentPage page : arguments) {
				for (AbstractCommandLinePart part : page.parts) {
					if (!first) {
						out.append(" ");
					}
					try {
						part.writetUsageText(out);
					} catch (IOException e) {
						throw new AssertionError(e);
					}
					first = false;
				}
			}
			return out.toString();
		} catch (Throwable t) {
			return null;
		}
	}

	@Override
	public String toString() {
		return title;
	}

}
