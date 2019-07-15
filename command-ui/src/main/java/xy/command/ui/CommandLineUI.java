package xy.command.ui;

import java.io.File;

import xy.command.instance.CommandLineInstance;
import xy.command.model.CommandLineProject;
import xy.reflect.ui.CustomizedUI;
import xy.reflect.ui.control.swing.customizer.SwingCustomizer;
import xy.reflect.ui.info.type.ITypeInfo;
import xy.reflect.ui.info.type.factory.InfoProxyFactory;
import xy.reflect.ui.info.type.source.ITypeInfoSource;
import xy.reflect.ui.util.MoreSystemProperties;

public class CommandLineUI extends CustomizedUI {

	public static void main(String[] args) {
		System.out.println("Set the following system property to disable the design mode:\n-D"
				+ MoreSystemProperties.HIDE_INFO_CUSTOMIZATIONS_TOOLS + "=true");

		CommandLineProject object = new CommandLineProject();
		if (args.length >= 1) {
			object.loadFromFile(new File(args[0]));
		}
		CommandLineUI ui = new CommandLineUI();
		if ((args.length >= 2) && (args[1].equals("--instanciate"))) {
			ui.getRenderer().openObjectFrame(object.instanciate());
		} else {
			ui.getRenderer().openObjectFrame(object);
		}
	}

	private SwingCustomizer renderer = new SwingCustomizer(this, "commandLine.icu");

	public SwingCustomizer getRenderer() {
		return renderer;
	}

	@Override
	public ITypeInfoSource getTypeInfoSource(Object object) {
		if (object instanceof CommandLineInstance) {
			return new TypeInfoSourceFromCommandLine(((CommandLineInstance) object).model, null);
		} else {
			return super.getTypeInfoSource(object);
		}
	}

	@Override
	protected ITypeInfo getTypeInfoBeforeCustomizations(ITypeInfo type) {
		return new InfoProxyFactory() {

		}.wrapTypeInfo(super.getTypeInfoBeforeCustomizations(type));
	}

}
