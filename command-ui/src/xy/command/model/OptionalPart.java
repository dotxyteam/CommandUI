package xy.command.model;

import xy.command.model.instance.OptionalPartInstance;
import xy.command.ui.CommandLinePlayer;

public class OptionalPart extends ArgumentGroup {

	protected static final long serialVersionUID = 1L;
	public boolean defaultValue = false;

	@Override
	public OptionalPartInstance createInstance(CommandLinePlayer player) {
		return new OptionalPartInstance(player, this);
	}

}
