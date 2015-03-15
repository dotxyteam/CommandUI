package xy.command.model.instance;

import java.util.Collections;
import java.util.List;

import xy.command.model.InputArgument;

public class InputArgumentInstance extends AbstractCommandLinePartInstance {

	public String value;

	public InputArgumentInstance(InputArgument model) {
		super(model);
		if (model.defaultValue != null) {
			this.value = model.defaultValue;
		} else {
			value = "";
		}
	}

	public InputArgument getModel() {
		return (InputArgument) model;
	}

	@Override
	public List<String> listArgumentValues() {
		return Collections.singletonList(value);
	}

}
