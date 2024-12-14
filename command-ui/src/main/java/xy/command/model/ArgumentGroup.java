package xy.command.model;

import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;

import xy.command.instance.AbstractCommandLinePartInstance;
import xy.command.instance.ArgumentGroupInstance;

/**
 * A collection of command line model parts.
 * 
 * @author olitank
 *
 */
public class ArgumentGroup extends AbstractCommandLinePart {

	private static final long serialVersionUID = 1L;

	/**
	 * The title of this command line model part.
	 */
	public String title = "";
	
	/**
	 * The children command line model parts.
	 */
	public List<AbstractCommandLinePart> parts = new ArrayList<AbstractCommandLinePart>();

	@Override
	public AbstractCommandLinePartInstance instantiate() {
		return new ArgumentGroupInstance(this);
	}

	@Override
	public void writetUsageText(Writer out) throws IOException {
		boolean first = true;
		for (AbstractCommandLinePart part : parts) {
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

	@Override
	public String toString() {
		return title;
	}

}
