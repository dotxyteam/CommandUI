package xy.command.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import xy.command.instance.ArgumentPageInstance;
import xy.command.ui.util.ValidationError;

/**
 * A collection of command line model parts that should be displayed together.
 * 
 * @author olitank
 *
 */
public class ArgumentPage implements Serializable {

	protected static final long serialVersionUID = 1L;

	/**
	 * The title of this command line model part.
	 */
	public String title = "Settings";

	/**
	 * The children command line model parts.
	 */
	public List<AbstractCommandLinePart> parts = new ArrayList<AbstractCommandLinePart>();

	@Override
	public String toString() {
		return title;
	}

	/**
	 * Allows to validate the correctness of the properties of this command line
	 * model page.
	 * 
	 * @throws Exception If a property is not valid.
	 */
	public void validate() throws Exception {
		if ((title == null) || (title.trim().length() == 0)) {
			throw new ValidationError("Enter the title");
		}
	}

	/**
	 * @return An instance of this command line model page.
	 */
	public ArgumentPageInstance instanciate() {
		return new ArgumentPageInstance(this);
	}
}
