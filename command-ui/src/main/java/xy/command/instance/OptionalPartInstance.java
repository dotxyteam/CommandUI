package xy.command.instance;

import xy.command.model.OptionalPart;

public class OptionalPartInstance extends AbstractCommandLinePartInstance {

	public OptionalPart model;
	public CommandLineInstance commandLineInstance = null;

	public OptionalPartInstance(OptionalPart model) {
		this.model = model;
		if(model.activeByDefault) {
			commandLineInstance = new CommandLineInstance(model);
		}
	}

	@Override
	public String getExecutionText() {
		if (commandLineInstance == null) {
			return null;
		}else {
			return commandLineInstance.getExecutionText();
		}
	}

}
