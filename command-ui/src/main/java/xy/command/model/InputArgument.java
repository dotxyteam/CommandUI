package xy.command.model;

import java.io.IOException;
import java.io.Writer;

import xy.command.instance.AbstractCommandLinePartInstance;
import xy.command.instance.InputArgumentInstance;
import xy.command.ui.util.ValidationError;

public class InputArgument extends AbstractCommandLinePart {

	protected  static final long serialVersionUID = 1L;

	// @OnlineHelp("This title will identify the current element")
	public String title;
	
	// @OnlineHelp("This value will provided by default")
	public String defaultValue;


	@Override
	public String toString() {
		return "<" + title + ">";
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
	
	// @Validating
	@Override
	public void validate() throws Exception {
		if ((title == null) || (title.trim().length() == 0)) {
			throw new ValidationError("Enter the title");
		}
	}

	@Override
	public AbstractCommandLinePartInstance instanciate() {
		return new InputArgumentInstance(this);
	}


}
