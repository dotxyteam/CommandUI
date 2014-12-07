package xy.command.model.instance;

import java.util.List;

import xy.command.model.AbstractCommandLinePart;
import xy.command.ui.CommandLinePlayer;

public abstract class AbstractCommandLinePartInstance {
	
	protected CommandLinePlayer player;
	protected AbstractCommandLinePart model;	

	public abstract List<String> listArgumentValues();

	protected AbstractCommandLinePartInstance(CommandLinePlayer player,
			AbstractCommandLinePart model) {
		this.player = player;
		this.model = model;
	}

}
