package xy.command.instance;

import xy.command.model.InputArgument;
import xy.command.ui.util.CommandUIUtils;

/**
 * The instance class of {@link InputArgument}.
 * 
 * @author olitank
 *
 */
public class InputArgumentInstance extends AbstractCommandLinePartInstance {

	public InputArgument model;
	public String value;

	/**
	 * The main constructor. Builds an instance from the given model part object.
	 * 
	 * @param model The model part object.
	 */
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
