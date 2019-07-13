package xy.command.instance;

import xy.command.model.MultiplePart;

public class MultiplePartInstance extends AbstractCommandLinePartInstance {

	public MultiplePart model;
	public CommandLineInstance[] commandLineInstances = new CommandLineInstance[0];

	public MultiplePartInstance(MultiplePart model) {
		this.model = model;
	}

	@Override
	public String getExecutionText() {
		StringBuilder result = new StringBuilder();
		int i = 0;
		for (CommandLineInstance instance : commandLineInstances) {
			if (i > 0) {
				result.append(" ");
			}
			result.append(instance.getExecutionText());
			i++;
		}
		return result.toString();
	}

}
