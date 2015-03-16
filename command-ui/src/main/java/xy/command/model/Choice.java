package xy.command.model;

import java.io.IOException;
import java.io.Writer;
import java.util.Map;
import java.util.TreeMap;

import xy.command.model.instance.AbstractCommandLinePartInstance;
import xy.command.model.instance.ChoiceInstance;
import xy.reflect.ui.info.annotation.Documentation;

public class Choice extends AbstractCommandLinePart {

	protected static final long serialVersionUID = 1L;
	public Map<String, ArgumentGroup> options = new TreeMap<String, ArgumentGroup>();
	
	@Documentation("This title will identify the current element")
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
	


}
