package xy.command.model;

import java.io.IOException;
import java.io.Writer;

import xy.command.instance.AbstractCommandLinePartInstance;
import xy.command.instance.MultiplePartInstance;
import xy.command.ui.util.ValidationError;

/**
 * A collection of command line model parts that can be instanciated multiple
 * times.
 * 
 * @author olitank
 *
 */
public class MultiplePart extends ArgumentGroup {

	protected static final long serialVersionUID = 1L;

	@Override
	public void validate() throws Exception {
		if ((title == null) || (title.trim().length() == 0)) {
			throw new ValidationError("Enter the title");
		}
	}

	@Override
	public void writetUsageText(Writer out) throws IOException {
		out.write("(");
		super.writetUsageText(out);
		out.write(" ...)");
	}

	@Override
	public AbstractCommandLinePartInstance instanciate() {
		return new MultiplePartInstance(this);
	}

	@Override
	public String toString() {
		return title;
	}

}
