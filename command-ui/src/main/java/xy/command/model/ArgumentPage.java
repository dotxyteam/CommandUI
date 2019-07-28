package xy.command.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import xy.command.instance.ArgumentPageInstance;
import xy.command.ui.util.ValidationError;

public class ArgumentPage implements Serializable {

	protected static final long serialVersionUID = 1L;

	// @OnlineHelp("This title will identify the current element")
	public String title = "Settings";

	public List<AbstractCommandLinePart> parts = new ArrayList<AbstractCommandLinePart>();

	@Override
	public String toString() {
		return  title;
	}


	// @Validating
	public void validate() throws Exception {
		if ((title == null) || (title.trim().length() == 0)) {
			throw new ValidationError("Enter the title");
		}
	}

	public ArgumentPageInstance instanciate() {
		return new ArgumentPageInstance(this);
	}
}
