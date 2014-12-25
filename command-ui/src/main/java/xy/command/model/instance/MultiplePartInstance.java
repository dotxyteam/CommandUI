package xy.command.model.instance;

import java.util.ArrayList;
import java.util.List;

import xy.command.model.AbstractCommandLinePart;
import xy.command.model.ArgumentGroup;
import xy.command.model.MultiplePart;

public class MultiplePartInstance extends ArgumentGroupInstance {

	public List<List<AbstractCommandLinePartInstance>> multiPartInstances;
	
	public MultiplePartInstance(MultiplePart model) {
		super(model);
		multiPartInstances = new ArrayList<List<AbstractCommandLinePartInstance>>();
	}
	
	

	public ArgumentGroup getModel() {
		return (ArgumentGroup) model;
	}



	public List<AbstractCommandLinePartInstance> addOccurrence() {
		List<AbstractCommandLinePartInstance> occurrence = new ArrayList<AbstractCommandLinePartInstance>();
		for (AbstractCommandLinePart part : getModel().parts) {
			occurrence.add(part.createInstance());
		}
		multiPartInstances.add(occurrence);
		return occurrence;
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
