package xy.command.instance;

import java.util.ArrayList;
import java.util.List;

import xy.command.model.AbstractCommandLinePart;
import xy.command.model.ArgumentGroup;

public class ArgumentGroupInstance extends AbstractCommandLinePartInstance {

	public ArgumentGroup model;

	public ArgumentGroupInstance(ArgumentGroup model) {
		this.model = model;
	}

	public List<AbstractCommandLinePartInstance> getPartInstances() {
		List<AbstractCommandLinePartInstance> result = new ArrayList<AbstractCommandLinePartInstance>();
		for (AbstractCommandLinePart part : model.parts) {
			AbstractCommandLinePartInstance instance = part.instanciate();
			if (instance == null) {
				continue;
			}
			result.add(instance);
		}
		return result;
	}

}
