package xy.command.model;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import xy.command.model.instance.CommandLineInstance;
import xy.command.ui.CommandLinePlayer;

import com.thoughtworks.xstream.XStream;

public class CommandLine {

	public String title;
	public String documentation;
	public String executablePath;
	public String executionDir;
	public List<ArgumentPage> arguments = new ArrayList<ArgumentPage>();
	
	
	public CommandLineInstance createInstance() {
		return new CommandLineInstance(this);
	}
	
	
	public void load(File input) {
		XStream xstream = new XStream();
		CommandLine loaded = (CommandLine)xstream.fromXML(input);
		title = loaded.title;
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
	
	public void test(){
		CommandLinePlayer player = new CommandLinePlayer();
		player.openObjectFrame(createInstance(), title, null);
	}

		
}
