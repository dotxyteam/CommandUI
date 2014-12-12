package xy.command.model.instance;

import java.util.ArrayList;
import java.util.List;

import xy.command.model.ArgumentPage;
import xy.command.model.CommandLine;
import xy.command.ui.util.CommandUIUtils;

public class CommandLineInstance {

	public List<ArgumentPageInstance> pageInstances = new ArrayList<ArgumentPageInstance>();
	protected CommandLine model;
	
	public CommandLineInstance(CommandLine model) {
		this.model = model;
		for (ArgumentPage page : model.pages) {
			pageInstances.add(page.createInstance());
		}
	}

	public CommandLine getModel() {
		return model;
	}

	public String getCommandlineString(){
		List<String> args = new ArrayList<String>();
		args.add(model.executablePath);
		for (ArgumentPageInstance part : pageInstances) {
			args.addAll(part.listArgumentValues());
		}
		return CommandUIUtils.formatArgumentList(args);
	}

}
