package xy.command.model;

import xy.command.model.instance.OptionalPartInstance;

public class OptionalPart extends ArgumentGroup {

	protected static final long serialVersionUID = 1L;
	public boolean defaultValue = false;

	@Override
	public OptionalPartInstance createInstance() {
		return new OptionalPartInstance(this);
	}

}
