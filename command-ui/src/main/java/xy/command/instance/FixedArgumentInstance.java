package xy.command.instance;

import xy.command.model.FixedArgument;
import xy.command.ui.util.CommandUIUtils;

public class FixedArgumentInstance extends AbstractCommandLinePartInstance {

	public FixedArgument model;

	public FixedArgumentInstance(FixedArgument model) {
		this.model = model;
	}

	@Override
	public String getExecutionText() {
		return CommandUIUtils.quoteArgument(model.value);
	}
}
