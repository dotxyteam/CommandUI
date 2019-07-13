package xy.command.instance;

import xy.command.model.DirectoryArgument;
import xy.command.ui.util.CommandUIUtils;

public class DirectoryArgumentInstance extends AbstractCommandLinePartInstance {

	public DirectoryArgument model;
	public String value;

	public DirectoryArgumentInstance(DirectoryArgument model) {
		this.model = model;
		value = model.defaultValue;
	}

	
	@Override
	public String getExecutionText() {
		return CommandUIUtils.quoteArgument(value);
	}
}
