package xy.command.model.instance;

import xy.command.model.DirectoryArgument;

public class DirectoryArgumentInstance extends FileArgumentInstance {

	public DirectoryArgumentInstance(DirectoryArgument model) {
		super(model);
		this.value = "<path to the directory>";
	}

	public DirectoryArgument getModel() {
		return (DirectoryArgument) model;
	}

}
