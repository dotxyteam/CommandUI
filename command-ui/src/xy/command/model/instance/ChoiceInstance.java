package xy.command.model.instance;

import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import xy.command.model.Choice;
import xy.command.model.ArgumentGroup;
import xy.command.ui.CommandLinePlayer;

public class ChoiceInstance extends AbstractCommandLinePartInstance {

	public Map<String, ArgumentGroupInstance> optionInstances = new TreeMap<String, ArgumentGroupInstance>();
	public String value;
	
	public ChoiceInstance(CommandLinePlayer player, Choice model) {
		super(player, model);
		for (Map.Entry<String, ArgumentGroup> modelEntry : model.options.entrySet()) {
			optionInstances.put(modelEntry.getKey(), modelEntry.getValue()
					.createInstance(player));
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
