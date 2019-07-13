package xy.command.instance;

import xy.command.model.Choice;

public class ChoiceInstance extends AbstractCommandLinePartInstance {

	public Choice model;
	public CommandLineInstance chosenPartInstance;

	public ChoiceInstance(Choice model) {
		this.model = model;
	}

	@Override
	public String getExecutionText() {
		return null;
	}

	
	
}
