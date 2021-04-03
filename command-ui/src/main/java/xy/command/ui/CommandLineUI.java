package xy.command.ui;

import java.io.File;

import xy.command.instance.ArgumentGroupInstance;
import xy.command.instance.CommandLineInstance;
import xy.command.model.CommandLineProject;
import xy.reflect.ui.CustomizedUI;
import xy.reflect.ui.control.swing.customizer.SwingCustomizer;
import xy.reflect.ui.info.type.source.ITypeInfoSource;

public class CommandLineUI extends CustomizedUI {

	public static final String DEFAULT_EXE_FILE_PATH = System.getProperty("xy.command.ui.defaultExeFile");
	public static final String EXE_FILE_PATH = System.getProperty("xy.command.ui.exeFile");
	public static final String GUI_CUSTOMIZATIONS_FILE_PATH = "commandLine.icu";
	public static final String PROJECT_FILE_EXTENSION = "cml";

	public static void main(String[] args) throws Exception {
		CommandLineUI ui = new CommandLineUI();
		if ((EXE_FILE_PATH != null) && (DEFAULT_EXE_FILE_PATH != null)) {
			File exeFile = new File(EXE_FILE_PATH);
			File defaultExeFile = new File(DEFAULT_EXE_FILE_PATH);
			if (!exeFile.getCanonicalPath().equals(defaultExeFile.getCanonicalPath())) {
				CommandLineProject object = new CommandLineProject();
				object.loadFromFile(new File(EXE_FILE_PATH + "." + PROJECT_FILE_EXTENSION));
				ui.getRenderer().openObjectFrame(object.instanciate());
				return;
			}
		}
		CommandLineProject object = new CommandLineProject();
		if (args.length >= 1) {
			object.loadFromFile(new File(args[0]));
		}
		if ((args.length >= 2) && (args[1].equals("--instanciate"))) {
			ui.getRenderer().openObjectFrame(object.instanciate());
		} else {
			ui.getRenderer().openObjectFrame(object);
		}
	}

	private SwingCustomizer renderer = new SwingCustomizer(this, GUI_CUSTOMIZATIONS_FILE_PATH);

	public SwingCustomizer getRenderer() {
		return renderer;
	}

	@Override
	public ITypeInfoSource getTypeInfoSource(Object object) {
		if (object instanceof CommandLineInstance) {
			return new TypeInfoSourceFromCommandLine(((CommandLineInstance) object).model, null);
		} else if (object instanceof ArgumentGroupInstance) {
			return new TypeInfoSourceFromArgumentGroup(((ArgumentGroupInstance) object).model, null);
		} else {
			return super.getTypeInfoSource(object);
		}
	}

}
