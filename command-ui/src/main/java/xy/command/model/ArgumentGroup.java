package xy.command.model;

import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;

import xy.command.model.instance.ArgumentGroupInstance;
import xy.command.ui.util.ValidationError;
import xy.reflect.ui.info.annotation.Documentation;
import xy.reflect.ui.info.annotation.Validating;

public class ArgumentGroup extends AbstractCommandLinePart {

	protected static final long serialVersionUID = 1L;
	
	public List<AbstractCommandLinePart> parts = new ArrayList<AbstractCommandLinePart>();
	
	@Documentation("This title will identify the current element")
	public String title;

	@Override
	public ArgumentGroupInstance createInstance() {
		return new ArgumentGroupInstance(this);
	}

	@Override
	public String toString() {
		return "(" + title + ")";
	}

	@Override
	public void writetUsageText(Writer out) throws IOException {
		boolean first = true;
		for (AbstractCommandLinePart part : parts) {
			if (!first) {
				out.append(" ");
			}
			part.writetUsageText(out);
			first = false;
		}
	}
	
	@Validating
	@Override
	public void validate() throws Exception {
		if ((title == null) || (title.trim().length() == 0)) {
			throw new ValidationError("Enter the title");
		}
	}

}
