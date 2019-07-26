package xy.command.instance;

import java.util.Arrays;

import xy.command.model.MultiplePart;

public class MultiplePartInstance extends AbstractCommandLinePartInstance {

	public MultiplePart model;
	public ArgumentGroupInstance[] argumentGroupInstances = new ArgumentGroupInstance[0];

	public MultiplePartInstance(MultiplePart model) {
		this.model = model;
	}

	@Override
	public String getExecutionText() {
		StringBuilder result = new StringBuilder();
		int i = 0;
		for (ArgumentGroupInstance instance : argumentGroupInstances) {
			if (i > 0) {
				result.append(" ");
			}
			result.append(instance.getExecutionText());
			i++;
		}
		return result.toString();
	}

	@Override
	public String toString() {
		return model.title + "=" + Arrays.toString(argumentGroupInstances);
	}
}
