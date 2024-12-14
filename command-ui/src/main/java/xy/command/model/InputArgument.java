package xy.command.model;

import java.io.IOException;
import java.io.Writer;

import xy.command.instance.AbstractCommandLinePartInstance;
import xy.command.instance.InputArgumentInstance;
import xy.command.ui.util.ValidationError;

/**
 * A text input command line model part.
 * 
 * @author olitank
 *
 */
public class InputArgument extends AbstractCommandLinePart {

	protected static final long serialVersionUID = 1L;

	/**
	 * The title of this command line model part.
	 */
	public String title = "";

	/**
	 * This value will provided by default.
	 */
	public String defaultValue = "";

	@Override
	public String toString() {
		return title;
	}

	@Override
	public void writetUsageText(Writer out) throws IOException {
		out.write("<");
		if ((title == null) || (title.trim().length() == 0)) {
			out.write("arg");
		} else {
			out.write(title.replaceAll("\\s", "_"));
		}
		out.write(">");
	}

	@Override
	public void validate() throws Exception {
		if ((title == null) || (title.trim().length() == 0)) {
			throw new ValidationError("Enter the title");
		}
	}

	@Override
	public AbstractCommandLinePartInstance instantiate() {
		return new InputArgumentInstance(this);
	}

}
