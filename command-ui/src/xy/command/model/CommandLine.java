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
	public String executablePath;
	public String executionDir;
	public List<AbstractCommandLinePart> parts = new ArrayList<AbstractCommandLinePart>();
	
	
	public CommandLineInstance createInstance(CommandLinePlayer player) {
		return new CommandLineInstance(player, this);
	}
	
	
	public void load(File file) {
		XStream xstream = new XStream();
		CommandLine loaded = (CommandLine)xstream.fromXML(file);
		title = loaded.title;
		executablePath = loaded.executablePath;
		executionDir = loaded.executionDir;
		parts = loaded.parts;
	}
	
	public void save(File file) throws IOException {
		XStream xstream = new XStream();
		FileWriter fileWriter = new FileWriter(file);
		xstream.toXML(this, fileWriter);
		fileWriter.flush();
		fileWriter.close();
	}
	
	public void test(){
		CommandLinePlayer player = new CommandLinePlayer();
		player.openObjectFrame(createInstance(player), title, null);
	}

		
}
