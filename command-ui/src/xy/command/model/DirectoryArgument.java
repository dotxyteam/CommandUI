package xy.command.model;

import javax.swing.JFileChooser;

import xy.command.model.instance.DirectoryArgumentInstance;
import xy.command.ui.CommandLinePlayer;

public class DirectoryArgument extends FileArgument {

	protected static final long serialVersionUID = 1L;
	@Override
	public DirectoryArgumentInstance createInstance(CommandLinePlayer player) {
		return new DirectoryArgumentInstance(player, this);
	}
	
	@Override
	public void configureFileChooser(JFileChooser fileChooser) {
		fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
	}

}
