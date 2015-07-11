package xy.command.model;

import java.io.IOException;
import java.io.Writer;

import xy.command.model.instance.OptionalPartInstance;
import xy.reflect.ui.info.annotation.Hidden;
import xy.reflect.ui.info.annotation.OnlineHelp;

public class OptionalPart extends ArgumentGroup {

	protected  static final long serialVersionUID = 1L;
	
	@OnlineHelp("If true, the option will be selected by default")
	public boolean activeByDefault = false;

	@Hidden
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
