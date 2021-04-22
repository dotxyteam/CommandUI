package xy.command.instance;

import xy.command.model.OptionalPart;

/**
 * The instance class of {@link OptionalPart}.
 * 
 * @author olitank
 *
 */
public class OptionalPartInstance extends AbstractCommandLinePartInstance {

	public OptionalPart model;
	public ArgumentGroupInstance argumentGroupInstance = null;

	/**
	 * The main constructor. Builds an instance from the given model part object.
	 * 
	 * @param model The model part object.
	 */
	public OptionalPartInstance(OptionalPart model) {
		this.model = model;
		if (model.activeByDefault) {
			argumentGroupInstance = new ArgumentGroupInstance(model);
		}
	}

	@Override
	public String getExecutionText() {
		if (argumentGroupInstance == null) {
			return null;
		} else {
			return argumentGroupInstance.getExecutionText();
		}
	}

	@Override
	public String toString() {
		return model.title + "=" + argumentGroupInstance;
	}
}
