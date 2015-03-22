package xy.command.ui.util;

public class ValidationError extends Exception{

	private static final long serialVersionUID = 1L;

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
