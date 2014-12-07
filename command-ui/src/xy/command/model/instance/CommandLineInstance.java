package xy.command.model.instance;

import java.util.ArrayList;
import java.util.List;

import xy.command.model.AbstractCommandLinePart;
import xy.command.model.CommandLine;
import xy.command.ui.CommandLinePlayer;

public class CommandLineInstance {

	public List<AbstractCommandLinePartInstance> partInstances = new ArrayList<AbstractCommandLinePartInstance>();
	protected CommandLine model;
	
	public CommandLineInstance(CommandLinePlayer player, CommandLine model) {
		this.model = model;
		for (AbstractCommandLinePart part : model.parts) {
			partInstances.add(part.createInstance(player));
		}
	}

	public CommandLine getModel() {
		return model;
	}

	public String getCommandlineString(){
		List<String> args = new ArrayList<String>();
		args.add(model.executablePath);
		for (AbstractCommandLinePartInstance part : partInstances) {
			args.addAll(part.listArgumentValues());
		}
		return formatArgumentList(args);
	}

	protected String formatArgumentList(List<String> argList) {
		StringBuilder result = new StringBuilder();
		for (String arg : argList) {
			result.append(" " + arg);
		}
		return result.toString();
	}

}
