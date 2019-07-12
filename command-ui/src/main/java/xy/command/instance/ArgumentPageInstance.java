package xy.command.instance;

import java.util.ArrayList;
import java.util.List;

import xy.command.model.AbstractCommandLinePart;
import xy.command.model.ArgumentPage;

public class ArgumentPageInstance {

	public ArgumentPage model;
	public List<AbstractCommandLinePartInstance> partInstances = new ArrayList<AbstractCommandLinePartInstance>();

	public ArgumentPageInstance(ArgumentPage model) {
		this.model = model;
		for (AbstractCommandLinePart part : model.parts) {
			AbstractCommandLinePartInstance instance = part.instanciate();
			if (instance == null) {
				continue;
			}
			partInstances.add(instance);
		}
	}

	@Override
	public String toString() {
		return model.title;
	}

}
