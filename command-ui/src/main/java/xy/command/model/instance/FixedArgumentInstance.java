package xy.command.model.instance;

import java.util.Collections;
import java.util.List;

import xy.command.model.FixedArgument;

public class FixedArgumentInstance extends AbstractCommandLinePartInstance {

	public FixedArgumentInstance(FixedArgument model) {
		super(model);
	}

	public FixedArgument getModel() {
		return (FixedArgument) model;
	}

	
	@Override
	public List<String> listArgumentValues() {
		return Collections.singletonList(getModel().value);
	}
	
	@Override
	public void validate() throws Exception {
	}

}
