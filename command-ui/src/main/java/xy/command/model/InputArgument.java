package xy.command.model;

import xy.command.model.instance.InputArgumentInstance;

public class InputArgument extends AbstractCommandLinePart {

	protected static final long serialVersionUID = 1L;
	public String title;
	public String defaultValue;
	
	@Override
	public InputArgumentInstance createInstance() {
		return new InputArgumentInstance(this);
	}

	@Override
	public String toString() {
		return "<"+title+">";
	}
	

}
