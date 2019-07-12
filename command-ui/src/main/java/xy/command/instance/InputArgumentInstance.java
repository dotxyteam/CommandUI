package xy.command.instance;

import xy.command.model.InputArgument;

public class InputArgumentInstance extends AbstractCommandLinePartInstance {

	public InputArgument model;

	public InputArgumentInstance(InputArgument model) {
		this.model = model;
	}

	public String value;

}
