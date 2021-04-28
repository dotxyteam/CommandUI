package xy.command.ui.util;

/**
 * Error thrown when a command line model is not valid.
 * 
 * @author olitank
 *
 */
public class ValidationError extends Exception {

	protected static final long serialVersionUID = 1L;

	public ValidationError() {
		super();
	}

	public ValidationError(String message, Throwable cause) {
		super(message, cause);
	}

	public ValidationError(String message) {
		super(message);
	}

	public ValidationError(Throwable cause) {
		super(cause);
	}

	@Override
	public String toString() {
		return super.getMessage();
	}

}
