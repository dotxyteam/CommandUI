package xy.command.model;

import java.io.Serializable;

import xy.command.model.instance.AbstractCommandLinePartInstance;

public abstract class AbstractCommandLinePart implements Serializable {

	protected static final long serialVersionUID = 1L;
	public String title;
	public String documentation;

	public abstract AbstractCommandLinePartInstance createInstance();
}
