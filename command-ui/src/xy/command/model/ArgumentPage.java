package xy.command.model;

import java.util.ArrayList;
import java.util.List;

import xy.command.model.instance.ArgumentPageInstance;

public class ArgumentPage {

	public String title;
	public List<AbstractCommandLinePart> parts = new ArrayList<AbstractCommandLinePart>();

	public ArgumentPageInstance createInstance() {
		return new ArgumentPageInstance(this);
	}
	
}
