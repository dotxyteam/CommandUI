package xy.command.model;

import java.io.IOException;
import java.io.Writer;

import xy.command.model.instance.OptionalPartInstance;

public class OptionalPart extends ArgumentGroup {

	protected static final long serialVersionUID = 1L;
	public boolean activeByDefault = false;

	@Override
	public OptionalPartInstance createInstance() {
		return new OptionalPartInstance(this);
	}
	
	@Override
	public void writetUsageText(Writer out) throws IOException {
		out.write("[");
		super.writetUsageText(out);
		out.write("]");		
	}

}
