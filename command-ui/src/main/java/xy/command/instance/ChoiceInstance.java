package xy.command.instance;

import xy.command.model.Choice;

public class ChoiceInstance extends AbstractCommandLinePartInstance {

	public Choice model;
	public ArgumentGroupInstance chosenPartInstance;

	public ChoiceInstance(Choice model) {
		this.model = model;
		if (model.options.size() > 0) {
			chosenPartInstance = (ArgumentGroupInstance) model.options.get(0).instanciate();
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
