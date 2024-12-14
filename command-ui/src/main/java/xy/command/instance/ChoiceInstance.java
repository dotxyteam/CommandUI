package xy.command.instance;

import xy.command.model.Choice;

/**
 * The instance class of {@link Choice}.
 * 
 * @author olitank
 *
 */
public class ChoiceInstance extends AbstractCommandLinePartInstance {

	public Choice model;
	public ArgumentGroupInstance chosenPartInstance;

	/**
	 * The main constructor. Builds an instance from the given model part object.
	 * 
	 * @param model The model part object.
	 */
	public ChoiceInstance(Choice model) {
		this.model = model;
		if (model.options.size() > 0) {
			chosenPartInstance = (ArgumentGroupInstance) model.options.get(0).instantiate();
		}
	}

	@Override
	public String getExecutionText() {
		return chosenPartInstance.getExecutionText();
	}

	@Override
	public String toString() {
		return model.title + "=" + chosenPartInstance;
	}

}
