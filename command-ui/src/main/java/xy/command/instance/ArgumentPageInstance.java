package xy.command.instance;

import java.util.ArrayList;
import java.util.List;

import xy.command.model.AbstractCommandLinePart;
import xy.command.model.ArgumentPage;

/**
 * The instance class of {@link ArgumentPage}.
 * 
 * @author olitank
 *
 */
public class ArgumentPageInstance {

	public ArgumentPage model;
	public List<AbstractCommandLinePartInstance> partInstances = new ArrayList<AbstractCommandLinePartInstance>();

	/**
	 * The main constructor. Builds an instance from the given model page object.
	 * 
	 * @param model The model page object.
	 */
	public ArgumentPageInstance(ArgumentPage model) {
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
	public String toString() {
		return model.title;
	}

}
