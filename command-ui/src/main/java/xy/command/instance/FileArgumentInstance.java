package xy.command.instance;

import xy.command.model.FileArgument;
import xy.command.ui.util.CommandUIUtils;

/**
 * The instance class of {@link FileArgument}.
 * 
 * @author olitank
 *
 */
public class FileArgumentInstance extends AbstractCommandLinePartInstance {

	public FileArgument model;
	public String value;

	/**
	 * The main constructor. Builds an instance from the given model part object.
	 * 
	 * @param model The model part object.
	 */
	public FileArgumentInstance(FileArgument model) {
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
