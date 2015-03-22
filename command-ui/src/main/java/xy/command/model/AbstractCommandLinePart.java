package xy.command.model;

import java.io.IOException;
import java.io.Serializable;
import java.io.Writer;

import xy.command.model.instance.AbstractCommandLinePartInstance;
import xy.reflect.ui.info.annotation.Documentation;
import xy.reflect.ui.info.annotation.Validating;

public abstract class AbstractCommandLinePart implements Serializable {

	protected static final long serialVersionUID = 1L;
	
	@Documentation("Specifies the description of the current element")
	public String description;

	public abstract AbstractCommandLinePartInstance createInstance();

	public abstract void writetUsageText(Writer out) throws IOException;
	


	
	@Validating
	public void validate() throws Exception {
	}
	
}
