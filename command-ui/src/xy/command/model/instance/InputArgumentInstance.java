package xy.command.model.instance;

import java.util.Collections;
import java.util.List;

import xy.command.model.InputArgument;
import xy.command.ui.CommandLinePlayer;

public class InputArgumentInstance extends AbstractCommandLinePartInstance {

	public String title;
	public String value;
	
	public InputArgumentInstance(CommandLinePlayer player, InputArgument model) {
		super(player, model);
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
