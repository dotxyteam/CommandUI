package xy.command.model;

import java.util.ArrayList;
import java.util.List;

import xy.command.model.instance.ArgumentGroupInstance;

public class ArgumentGroup extends AbstractCommandLinePart {

	protected static final long serialVersionUID = 1L;
	public List<AbstractCommandLinePart> parts = new ArrayList<AbstractCommandLinePart>();
	public String title;
	
	@Override
	public ArgumentGroupInstance createInstance() {
		return new ArgumentGroupInstance(this);
	}

	
	@Override
	public String toString() {
		return "("+title+")";
	}
	


}
