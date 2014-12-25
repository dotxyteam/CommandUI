package xy.command.model.instance;

import java.util.ArrayList;
import java.util.List;

import xy.command.model.AbstractCommandLinePart;
import xy.command.model.ArgumentGroup;

public class ArgumentGroupInstance extends AbstractCommandLinePartInstance {

	public List<AbstractCommandLinePartInstance> partInstances;

	public ArgumentGroupInstance(ArgumentGroup model) {
		super(model);
		partInstances = new ArrayList<AbstractCommandLinePartInstance>();
		for (AbstractCommandLinePart part : getModel().parts) {
			partInstances.add(part.createInstance());
		}

	}

	public ArgumentGroup getModel() {
		return (ArgumentGroup) model;
	}

	@Override
	public List<String> listArgumentValues() {
		List<String> result = new ArrayList<String>();
		for (AbstractCommandLinePartInstance part : partInstances) {
			result.addAll(part.listArgumentValues());
		}
		return result;
	}

}
