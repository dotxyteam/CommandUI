package xy.command.model;

import java.io.IOException;
import java.io.Writer;
import java.util.Map;
import java.util.TreeMap;

import xy.command.model.instance.AbstractCommandLinePartInstance;
import xy.command.model.instance.ChoiceInstance;
import xy.command.ui.util.ValidationError;
import xy.reflect.ui.info.annotation.OnlineHelp;
import xy.reflect.ui.info.annotation.Validating;

public class Choice extends AbstractCommandLinePart {

	protected static final long serialVersionUID = 1L;
	public Map<String, ArgumentGroup> options = new TreeMap<String, ArgumentGroup>();
	
	@OnlineHelp("This title will identify the current element")
	public String title;
	
	@Override
	public AbstractCommandLinePartInstance createInstance() {
		return new ChoiceInstance(this);
	}

	@Override
	public String toString() {
		return "("+title+")";
	}

	@Override
	public void writetUsageText(Writer out) throws IOException {
		out.append("(");
		boolean first = true;
		for(Map.Entry<String, ArgumentGroup> optionEntry: options.entrySet()){
			if(!first){
				out.append(" | ");
			}
			optionEntry.getValue().writetUsageText(out);
			first = false;
		}
		out.append(")");
	}
	

	@Validating
	@Override
	public void validate() throws Exception {
		if ((title == null) || (title.trim().length() == 0)) {
			throw new ValidationError("Enter the title");
		}
	}


}
