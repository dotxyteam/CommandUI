package xy.command.model.instance;

import java.util.Collections;
import java.util.List;

import xy.command.model.InputArgument;

public class InputArgumentInstance extends AbstractCommandLinePartInstance {

	public String title;
	public String value;
	
	public InputArgumentInstance(InputArgument model) {
		super(model);
		this.value = "<value>";
	}

	public InputArgument getModel() {
		return (InputArgument) model;
	}

	@Override
	public List<String> listArgumentValues() {
		return Collections.singletonList(value);
	}

}
