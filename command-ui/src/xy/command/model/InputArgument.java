package xy.command.model;

import xy.command.model.instance.InputArgumentInstance;
import xy.command.ui.CommandLinePlayer;

public class InputArgument extends AbstractCommandLinePart {

	protected static final long serialVersionUID = 1L;

	@Override
	public InputArgumentInstance createInstance(CommandLinePlayer player) {
		return new InputArgumentInstance(player, this);
	}


}
