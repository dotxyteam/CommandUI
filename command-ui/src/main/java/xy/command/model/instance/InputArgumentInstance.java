package xy.command.model.instance;

import java.util.Collections;
import java.util.List;

import xy.command.model.InputArgument;
import xy.command.ui.util.ValidationError;

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

	@Override
	public void validate() throws Exception {
		try {
			if ((value == null) || (value.trim().length() == 0)) {
				throw new ValidationError("Enter the value");
			}
		} catch (Exception e) {
			throw contextualizeFieldValidationError(e, ((InputArgument) model).title);
		}

	}

}
