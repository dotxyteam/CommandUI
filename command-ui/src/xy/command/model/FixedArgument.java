package xy.command.model;

import xy.command.model.instance.FixedArgumentInstance;
import xy.command.ui.CommandLinePlayer;

public class FixedArgument extends AbstractCommandLinePart{

	protected static final long serialVersionUID = 1L;
	public String value;

	@Override
	public FixedArgumentInstance createInstance(CommandLinePlayer player) {
		return new FixedArgumentInstance(player, this);
	}


}
