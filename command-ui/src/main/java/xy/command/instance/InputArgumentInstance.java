package xy.command.instance;

import xy.command.model.InputArgument;
import xy.command.ui.util.CommandUIUtils;

public class InputArgumentInstance extends AbstractCommandLinePartInstance {

	public InputArgument model;
	public String value;

	public InputArgumentInstance(InputArgument model) {
		this.model = model;
		value = model.defaultValue;
	}

	@Override
	public String getExecutionText() {
		return CommandUIUtils.quoteArgument(value);
	}

	@Override
	public String toString() {
		return model.title + "=" + value;
	}
}
