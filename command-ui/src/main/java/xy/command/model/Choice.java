package xy.command.model;

import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;

import xy.command.instance.AbstractCommandLinePartInstance;
import xy.command.instance.ChoiceInstance;
import xy.command.ui.util.ValidationError;

public class Choice extends AbstractCommandLinePart {

	protected  static final long serialVersionUID = 1L;
	
	public List<ArgumentGroup> options = new ArrayList<ArgumentGroup>();
	
	// @OnlineHelp("This title will identify the current element")
	public String title = "";
	

	@Override
	public String toString() {
		return  title;
	}


	@Override
	public void writetUsageText(Writer out) throws IOException {
		out.append("(");
		boolean first = true;
		for(ArgumentGroup optionEntry: options){
			if(!first){
				out.append(" | ");
			}
			optionEntry.writetUsageText(out);
			first = false;
		}
		out.append(")");
	}
	

	// @Validating
	@Override
	public void validate() throws Exception {
		if ((title == null) || (title.trim().length() == 0)) {
			throw new ValidationError("Enter the title");
		}
	}

	@Override
	public AbstractCommandLinePartInstance instanciate() {
		return new ChoiceInstance(this);
	}


}
