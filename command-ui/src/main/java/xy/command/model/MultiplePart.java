package xy.command.model;

import xy.command.model.instance.MultiplePartInstance;

public class MultiplePart extends ArgumentGroup{

	protected static final long serialVersionUID = 1L;
	
	@Override
	public MultiplePartInstance createInstance() {
		return new MultiplePartInstance(this);
	}
}
