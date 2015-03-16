package xy.command.model;

import java.io.IOException;
import java.io.Writer;

import javax.swing.JFileChooser;

import xy.command.model.instance.FileArgumentInstance;

public class FileArgument extends AbstractCommandLinePart {

	protected static final long serialVersionUID = 1L;
	public String title;
	public String defaultValue;

	@Override
	public FileArgumentInstance createInstance() {
		return new FileArgumentInstance(this);
	}


	public void configureFileChooser(JFileChooser fileChooser) {
		fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
	}

	@Override
	public String toString() {
		return "<"+title+">";
	}


	@Override
	public void writetUsageText(Writer out) throws IOException {
		out.write("<");
		if((title == null) || (title.trim().length()==0)){
			out.write("arg");
		}else{
			out.write(title.replaceAll("\\s", "_"));
		}
		out.write(">");
	}
	

}
