package xy.command.model.instance;

import java.util.ArrayList;
import java.util.List;

import xy.command.model.AbstractCommandLinePart;
import xy.command.model.ArgumentGroup;
import xy.command.model.ArgumentGroup.Cardinality;

public class ArgumentGroupInstance extends AbstractCommandLinePartInstance {

	public List<List<AbstractCommandLinePartInstance>> multiPartInstances;
	public ArgumentGroupInstance(ArgumentGroup model) {
		super(model);
		multiPartInstances = new ArrayList<List<AbstractCommandLinePartInstance>>();
		if (model.cardinality == Cardinality.ONE) {
			multiPartInstances.add(newPartInstances());
		}
	}
	
	

	public ArgumentGroup getModel() {
		return (ArgumentGroup) model;
	}



	protected List<AbstractCommandLinePartInstance> newPartInstances() {
		List<AbstractCommandLinePartInstance> result = new ArrayList<AbstractCommandLinePartInstance>();
		for (AbstractCommandLinePart part : getModel().parts) {
			result.add(part.createInstance());
		}
		return result;
	}

	@Override
	public List<String> listArgumentValues() {
		List<String> result = new ArrayList<String>();
		for (List<AbstractCommandLinePartInstance> partInstances : multiPartInstances) {
			for (AbstractCommandLinePartInstance part : partInstances) {
				result.addAll(part.listArgumentValues());
			}
		}
		return result;
	}

}
