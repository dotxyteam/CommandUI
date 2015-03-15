package xy.command.model;

import xy.command.model.instance.FixedArgumentInstance;

public class FixedArgument extends AbstractCommandLinePart{

	protected static final long serialVersionUID = 1L;
	public String value;

	@Override
	public FixedArgumentInstance createInstance() {
		return new FixedArgumentInstance(this);
	}

	@Override
	public String toString() {
		return value;
	}
	

}
