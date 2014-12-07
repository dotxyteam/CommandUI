package xy.command.model.instance;

import java.util.Collections;
import java.util.List;

import xy.command.model.FileArgument;
import xy.command.ui.CommandLinePlayer;

public class FileArgumentInstance extends AbstractCommandLinePartInstance {

	public String value = "<path to the file>";
	
	public FileArgumentInstance(CommandLinePlayer player, FileArgument model) {
		super(player, model);
	}
	
	

	public FileArgument getModel() {
		return (FileArgument) model;
	}

	@Override
	public List<String> listArgumentValues() {
		return Collections.singletonList(value);
	}

}
