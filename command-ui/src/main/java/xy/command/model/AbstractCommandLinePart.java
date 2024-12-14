package xy.command.model;

import java.io.IOException;
import java.io.Serializable;
import java.io.Writer;

import xy.command.instance.AbstractCommandLinePartInstance;

/**
 * The base class of command line model parts.
 * 
 * @author olitank
 *
 */
public abstract class AbstractCommandLinePart implements Serializable {

	protected static final long serialVersionUID = 1L;

	/**
	 * @return An instance of this command line model part.
	 */
	public abstract AbstractCommandLinePartInstance instantiate();

	/**
	 * The description of this command line model part.
	 */
	public String description = "";

	/**
	 * Writes a description of this command line model part in a syntax close to the
	 * POSIX Utility Argument Syntax.
	 * 
	 * @param out The writer that will receive the description.
	 * @throws IOException If thrown by the writer.
	 */
	public abstract void writetUsageText(Writer out) throws IOException;

	/**
	 * Allows to validate the correctness of the properties of this command line
	 * model part.
	 * 
	 * @throws Exception If a property is not valid.
	 */
	public void validate() throws Exception {
	}

}
