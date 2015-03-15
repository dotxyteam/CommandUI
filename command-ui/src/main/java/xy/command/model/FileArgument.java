package xy.command.model;

import javax.swing.JFileChooser;

import xy.command.model.instance.FileArgumentInstance;

public class FileArgument extends AbstractCommandLinePart {

	protected static final long serialVersionUID = 1L;
	public String title;
	
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
	

}
