package xy.command.instance;

import java.util.ArrayList;
import java.util.List;

import xy.command.model.AbstractCommandLinePart;
import xy.command.model.ArgumentGroup;

/**
 * The instance class of {@link ArgumentGroup}.
 * 
 * @author olitank
 *
 */
public class ArgumentGroupInstance extends AbstractCommandLinePartInstance {

	public ArgumentGroup model;
	public List<AbstractCommandLinePartInstance> partInstances = new ArrayList<AbstractCommandLinePartInstance>();

	/**
	 * The main constructor. Builds an instance from the given model part object.
	 * 
	 * @param model The model part object.
	 */
	public ArgumentGroupInstance(ArgumentGroup model) {
		this.model = model;
		for (AbstractCommandLinePart part : model.parts) {
			AbstractCommandLinePartInstance instance = part.instantiate();
			if (instance == null) {
				continue;
			}
			partInstances.add(instance);
		}
	}

	@Override
	public String getExecutionText() {
		StringBuilder result = new StringBuilder();
		int i = 0;
		for (AbstractCommandLinePartInstance partInstance : partInstances) {
			if (i > 0) {
				result.append(" ");
			}
			String partInstanceExecutionText = partInstance.getExecutionText();
			if (partInstanceExecutionText == null) {
				continue;
			}
			result.append(partInstanceExecutionText);
			i++;
		}
		return result.toString();
	}

	@Override
	public String toString() {
		StringBuilder result = new StringBuilder();
		int i = 0;
		for (AbstractCommandLinePartInstance partInstance : partInstances) {
			if (i > 0) {
				result.append(", ");
			}
			result.append(partInstance.toString());
			i++;
		}
		return result.toString();
	}
}
