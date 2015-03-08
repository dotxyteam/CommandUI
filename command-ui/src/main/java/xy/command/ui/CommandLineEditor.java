package xy.command.ui;

import java.awt.FileDialog;
import java.awt.Image;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.imageio.ImageIO;
import javax.swing.JFrame;

import xy.command.model.AbstractCommandLinePart;
import xy.command.model.ArgumentGroup;
import xy.command.model.ArgumentPage;
import xy.command.model.Choice;
import xy.command.model.CommandLine;
import xy.command.model.DirectoryArgument;
import xy.command.model.FileArgument;
import xy.command.model.FixedArgument;
import xy.command.model.InputArgument;
import xy.command.model.MultiplePart;
import xy.command.model.OptionalPart;
import xy.command.model.instance.CommandLineInstance;
import xy.command.ui.util.FileUtils;
import xy.reflect.ui.ReflectionUI;
import xy.reflect.ui.control.ModificationStack.IModification;
import xy.reflect.ui.info.InfoCategory;
import xy.reflect.ui.info.field.FieldInfoProxy;
import xy.reflect.ui.info.field.IFieldInfo;
import xy.reflect.ui.info.method.IMethodInfo;
import xy.reflect.ui.info.parameter.IParameterInfo;
import xy.reflect.ui.info.type.FileTypeInfo;
import xy.reflect.ui.info.type.IListTypeInfo;
import xy.reflect.ui.info.type.IListTypeInfo.IItemPosition;
import xy.reflect.ui.info.type.IListTypeInfo.IListStructuralInfo;
import xy.reflect.ui.info.type.AbstractTreeDetectionListStructuralInfo;
import xy.reflect.ui.info.type.HiddenNullableFacetsTypeInfoProxyConfiguration;
import xy.reflect.ui.info.type.IMapEntryTypeInfo;
import xy.reflect.ui.info.type.ITypeInfo;
import xy.reflect.ui.info.type.ITypeInfoSource;
import xy.reflect.ui.info.type.JavaTypeInfoSource;
import xy.reflect.ui.info.type.StandardCollectionTypeInfo;
import xy.reflect.ui.info.type.StandardMapListTypeInfo;
import xy.reflect.ui.info.type.TypeInfoProxyConfiguration;
import xy.reflect.ui.util.ReflectionUIError;
import xy.reflect.ui.util.ReflectionUIUtils;

public class CommandLineEditor extends ReflectionUI {

	public static final String COMMAND_LINE_FILE_EXTENSION = "cml";
	private static final String EXE_FILE_PATH_PROPERTY_KEY = "commandui.exe.file";
	protected static final String APP_NAME = "Command UI";

	public static void main(String[] args) {
		String exeFilePath = System.getProperty(EXE_FILE_PATH_PROPERTY_KEY);
		if (exeFilePath != null) {
			File playerFile = getPlayerFile(new File(exeFilePath));
			if (playerFile.exists()) {
				CommandLinePlayer.main(new String[] { playerFile.getPath() });
				return;
			}
		}
		new CommandLineEditor().openObjectFrame(new CommandLine(),
				"Command UI", getClassIconImage(CommandLine.class));
	}

	private static File getPlayerFile(File exeFile) {
		return new File(exeFile + COMMAND_LINE_FILE_EXTENSION);
	}

	public static Image getClassIconImage(Class<? extends Object> class1) {
		try {
			return ImageIO.read(xy.command.ui.resource.ClassInPackage.class
					.getResource(class1.getSimpleName() + ".gif"));
		} catch (IOException e) {
			throw new AssertionError(e);
		}
	}

	@Override
	public Image getObjectIconImage(Object object) {
		if (object == null) {
			return null;
		}
		Class<?> class1 = object.getClass();
		if ((object != null)
				&& CommandLine.class.getPackage().equals(class1.getPackage())
				&& (class1.getEnclosingClass() == null)) {
			return getClassIconImage(class1);
		} else {
			return super.getObjectIconImage(object);
		}
	}

	@Override
	public String toString(Object object) {
		if (object instanceof ArgumentPage) {
			String title = ((ArgumentPage) object).title;
			return ((title == null) ? "" : title);
		} else if (object instanceof AbstractCommandLinePart) {
			String title = ((AbstractCommandLinePart) object).title;
			return ((title == null) ? "" : title);
		} else {
			return super.toString(object);
		}
	}

	@Override
	public ITypeInfo getTypeInfo(ITypeInfoSource typeSource) {
		return new HiddenNullableFacetsTypeInfoProxyConfiguration(
				CommandLineEditor.this) {

			@Override
			protected String getCaption(ITypeInfo type) {
				if (type.getName().equals(
						AbstractCommandLinePart.class.getName())) {
					return "Command Line part";
				} else {
					return super.getCaption(type);
				}
			}

			@Override
			protected List<ITypeInfo> getPolymorphicInstanceSubTypes(
					ITypeInfo type) {
				if (type.getName().equals(
						AbstractCommandLinePart.class.getName())) {
					return Arrays.asList(getTypeInfo(new JavaTypeInfoSource(
							InputArgument.class)),
							getTypeInfo(new JavaTypeInfoSource(
									OptionalPart.class)),
							getTypeInfo(new JavaTypeInfoSource(
									MultiplePart.class)),
							getTypeInfo(new JavaTypeInfoSource(Choice.class)),
							getTypeInfo(new JavaTypeInfoSource(
									FixedArgument.class)),
							getTypeInfo(new JavaTypeInfoSource(
									FileArgument.class)),
							getTypeInfo(new JavaTypeInfoSource(
									DirectoryArgument.class)),
							getTypeInfo(new JavaTypeInfoSource(
									ArgumentGroup.class)));
				} else {
					return super.getPolymorphicInstanceSubTypes(type);
				}
			}

			@Override
			protected List<IFieldInfo> getFields(ITypeInfo type) {
				if (type.getName().equals(FixedArgument.class.getName())) {
					List<IFieldInfo> result = new ArrayList<IFieldInfo>(
							super.getFields(type));
					result.remove(ReflectionUIUtils.findInfoByName(result,
							"description"));
					return result;
				} else {
					return super.getFields(type);
				}
			}

			@Override
			protected List<IMethodInfo> getMethods(ITypeInfo type) {
				if (type.getName().startsWith(
						CommandLine.class.getPackage().getName())
						&& !type.getName().contains("$")) {
					if (type.getName().equals(CommandLine.class.getName())) {
						List<IMethodInfo> result = new ArrayList<IMethodInfo>(
								super.getMethods(type));
						IMethodInfo createInstanceMethod = ReflectionUIUtils
								.findInfoByName(result, "createInstance");
						result.remove(createInstanceMethod);
						result.add(getTestMethod());
						result.add(getDistributeMethod());
						return result;
					} else {
						return Collections.<IMethodInfo> emptyList();
					}
				} else {
					return super.getMethods(type);
				}
			}

			@Override
			protected ITypeInfo getType(IFieldInfo field,
					ITypeInfo containingType) {
				if (containingType.getName().equals(Choice.class.getName())
						&& field.getName().equals("options")) {
					return new StandardMapListTypeInfo(CommandLineEditor.this,
							HashMap.class, String.class, List.class) {

						@Override
						public StandardMapEntryTypeInfo getItemType() {
							return new StandardMapEntryTypeInfo() {

								@Override
								public IFieldInfo getKeyField() {
									return new FieldInfoProxy(
											super.getKeyField()) {

										@Override
										public String getCaption() {
											return "Title";
										}

									};
								}

								@Override
								public IFieldInfo getValueField() {
									return new FieldInfoProxy(
											super.getValueField()) {

										@Override
										public Object getValue(Object object) {
											ArgumentGroup group = (ArgumentGroup) super
													.getValue(object);
											if (group == null) {
												return null;
											}
											return group.parts;
										}

										@Override
										public void setValue(Object object,
												Object value) {
											@SuppressWarnings("unchecked")
											List<AbstractCommandLinePart> parts = (List<AbstractCommandLinePart>) value;
											ArgumentGroup group;
											if (parts == null) {
												group = null;
											}
											group = new ArgumentGroup();
											group.parts = parts;
											super.setValue(object, group);
										}

										@Override
										public ITypeInfo getType() {
											return new StandardCollectionTypeInfo(
													CommandLineEditor.this,
													List.class,
													AbstractCommandLinePart.class);
										}

									};
								}

							};
						}

					};
				} else if (containingType.getName().equals(
						CommandLine.class.getName())
						&& field.getName().equals("arguments")) {
					return new TypeInfoProxyConfiguration() {

						@Override
						protected IListStructuralInfo getStructuralInfo(
								IListTypeInfo type) {
							return new AbstractTreeDetectionListStructuralInfo(
									CommandLineEditor.this, type.getItemType()) {

								@Override
								protected boolean isFieldBased() {
									return false;
								}

								@Override
								public int getColumnCount() {
									return 2;
								}

								@Override
								public String getColumnCaption(int columnIndex) {
									if (columnIndex == 0) {
										return "Type";
									} else if (columnIndex == 1) {
										return "Title";
									} else {
										throw new AssertionError();
									}
								}

								@Override
								public String getCellValue(
										IItemPosition itemPosition,
										int columnIndex) {
									Object item = itemPosition.getItem();
									ITypeInfo type = getTypeInfo(getTypeInfoSource(item));
									if (columnIndex == 0) {
										if (type instanceof IMapEntryTypeInfo) {
											Object key = ((IMapEntryTypeInfo) type)
													.getKeyField().getValue(
															item);
											return (key == null) ? ""
													: CommandLineEditor.this
															.toString(key);
										} else {
											return type.getCaption();
										}
									} else if (columnIndex == 1) {
										IFieldInfo titleField = ReflectionUIUtils
												.findInfoByName(
														type.getFields(),
														"title");
										if (titleField == null) {
											return "";
										} else {
											return (String) titleField
													.getValue(item);
										}
									} else {
										throw new AssertionError();
									}
								}

							};
						}

					}.get((IListTypeInfo) field.getType());
				} else {
					return super.getType(field, containingType);
				}
			}

		}.get(super.getTypeInfo(typeSource));
	}

	private IMethodInfo getTestMethod() {
		return new IMethodInfo() {
			@Override
			public Object invoke(Object object,
					Map<String, Object> valueByParameterName) {
				CommandLine commandLine = (CommandLine) object;
				CommandLineInstance instance = commandLine.createInstance();
				CommandLinePlayer player = new CommandLinePlayer();
				CommandLine model = instance.getModel();
				player.openObjectFrame(instance, model.title,
						getObjectIconImage(model));
				return null;
			}

			@Override
			public String getCaption() {
				return "Test";
			}

			@Override
			public ITypeInfo getReturnValueType() {
				return null;
			}

			@Override
			public String getName() {
				return "test";
			}

			@Override
			public String getDocumentation() {
				return null;
			}

			@Override
			public List<IParameterInfo> getParameters() {
				return Collections.emptyList();
			}

			@Override
			public boolean isReadOnly() {
				return true;
			}

			@Override
			public InfoCategory getCategory() {
				return null;
			}

			@Override
			public IModification getUndoModification(Object object,
					Map<String, Object> valueByParameterName) {
				return null;
			}

		};
	}

	private IMethodInfo getDistributeMethod() {
		return new IMethodInfo() {
			@Override
			public Object invoke(Object object,
					Map<String, Object> valueByParameterName) {
				File commandUIExeFile;
				String commandUIExeFilePath = System
						.getProperty(EXE_FILE_PATH_PROPERTY_KEY);
				if (commandUIExeFilePath == null) {
					if ((commandUIExeFile = askCommandUIExecutableLocation()) == null) {
						return null;
					}
				} else {
					commandUIExeFile = new File(commandUIExeFilePath);
				}
				File outputExeFile = (File) valueByParameterName
						.get("executableFilePath");
				CommandLine commandLine = (CommandLine) object;
				generateOutputFiles(commandLine, commandUIExeFile,
						outputExeFile);
				return null;
			}

			private void generateOutputFiles(CommandLine commandLine,
					File commandUIExeFile, File outputExeFile) {
				try {
					FileUtils.copy(commandUIExeFile, outputExeFile);
					commandLine.save(getPlayerFile(outputExeFile));
				} catch (Exception e) {
					throw new ReflectionUIError(e);
				}
			}

			private File askCommandUIExecutableLocation() {
				FileDialog fd = new FileDialog((JFrame) null, "Locate the '"
						+ APP_NAME + "' executable file:", FileDialog.LOAD);
				fd.setVisible(true);
				return new File(fd.getDirectory(), fd.getFile());
			}

			@Override
			public String getCaption() {
				return "Distribute";
			}

			@Override
			public ITypeInfo getReturnValueType() {
				return null;
			}

			@Override
			public String getName() {
				return "distribute";
			}

			@Override
			public String getDocumentation() {
				return null;
			}

			@Override
			public List<IParameterInfo> getParameters() {
				return Collections
						.<IParameterInfo> singletonList(new IParameterInfo() {

							@Override
							public String getName() {
								return "executableFilePath";
							}

							@Override
							public String getDocumentation() {
								return null;
							}

							@Override
							public String getCaption() {
								return "Executable File Path";
							}

							@Override
							public boolean isNullable() {
								return false;
							}

							@Override
							public ITypeInfo getType() {
								return new FileTypeInfo(CommandLineEditor.this);
							}

							@Override
							public int getPosition() {
								return 0;
							}

							@Override
							public Object getDefaultValue() {
								return null;
							}
						});
			}

			@Override
			public boolean isReadOnly() {
				return true;
			}

			@Override
			public InfoCategory getCategory() {
				return null;
			}

			@Override
			public IModification getUndoModification(Object object,
					Map<String, Object> valueByParameterName) {
				return null;
			}

		};
	}
}
