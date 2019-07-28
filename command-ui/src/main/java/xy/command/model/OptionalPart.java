package xy.command.model;

import java.io.IOException;
import java.io.Writer;

import xy.command.instance.AbstractCommandLinePartInstance;
import xy.command.instance.OptionalPartInstance;
import xy.command.ui.util.ValidationError;

public class OptionalPart extends ArgumentGroup {

	protected static final long serialVersionUID = 1L;

	// @OnlineHelp("If true, the option will be selected by default")
	public boolean activeByDefault = false;

	// @Validating
	@Override
	public void validate() throws Exception {
		if ((title == null) || (title.trim().length() == 0)) {
			throw new ValidationError("Enter the title");
		}
	}

	@Override
	public void writetUsageText(Writer out) throws IOException {
		out.write("[");
		super.writetUsageText(out);
		out.write("]");
	}

	@Override
	public AbstractCommandLinePartInstance instanciate() {
		return new OptionalPartInstance(this);
	}

	@Override
	public String toString() {
		return  title;
	}


}
