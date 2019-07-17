package xy.command.model;

import java.io.IOException;
import java.io.Writer;

import xy.command.instance.AbstractCommandLinePartInstance;
import xy.command.instance.MultiplePartInstance;


public class MultiplePart extends CommandLine{

	protected  static final long serialVersionUID = 1L;
	
	
	@Override
	public void writetUsageText(Writer out) throws IOException {
		out.write("(");
		super.writetUsageText(out);
		out.write(" ...)");		
	}

	@Override
	public AbstractCommandLinePartInstance instanciate() {
		return new MultiplePartInstance(this);
	}

	@Override
	public String toString() {
		return "("+title+")";
	}

}
