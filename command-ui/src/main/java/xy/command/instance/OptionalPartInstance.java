package xy.command.instance;

import xy.command.model.OptionalPart;

public class OptionalPartInstance extends AbstractCommandLinePartInstance {

	public OptionalPart model;
	public boolean selected = false;
	public CommandLineInstance commandLineInstance = null;
	
	public OptionalPartInstance(OptionalPart model) {
		this.model = model;
	}

}
