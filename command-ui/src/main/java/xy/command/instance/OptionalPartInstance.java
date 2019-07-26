package xy.command.instance;

import xy.command.model.OptionalPart;

public class OptionalPartInstance extends AbstractCommandLinePartInstance {

	public OptionalPart model;
	public ArgumentGroupInstance argumentGroupInstance = null;

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
