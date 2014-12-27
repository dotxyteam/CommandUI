package xy.command.model.instance;

import java.util.ArrayList;
import java.util.List;

import xy.command.model.AbstractCommandLinePart;
import xy.command.model.ArgumentPage;
import xy.command.model.CommandLine;
import xy.command.ui.util.CommandUIUtils;

public class CommandLineInstance {

	public List<AbstractCommandLinePartInstance> partInstances = new ArrayList<AbstractCommandLinePartInstance>();
	protected CommandLine model;
	
	public CommandLineInstance(CommandLine model) {
		this.model = model;
		for (ArgumentPage page : model.arguments) {
			for(AbstractCommandLinePart part: page.parts){
				partInstances.add(part.createInstance());
			}
		}
	}

	public CommandLine getModel() {
		return model;
	}

	public String getCommandlineString(){
		return model.executablePath + " " + CommandUIUtils.formatArgumentList(listArgumentValues());
	}
	
	public List<String> listArgumentValues(){
		List<String> result = new ArrayList<String>();
		for (AbstractCommandLinePartInstance part : partInstances) {
			result.addAll(part.listArgumentValues());
		}
		return result;
	}


}
