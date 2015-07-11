package xy.command.model;

import java.io.IOException;
import java.io.Writer;

import xy.command.model.instance.MultiplePartInstance;
import xy.reflect.ui.info.annotation.Hidden;

public class MultiplePart extends ArgumentGroup{

	protected  static final long serialVersionUID = 1L;
	
	@Hidden
	@Override
	public MultiplePartInstance createInstance() {
		return new MultiplePartInstance(this);
	}
	
	@Override
	public void writetUsageText(Writer out) throws IOException {
		out.write("(");
		super.writetUsageText(out);
		out.write(" ...)");		
	}

	
	
}
