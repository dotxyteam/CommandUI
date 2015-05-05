package xy.command.model.instance;

import java.util.ArrayList;
import java.util.List;
import xy.command.model.ArgumentGroup;
import xy.command.model.Choice;
import xy.command.ui.util.ValidationError;

public class ChoiceInstance extends AbstractCommandLinePartInstance {

	public List<ArgumentGroupInstance> optionInstances = new ArrayList<ArgumentGroupInstance>();
	public int chosenOption = -1;

	public ChoiceInstance(Choice model) {
		super(model);
		for (ArgumentGroup modelEntry : model.options) {
			optionInstances.add(modelEntry.createInstance());
		}
	}

	public Choice getModel() {
		return (Choice) model;
	}

	@Override
	public List<String> listArgumentValues() {
		return optionInstances.get(chosenOption).listArgumentValues();
	}

	@Override
	public void validate() throws Exception {
		try {
			if (chosenOption == -1) {
				throw new ValidationError("Choose an option");
			}
			ArgumentGroupInstance chosen = optionInstances.get(chosenOption);
			for (AbstractCommandLinePartInstance partInstance : chosen.partInstances) {
				partInstance.validate();
			}
		} catch (Exception e) {
			throw contextualizeFieldValidationError(e, ((Choice) model).title);
		}

	}

}
