package xy.command.model.instance;

import java.util.ArrayList;
import java.util.List;

import xy.command.model.AbstractCommandLinePart;
import xy.command.model.ArgumentPage;

public class ArgumentPageInstance {

	public List<AbstractCommandLinePartInstance> partInstances = new ArrayList<AbstractCommandLinePartInstance>();
	protected ArgumentPage model;
	
	public ArgumentPageInstance(ArgumentPage model) {
		this.model = model;
		for (AbstractCommandLinePart part : model.parts) {
			partInstances.add(part.createInstance());
		}
	}

	public ArgumentPage getModel() {
		return model;
	}

	public List<String> listArgumentValues(){
		List<String> result = new ArrayList<String>();
		for (AbstractCommandLinePartInstance part : partInstances) {
			result.addAll(part.listArgumentValues());
		}
		return result;
	}

}
