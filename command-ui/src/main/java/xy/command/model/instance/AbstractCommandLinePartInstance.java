package xy.command.model.instance;

import java.util.List;

import xy.command.model.AbstractCommandLinePart;

public abstract class AbstractCommandLinePartInstance {
	
	protected AbstractCommandLinePart model;	

	public abstract List<String> listArgumentValues();

	protected AbstractCommandLinePartInstance(
			AbstractCommandLinePart model) {
		this.model = model;
	}

}
