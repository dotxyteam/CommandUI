package xy.command.model;

import java.io.IOException;
import java.io.Serializable;
import java.io.Writer;

import xy.command.instance.AbstractCommandLinePartInstance;

public abstract class AbstractCommandLinePart implements Serializable {

	protected static final long serialVersionUID = 1L;

	public abstract AbstractCommandLinePartInstance instanciate();

	public String description = "";

	public abstract void writetUsageText(Writer out) throws IOException;

	public void validate() throws Exception {
	}


}
