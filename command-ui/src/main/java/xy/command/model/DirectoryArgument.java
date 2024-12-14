package xy.command.model;

import java.io.IOException;
import java.io.Writer;

import javax.swing.JFileChooser;

import xy.command.instance.AbstractCommandLinePartInstance;
import xy.command.instance.DirectoryArgumentInstance;
import xy.command.ui.util.ValidationError;

/**
 * A directory path command line model part.
 * 
 * @author olitank
 *
 */
public class DirectoryArgument extends AbstractCommandLinePart {

	protected static final long serialVersionUID = 1L;

	/**
	 * The title of this command line model part.
	 */
	public String title = "";

	/**
	 * This value will provided by default.
	 */
	public String defaultValue = "";

	@Override
	public String toString() {
		return title;
	}

	@Override
	public void writetUsageText(Writer out) throws IOException {
		out.write("<");
		if ((title == null) || (title.trim().length() == 0)) {
			out.write("arg");
		} else {
			out.write(title.replaceAll("\\s", "_"));
		}
		out.write(">");
	}

	@Override
	public void validate() throws Exception {
		if ((title == null) || (title.trim().length() == 0)) {
			throw new ValidationError("Enter the title");
		}
	}

	/**
	 * Configures the file browser.
	 * 
	 * @param fileChooser The file browser.
	 */
	public void configureFileChooser(JFileChooser fileChooser) {
		fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
	}

	@Override
	public AbstractCommandLinePartInstance instantiate() {
		return new DirectoryArgumentInstance(this);
	}

}
