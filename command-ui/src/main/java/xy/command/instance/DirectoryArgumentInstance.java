package xy.command.instance;

import xy.command.model.DirectoryArgument;
import xy.command.ui.util.CommandUIUtils;

/**
 * The instance class of {@link DirectoryArgument}.
 * 
 * @author olitank
 *
 */
public class DirectoryArgumentInstance extends AbstractCommandLinePartInstance {

	public DirectoryArgument model;
	public String value;

	/**
	 * The main constructor. Builds an instance from the given model part object.
	 * 
	 * @param model The model part object.
	 */
	public DirectoryArgumentInstance(DirectoryArgument model) {
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
