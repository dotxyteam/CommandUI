package xy.command.model;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;

import xy.command.model.instance.CommandLineInstance;
import xy.reflect.ui.info.annotation.Documentation;

import com.thoughtworks.xstream.XStream;

@Documentation("Here you can specify and generate a GUI wrapper for your command line tool")
public class CommandLine {

	@Documentation("Title of the generated command line GUI")
	public String title;
	
	@Documentation("Description of the generated command line GUI")
	public String description;
	
	@Documentation("Relative or absolute path of the executable file")
	public File executablePath;
	
	@Documentation("The directory from which the command will be executed")
	public File executionDir;
	
	@Documentation("The list of arguments of the command line")
	public List<ArgumentPage> arguments = new ArrayList<ArgumentPage>();

	public CommandLineInstance createInstance() {
		return new CommandLineInstance(this);
	}

	@Documentation("Loads a command line specification file")
	public void loadFromFile(File input) {
		XStream xstream = new XStream();
		CommandLine loaded = (CommandLine) xstream.fromXML(input);
		title = loaded.title;
		description = loaded.description;
		executablePath = loaded.executablePath;
		executionDir = loaded.executionDir;
		arguments = loaded.arguments;
	}

	@Documentation("Saves the current command line specification in a file")
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
