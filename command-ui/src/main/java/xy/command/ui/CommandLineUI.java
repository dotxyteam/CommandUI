package xy.command.ui;

import java.awt.Font;
import java.awt.GraphicsEnvironment;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import javax.swing.SwingUtilities;
import javax.swing.UIManager;

import com.formdev.flatlaf.FlatLaf;
import com.formdev.flatlaf.intellijthemes.FlatLightFlatIJTheme;

import xy.command.instance.ArgumentGroupInstance;
import xy.command.instance.CommandLineInstance;
import xy.command.model.CommandLineProject;
import xy.reflect.ui.CustomizedUI;
import xy.reflect.ui.control.swing.customizer.SwingCustomizer;
import xy.reflect.ui.info.method.IMethodInfo;
import xy.reflect.ui.info.method.InvocationData;
import xy.reflect.ui.info.method.MethodInfoProxy;
import xy.reflect.ui.info.type.ITypeInfo;
import xy.reflect.ui.info.type.factory.IInfoProxyFactory;
import xy.reflect.ui.info.type.factory.InfoProxyFactory;
import xy.reflect.ui.info.type.source.ITypeInfoSource;
import xy.reflect.ui.util.ReflectionUIUtils;

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
						ui.getRenderer().openObjectFrame(object.instantiate());
					}
				});
				return;
			}
		}
		CommandLineProject object = new CommandLineProject();
		if (args.length >= 1) {
			object.loadFromFile(new File(args[0]));
		}
		{
			System.setProperty("awt.useSystemAAFontSettings", "on");
			System.setProperty("swing.aatext", "true");
			Font font;
			try (InputStream is = xy.command.ui.resource.ClassInPackage.class
					.getResourceAsStream("FlatLightFlatIJTheme.ttf")) {
				font = Font.createFont(Font.TRUETYPE_FONT, is);
				GraphicsEnvironment.getLocalGraphicsEnvironment().registerFont(font);
				FlatLaf.setPreferredFontFamily(font.getFamily());
			}
			UIManager.put("TableHeader.height", 0);
			UIManager.setLookAndFeel(new FlatLightFlatIJTheme());
		}
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				if ((args.length >= 2) && (args[1].equals(INSTANTIATION_COMMAND_OPTION))) {
					ui.getRenderer().openObjectFrame(object.instantiate());
				} else {
					ui.getRenderer().openObjectFrame(object);
				}
			}
		});
	}

	public static final String NORMAL_EXE_FILE_PATH = System.getProperty("xy.command.ui.defaultExeFile");
	public static final String CURRENT_EXE_FILE_PATH = System.getProperty("xy.command.ui.exeFile");
	public static final String PROJECT_FILE_EXTENSION = "cml";

	private static final String INSTANTIATION_COMMAND_OPTION = "--instantiate";

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
			return new TypeInfoSourceFromCommandLine(((CommandLineInstance) object).model, null);
		} else if (object instanceof ArgumentGroupInstance) {
			return new TypeInfoSourceFromArgumentGroup(((ArgumentGroupInstance) object).model, null);
		} else {
			return super.getTypeInfoSource(object);
		}
	}

	@Override
	protected IInfoProxyFactory createBeforeInfoCustomizationsFactory() {
		return new InfoProxyFactory() {

			@Override
			protected List<IMethodInfo> getMethods(ITypeInfo type) {
				List<IMethodInfo> result = new ArrayList<IMethodInfo>(super.getMethods(type));
				if (type.getName().equals(CommandLineProject.class.getName())) {
					result.add(new MethodInfoProxy(IMethodInfo.NULL_METHOD_INFO) {

						@Override
						public String getName() {
							return "test";
						}

						@Override
						public String getSignature() {
							return ReflectionUIUtils.buildMethodSignature(this);
						}

						@Override
						public String getCaption() {
							return "Test";
						}

						@Override
						public Object invoke(Object object, InvocationData invocationData) {
							try {
								CommandLineProject project = (CommandLineProject) object;
								File tmpFile = File.createTempFile("test", "." + PROJECT_FILE_EXTENSION);
								try (FileOutputStream output = new FileOutputStream(tmpFile)) {
									project.saveToStream(output);
									CommandLineUI
											.main(new String[] { tmpFile.getPath(), INSTANTIATION_COMMAND_OPTION });
								} finally {
									if (!tmpFile.delete()) {
										System.err.println("Failed to delete file " + tmpFile);
									}
								}
								return null;
							} catch (Exception e) {
								throw new RuntimeException(e);
							}
						}
					});
				}
				return result;
			}
		};
	}

}
