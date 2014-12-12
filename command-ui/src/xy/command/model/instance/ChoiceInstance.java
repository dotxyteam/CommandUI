package xy.command.model.instance;

import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import xy.command.model.ArgumentGroup;
import xy.command.model.Choice;

public class ChoiceInstance extends AbstractCommandLinePartInstance {

	public Map<String, ArgumentGroupInstance> optionInstances = new TreeMap<String, ArgumentGroupInstance>();
	public String value;
	
	public ChoiceInstance(Choice model) {
		super(model);
		for (Map.Entry<String, ArgumentGroup> modelEntry : model.options.entrySet()) {
			optionInstances.put(modelEntry.getKey(), modelEntry.getValue()
					.createInstance());
		}
	}
	
	
	
	public Choice getModel() {
		return (Choice) model;
	}
	
	@Override
	public List<String> listArgumentValues() {
		return optionInstances.get(value).listArgumentValues();
	}

}
