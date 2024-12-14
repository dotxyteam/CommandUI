package xy.command.model;

import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;

import xy.command.instance.CommandLineInstance;
import xy.command.ui.util.ValidationError;

/**
 * The command line model class. Allows to specify the structure of the argument
 * list of any command line program.
 * 
 * @author olitank
 *
 */
public class CommandLine extends AbstractCommandLinePart {

	private static final long serialVersionUID = 1L;

	/**
	 * Title of the generated command line GUI.
	 */
	public String title = "";

	/**
	 * The list of argument pages.
	 */
	public List<ArgumentPage> arguments = new ArrayList<ArgumentPage>();

	@Override
	public void writetUsageText(Writer out) throws IOException {
		boolean first = true;
		for (ArgumentPage page : arguments) {
			for (AbstractCommandLinePart part : page.parts) {
				if (!first) {
					out.append(" ");
				}
				try {
					part.writetUsageText(out);
				} catch (IOException e) {
					throw new AssertionError(e);
				}
				first = false;
			}
		}
	}

	/**
	 * Allows to validate the correctness of the properties of this command line
	 * model.
	 * 
	 * @throws Exception If a property is not valid.
	 */
	public void validate() throws Exception {
		if ((title == null) || (title.trim().length() == 0)) {
			throw new ValidationError("Enter the title");
		}
	}

	/**
	 * @return An instance of this command line model.
	 */
	public CommandLineInstance instantiate() {
		return new CommandLineInstance(this);
	}

	/**
	 * @return A description of this command line model in a syntax close to the
	 *         POSIX Utility Argument Syntax.
	 */
	public String getUsageText() {
		StringWriter out = new StringWriter();
		try {
			writetUsageText(out);
		} catch (Exception e2) {
			return "";
		}
		return out.toString();
	}

	@Override
	public String toString() {
		return title;
	}

}
