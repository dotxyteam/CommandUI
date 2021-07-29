package xy.command.ui;

import java.io.File;
import java.io.IOException;
import javax.swing.SwingUtilities;

import xy.command.instance.ArgumentGroupInstance;
import xy.command.instance.CommandLineInstance;
import xy.command.model.CommandLineProject;
import xy.reflect.ui.CustomizedUI;
import xy.reflect.ui.control.swing.customizer.SwingCustomizer;
import xy.reflect.ui.info.type.source.ITypeInfoSource;

/**
 * The class specifying how the command line project and instance GUIs should
 * look and behave. The class is also responsible for generating the above GUIs.
 * 
 * @author olitank
 *
 */
public class CommandLineUI extends CustomizedUI {

	public static void main(String[] args) throws Exception {
		final CommandLineUI ui = new CommandLineUI();
		if ((CURRENT_EXE_FILE_PATH != null) && (NORMAL_EXE_FILE_PATH != null)) {
			File currentExeFile = new File(CURRENT_EXE_FILE_PATH);
			File normalExeFile = new File(NORMAL_EXE_FILE_PATH);
			boolean executingDistribution = !currentExeFile.getCanonicalPath().equals(normalExeFile.getCanonicalPath());
			if (executingDistribution) {
				CommandLineProject object = new CommandLineProject();
				object.loadFromFile(new File(CURRENT_EXE_FILE_PATH + "." + PROJECT_FILE_EXTENSION));
				SwingUtilities.invokeLater(new Runnable() {
					@Override
					public void run() {
						ui.getRenderer().openObjectFrame(object.instanciate());
					}
				});
				return;
			}
		}
		CommandLineProject object = new CommandLineProject();
		if (args.length >= 1) {
			object.loadFromFile(new File(args[0]));
		}
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				if ((args.length >= 2) && (args[1].equals("--instanciate"))) {
					ui.getRenderer().openObjectFrame(object.instanciate());
				} else {
					ui.getRenderer().openObjectFrame(object);
				}
			}
		});
	}

	public static final String NORMAL_EXE_FILE_PATH = System.getProperty("xy.command.ui.defaultExeFile");
	public static final String CURRENT_EXE_FILE_PATH = System.getProperty("xy.command.ui.exeFile");
	public static final String PROJECT_FILE_EXTENSION = "cml";

	private static final String GUI_CUSTOMIZATIONS_RESOURCE_NAME = "commandLine.icu";
	private static final String GUI_CUSTOMIZATIONS_RESOURCE_DIRECTORY = System
			.getProperty("xy.command.ui.alternateUICustomizationsFileDirectory");

	private SwingCustomizer renderer;

	/**
	 * The default constructor. It initializes the GUI system.
	 */
	public CommandLineUI() {
		if (GUI_CUSTOMIZATIONS_RESOURCE_DIRECTORY != null) {
			renderer = new SwingCustomizer(this,
					GUI_CUSTOMIZATIONS_RESOURCE_DIRECTORY + "/" + GUI_CUSTOMIZATIONS_RESOURCE_NAME);
		} else {
			try {
				getInfoCustomizations().loadFromStream(xy.command.ui.resource.ClassInPackage.class
						.getResourceAsStream(GUI_CUSTOMIZATIONS_RESOURCE_NAME), null);
			} catch (IOException e) {
				throw new AssertionError(e);
			}
			renderer = new SwingCustomizer(this, null);
		}
	}

	/**
	 * @return The GUI renderer object.
	 */
	public SwingCustomizer getRenderer() {
		return renderer;
	}

	@Override
	public ITypeInfoSource getTypeInfoSource(Object object) {
		if (object instanceof CommandLineInstance) {
			return new TypeInfoSourceFromCommandLine(this, ((CommandLineInstance) object).model, null);
		} else if (object instanceof ArgumentGroupInstance) {
			return new TypeInfoSourceFromArgumentGroup(this, ((ArgumentGroupInstance) object).model, null);
		} else {
			return super.getTypeInfoSource(object);
		}
	}

}
