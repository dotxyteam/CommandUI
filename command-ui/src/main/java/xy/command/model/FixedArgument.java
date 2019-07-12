package xy.command.model;

import java.io.IOException;
import java.io.Writer;

import xy.command.instance.AbstractCommandLinePartInstance;
import xy.command.instance.FixedARgumentInstance;
import xy.command.ui.util.ValidationError;

public class FixedArgument extends AbstractCommandLinePart{

	protected  static final long serialVersionUID = 1L;
	
	// @OnlineHelp("This is the fixed argument value")
	public String value;


	@Override
	public String toString() {
		return value;
	}
	
	@Override
	public void writetUsageText(Writer out) throws IOException {
		out.write(value);
	}
	
	// @Validating
	@Override
	public void validate() throws Exception {
		if ((value == null) || (value.trim().length() == 0)) {
			throw new ValidationError("Enter the value");
		}
	}

	@Override
	public AbstractCommandLinePartInstance instanciate() {
		return new FixedARgumentInstance(this);
	}


}
