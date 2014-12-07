package xy.command.model;

import java.io.Serializable;

import xy.command.model.instance.AbstractCommandLinePartInstance;
import xy.command.ui.CommandLinePlayer;

public abstract class AbstractCommandLinePart implements Serializable{

	protected static final long serialVersionUID = 1L;
	public String title;
	public abstract AbstractCommandLinePartInstance createInstance(CommandLinePlayer player);
}
