package xy.command.model;

import java.io.IOException;
import java.io.Writer;

import xy.command.instance.AbstractCommandLinePartInstance;
import xy.command.instance.FixedArgumentInstance;
import xy.command.ui.util.ValidationError;

/**
 * A fixed command line model part.
 * 
 * @author olitank
 *
 */
public class FixedArgument extends AbstractCommandLinePart {

	protected static final long serialVersionUID = 1L;

	/**
	 * This is the fixed argument value.
	 */
	public String value = "";

	@Override
	public String toString() {
		return value;
	}

	@Override
	public void writetUsageText(Writer out) throws IOException {
		out.write(value);
	}

	@Override
	public void validate() throws Exception {
		if ((value == null) || (value.trim().length() == 0)) {
			throw new ValidationError("Enter the value");
		}
	}

	@Override
	public AbstractCommandLinePartInstance instantiate() {
		return new FixedArgumentInstance(this);
	}

}
