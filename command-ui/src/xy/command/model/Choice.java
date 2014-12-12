package xy.command.model;

import java.util.Map;
import java.util.TreeMap;

import xy.command.model.instance.AbstractCommandLinePartInstance;
import xy.command.model.instance.ChoiceInstance;

public class Choice extends AbstractCommandLinePart {

	protected static final long serialVersionUID = 1L;
	public Map<String, ArgumentGroup> options = new TreeMap<String, ArgumentGroup>();

	@Override
	public AbstractCommandLinePartInstance createInstance() {
		return new ChoiceInstance(this);
	}

	

}
