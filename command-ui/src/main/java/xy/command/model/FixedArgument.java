package xy.command.model;

import java.io.IOException;
import java.io.Writer;

import xy.command.model.instance.FixedArgumentInstance;
import xy.command.ui.util.ValidationError;
import xy.reflect.ui.info.annotation.OnlineHelp;
import xy.reflect.ui.info.annotation.Validating;

public class FixedArgument extends AbstractCommandLinePart{

	protected static final long serialVersionUID = 1L;
	
	@OnlineHelp("This is the fixed argument value")
	public String value;

	@Override
	public FixedArgumentInstance createInstance() {
		return new FixedArgumentInstance(this);
	}

	@Override
	public String toString() {
		return value;
	}
	
	@Override
	public void writetUsageText(Writer out) throws IOException {
		out.write(value);
	}
	
	@Validating
	@Override
	public void validate() throws Exception {
		if ((value == null) || (value.trim().length() == 0)) {
			throw new ValidationError("Enter the value");
		}
	}


}
