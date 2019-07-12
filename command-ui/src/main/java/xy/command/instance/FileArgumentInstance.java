package xy.command.instance;

import xy.command.model.FileArgument;

public class FileArgumentInstance extends AbstractCommandLinePartInstance {

	public FileArgument model;

	public FileArgumentInstance(FileArgument model) {
		this.model = model;
	}

	public String value;
}
