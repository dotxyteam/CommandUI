package xy.command.model;

import javax.swing.JFileChooser;

import xy.command.model.instance.FileArgumentInstance;
import xy.command.ui.CommandLinePlayer;

public class FileArgument extends AbstractCommandLinePart {

	protected static final long serialVersionUID = 1L;

	@Override
	public FileArgumentInstance createInstance(CommandLinePlayer player) {
		return new FileArgumentInstance(player, this);
	}


	public void configureFileChooser(JFileChooser fileChooser) {
		fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
	}

}
