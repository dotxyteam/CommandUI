package xy.command.instance;

import xy.command.model.FixedArgument;
import xy.command.ui.util.CommandUIUtils;

/**
 * The instance class of {@link FixedArgument}.
 * 
 * @author olitank
 *
 */
public class FixedArgumentInstance extends AbstractCommandLinePartInstance {

	public FixedArgument model;

	/**
	 * The main constructor. Builds an instance from the given model part object.
	 * 
	 * @param model The model part object.
	 */
	public FixedArgumentInstance(FixedArgument model) {
		this.model = model;
	}

	@Override
	public String getExecutionText() {
		return CommandUIUtils.quoteArgument(model.value);
	}
	
	@Override
	public String toString() {
		return model.value;
	}
}
