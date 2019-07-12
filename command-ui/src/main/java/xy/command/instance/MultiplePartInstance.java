package xy.command.instance;

import xy.command.model.MultiplePart;

public class MultiplePartInstance extends AbstractCommandLinePartInstance {

	public MultiplePart model;
	public CommandLineInstance[] commandLineInstances = new CommandLineInstance[0];

	public MultiplePartInstance(MultiplePart model) {
		this.model = model;
	}

}
