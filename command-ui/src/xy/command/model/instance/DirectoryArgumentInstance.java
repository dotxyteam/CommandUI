package xy.command.model.instance;

import xy.command.model.DirectoryArgument;
import xy.command.ui.CommandLinePlayer;

public class DirectoryArgumentInstance extends FileArgumentInstance {

	public DirectoryArgumentInstance(CommandLinePlayer player, DirectoryArgument model) {
		super(player, model);
		this.value = "<path to the directory>";
	}

	public DirectoryArgument getModel() {
		return (DirectoryArgument) model;
	}

}
