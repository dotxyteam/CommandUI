package xy.command.instance;

import java.util.ArrayList;
import java.util.List;

import xy.command.model.ArgumentPage;
import xy.command.model.CommandLine;

public class CommandLineInstance extends AbstractCommandLinePartInstance {

	public CommandLine model;
	public List<ArgumentPageInstance> argumentPageInstances = new ArrayList<ArgumentPageInstance>();

	public CommandLineInstance(CommandLine model) {
		this.model = model;
		for (ArgumentPage page : model.arguments) {
			ArgumentPageInstance instance = page.instanciate();
			argumentPageInstances.add(instance);
		}
	}

	@Override
	public String getExecutionText() {
		StringBuilder result = new StringBuilder();
		int i = 0;
		for (ArgumentPageInstance pageInstance : argumentPageInstances) {
			for (AbstractCommandLinePartInstance partInstance : pageInstance.partInstances) {
				if (i > 0) {
					result.append(" ");
				}
				String partInstanceExecutionText = partInstance.getExecutionText();
				if (partInstanceExecutionText == null) {
					continue;
				}
				result.append(partInstanceExecutionText);
				i++;
			}
		}
		return result.toString();
	}

	@Override
	public String toString() {
		StringBuilder result = new StringBuilder();
		int i = 0;
		for (ArgumentPageInstance pageInstance : argumentPageInstances) {
			for (AbstractCommandLinePartInstance partInstance : pageInstance.partInstances) {
				if(partInstance instanceof FixedArgumentInstance) {
					continue;
				}
				if (i > 0) {
					result.append(", ");
				}
				result.append(partInstance.toString());
				i++;
			}
		}
		return result.toString();
	}

}
