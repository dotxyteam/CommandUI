package xy.command.instance;

/**
 * The base class of command line model instance parts.
 * 
 * @author olitank
 *
 */
public abstract class AbstractCommandLinePartInstance {

	/**
	 * @return The resulting text argument that should be used to execute the
	 *         command line program.
	 */
	public abstract String getExecutionText();

}
