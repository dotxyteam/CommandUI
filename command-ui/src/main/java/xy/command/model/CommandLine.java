package xy.command.model;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import xy.command.model.instance.CommandLineInstance;

import com.thoughtworks.xstream.XStream;

public class CommandLine {

	public String title;
	public String description;
	public File executablePath;
	public File executionDir;
	public List<ArgumentPage> arguments = new ArrayList<ArgumentPage>();
	
	
	public CommandLineInstance createInstance() {
		return new CommandLineInstance(this);
	}
	
	
	public void load(File input) {
		XStream xstream = new XStream();
		CommandLine loaded = (CommandLine)xstream.fromXML(input);
		title = loaded.title;
		description = loaded.description;
		executablePath = loaded.executablePath;
		executionDir = loaded.executionDir;
		arguments = loaded.arguments;
	}
	
	public void save(File output) throws IOException {
		XStream xstream = new XStream();
		FileWriter fileWriter = new FileWriter(output);
		xstream.toXML(this, fileWriter);
		fileWriter.flush();
		fileWriter.close();
	}
	
	@Override
	public String toString() {
		return title;
	}

		
}
