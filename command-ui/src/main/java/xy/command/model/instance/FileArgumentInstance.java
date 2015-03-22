package xy.command.model.instance;

import java.util.Collections;
import java.util.List;

import xy.command.model.FileArgument;
import xy.command.ui.util.ValidationError;

public class FileArgumentInstance extends AbstractCommandLinePartInstance {

	public String value = "<path to the file>";
	
	public FileArgumentInstance(FileArgument model) {
		super(model);
		if (model.defaultValue != null) {
			this.value = model.defaultValue;
		} else {
			value = "";
		}
	}
	
	

	public FileArgument getModel() {
		return (FileArgument) model;
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
			throw contextualizeFieldValidationError(e, ((FileArgument) model).title);
		}

	}
}
