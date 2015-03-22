package xy.command.model.instance;

import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import xy.command.model.ArgumentGroup;
import xy.command.model.Choice;
import xy.command.ui.util.ValidationError;

public class ChoiceInstance extends AbstractCommandLinePartInstance {

	public Map<String, ArgumentGroupInstance> optionInstances = new TreeMap<String, ArgumentGroupInstance>();
	public String value;

	public ChoiceInstance(Choice model) {
		super(model);
		for (Map.Entry<String, ArgumentGroup> modelEntry : model.options
				.entrySet()) {
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

	@Override
	public void validate() throws Exception {
		try {
			if ((value == null) || (value.trim().length() == 0)) {
				throw new ValidationError("Choose an option");
			}
			ArgumentGroupInstance chosen = optionInstances.get(value);
			for (AbstractCommandLinePartInstance partInstance : chosen.partInstances) {
				partInstance.validate();
			}
		} catch (Exception e) {
			throw contextualizeFieldValidationError(e, ((Choice) model).title);
		}

	}

}
