package xy.command.instance;

import java.util.ArrayList;
import java.util.List;
import xy.command.model.ArgumentPage;
import xy.command.model.CommandLine;

public class CommandLineInstance extends AbstractCommandLinePartInstance{

	public CommandLine model;
	public List<ArgumentPageInstance> argumentPageInstances = new ArrayList<ArgumentPageInstance>();
	
	public CommandLineInstance(CommandLine model) {
		this.model = model;
		for(ArgumentPage page: model.arguments) {
			ArgumentPageInstance instance = page.instanciate();
			argumentPageInstances.add(instance);
		}
	}
}
