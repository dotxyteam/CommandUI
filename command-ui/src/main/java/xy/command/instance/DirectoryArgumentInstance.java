package xy.command.instance;

import xy.command.model.DirectoryArgument;

public class DirectoryArgumentInstance extends AbstractCommandLinePartInstance {

	public DirectoryArgument model;

	public DirectoryArgumentInstance(DirectoryArgument model) {
		this.model = model;
	}
	
	public String getLabel() {
		return model.title;
	}

	public String value;
}
