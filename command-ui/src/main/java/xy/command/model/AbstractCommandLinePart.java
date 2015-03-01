package xy.command.model;

import java.io.Serializable;

import xy.command.model.instance.AbstractCommandLinePartInstance;

public abstract class AbstractCommandLinePart implements Serializable {

	protected static final long serialVersionUID = 1L;
	public String title;
	public String description;

	public abstract AbstractCommandLinePartInstance createInstance();

	@Override
	public String toString() {
		return title;
	}
	
	
}
