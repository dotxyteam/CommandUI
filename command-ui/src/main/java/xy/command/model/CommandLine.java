package xy.command.model;

import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;

import xy.command.instance.CommandLineInstance;
import xy.command.ui.util.ValidationError;

public class CommandLine extends AbstractCommandLinePart{

	private static final long serialVersionUID = 1L;

	// @OnlineHelp("Title of the generated command line GUI")
	public String title = "";

	// @OnlineHelp("The list of arguments of the command line")
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

	// @Validating
	public void validate() throws Exception {
		if ((title == null) || (title.trim().length() == 0)) {
			throw new ValidationError("Enter the title");
		}
	}

	public CommandLineInstance instanciate() {
		return new CommandLineInstance(this);
	}

	
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
		return  title;
	}


}
