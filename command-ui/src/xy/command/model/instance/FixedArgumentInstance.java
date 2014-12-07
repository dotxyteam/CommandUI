package xy.command.model.instance;

import java.util.Collections;
import java.util.List;

import xy.command.model.FixedArgument;
import xy.command.ui.CommandLinePlayer;

public class FixedArgumentInstance extends AbstractCommandLinePartInstance {

	public FixedArgumentInstance(CommandLinePlayer player, FixedArgument model) {
		super(player, model);
	}

	public FixedArgument getModel() {
		return (FixedArgument) model;
	}

	
	@Override
	public List<String> listArgumentValues() {
		return Collections.singletonList(getModel().value);
	}

}
