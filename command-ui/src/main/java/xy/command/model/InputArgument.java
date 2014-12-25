package xy.command.model;

import xy.command.model.instance.InputArgumentInstance;

public class InputArgument extends AbstractCommandLinePart {

	protected static final long serialVersionUID = 1L;

	@Override
	public InputArgumentInstance createInstance() {
		return new InputArgumentInstance(this);
	}


}
