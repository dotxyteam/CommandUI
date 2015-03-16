package xy.command.model.instance;

import xy.command.model.DirectoryArgument;

public class DirectoryArgumentInstance extends FileArgumentInstance {

	public DirectoryArgumentInstance(DirectoryArgument model) {
		super(model);
	}

	public DirectoryArgument getModel() {
		return (DirectoryArgument) model;
	}

}
