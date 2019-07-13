package xy.command.instance;

import xy.command.model.FileArgument;
import xy.command.ui.util.CommandUIUtils;

public class FileArgumentInstance extends AbstractCommandLinePartInstance {

	public FileArgument model;
	public String value;

	public FileArgumentInstance(FileArgument model) {
		this.model = model;
		value = model.defaultValue;
	}

	@Override
	public String getExecutionText() {
		return CommandUIUtils.quoteArgument(value);
	}
}
