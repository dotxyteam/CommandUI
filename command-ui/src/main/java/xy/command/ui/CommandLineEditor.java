package xy.command.ui;

import java.awt.Component;
import java.awt.FileDialog;
import java.awt.Image;
import java.awt.Window;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import javax.imageio.ImageIO;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

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
import xy.command.ui.util.CommandUIUtils;
import xy.command.ui.util.FileUtils;
import xy.reflect.ui.ReflectionUI;
import xy.reflect.ui.control.swing.ListControl;
import xy.reflect.ui.info.InfoCategory;
import xy.reflect.ui.info.field.IFieldInfo;
import xy.reflect.ui.info.method.IMethodInfo;
import xy.reflect.ui.info.parameter.IParameterInfo;
import xy.reflect.ui.info.type.custom.FileTypeInfo;
import xy.reflect.ui.info.type.iterable.IListTypeInfo;
import xy.reflect.ui.info.type.iterable.map.IMapEntryTypeInfo;
import xy.reflect.ui.info.type.iterable.util.ItemPosition;
import xy.reflect.ui.info.type.iterable.util.structure.AbstractTreeDetectionListStructuralInfo;
import xy.reflect.ui.info.type.iterable.util.structure.IListStructuralInfo;
import xy.reflect.ui.info.type.source.ITypeInfoSource;
import xy.reflect.ui.info.type.source.JavaTypeInfoSource;
import xy.reflect.ui.info.type.util.HiddenNullableFacetsTypeInfoProxyConfiguration;
import xy.reflect.ui.info.type.util.TypeInfoProxyConfiguration;
import xy.reflect.ui.info.type.ITypeInfo;
import xy.reflect.ui.undo.IModification;
import xy.reflect.ui.util.ReflectionUIError;
import xy.reflect.ui.util.ReflectionUIUtils;
import xy.reflect.ui.info.method.InvocationData;

public class CommandLineEditor extends ReflectionUI {

	public static final String COMMAND_LINE_FILE_EXTENSION = "cml";
	protected static final String CURRENT_EXE_FILE_PATH_PROPERTY_KEY = "current.exe.file.path";
	protected static final String NORMAL_EXE_FILE_NAME_PROPERTY_KEY = "normal.exe.file.name";
	protected static final String APP_NAME = "Command UI";

	public static void main(String[] args) {
		CommandUIUtils.setupLookAndFeel();
		try {
			String exeFilePath = System
					.getProperty(CURRENT_EXE_FILE_PATH_PROPERTY_KEY);
			if (exeFilePath != null) {
				File exeFile = new File(exeFilePath);
				String normalExeFileName = System
						.getProperty(NORMAL_EXE_FILE_NAME_PROPERTY_KEY);
				if (!normalExeFileName.equals(exeFile.getName())) {
					File playerFile = getPlayerFile(new File(exeFilePath));
					if (!playerFile.exists()) {
						throw new Exception(
								"Unexpected executable file name: '"
										+ exeFile.getName()
										+ "'.\nFile not found: '" + playerFile
										+ "'.") {
							protected static final long serialVersionUID = 1L;

							@Override
							public String toString() {
								return getMessage();
							}
						};
					}
					CommandLinePlayer
							.main(new String[] { playerFile.getPath() });
					return;
				}
			}
			new CommandLineEditor().getSwingRenderer().openObjectFrame(new CommandLine(),
					"Command UI", getClassIconImage(CommandLine.class));
		} catch (Throwable t) {
			JOptionPane.showMessageDialog(null, t.toString(), null,
					JOptionPane.ERROR_MESSAGE);
		}

	}

	protected static File getPlayerFile(File exeFile) {
		return new File(exeFile + ".ini");
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
							FixedArgument.class)),
							getTypeInfo(new JavaTypeInfoSource(
									InputArgument.class)),
							getTypeInfo(new JavaTypeInfoSource(
									OptionalPart.class)),
							getTypeInfo(new JavaTypeInfoSource(
									MultiplePart.class)),
							getTypeInfo(new JavaTypeInfoSource(Choice.class)),
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
				List<IFieldInfo> result = new ArrayList<IFieldInfo>(
						super.getFields(type));
				IFieldInfo titleField = ReflectionUIUtils.findInfoByName(
						result, "title");
				if (titleField != null) {
					result.remove(titleField);
					result.add(0, titleField);
				}
				if (type.getName().equals(FixedArgument.class.getName())) {
					result.remove(ReflectionUIUtils.findInfoByName(result,
							"description"));
				}
				return result;
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
						result.add(getValidateMethod());
						result.add(getPreviewMethod());
						result.add(getDistributeMethod());
						result.add(getOpenCommandTestWindowMethod());
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
				if (containingType.getName()
						.equals(CommandLine.class.getName())
						&& field.getName().equals("arguments")) {
					return new TypeInfoProxyConfiguration() {

						@Override
						protected IListStructuralInfo getStructuralInfo(
								IListTypeInfo type) {
							return new AbstractTreeDetectionListStructuralInfo(
									CommandLineEditor.this, type.getItemType()) {

								@Override
								public int getColumnCount() {
									return 2;
								}

								@Override
								public String getColumnCaption(int columnIndex) {
									if (columnIndex == 0) {
										return "Type";
									} else if (columnIndex == 1) {
										return "Value";
									} else {
										throw new AssertionError();
									}
								}

								@Override
								public String getCellValue(
										ItemPosition itemPosition,
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
										if (type instanceof IMapEntryTypeInfo) {
											return "";
										} else {
											return item.toString();
										}
									} else {
										throw new AssertionError();
									}
								}

								@Override
								public Image getCellIconImage(
										ItemPosition itemPosition,
										int columnIndex) {
									if (columnIndex == 0) {
										Object item = itemPosition.getItem();
										return CommandLineEditor.this
												.getObjectIconImage(item);
									} else {
										return null;
									}
								}

								@Override
								protected boolean autoDetectTreeStructure() {
									return true;
								}

								@Override
								protected boolean isValidTreeNodeItemType(
										ITypeInfo type) {
									if (type.getName().startsWith(
											CommandLine.class.getPackage()
													.getName())) {
										return true;
									}
									return super.isValidTreeNodeItemType(type);
								}

								@Override
								protected boolean displaysSubListFieldNameAsTreeNode(
										IFieldInfo subListField,
										ItemPosition itemPosition) {
									if (subListField.getName().equals("parts")) {
										return false;
									}
									if (subListField.getName()
											.equals("options")) {
										return false;
									}
									return super
											.displaysSubListFieldNameAsTreeNode(
													subListField, itemPosition);
								}

								@Override
								public List<IFieldInfo> getItemSubListCandidateFields(
										ItemPosition itemPosition) {
									IFieldInfo containingListField = itemPosition
											.getContainingListField();
									if (containingListField.getName().equals(
											"arguments")) {
										ITypeInfo pageType = ((IListTypeInfo) containingListField
												.getType()).getItemType();
										IFieldInfo pagePartsField = ReflectionUIUtils
												.findInfoByName(
														pageType.getFields(),
														"parts");
										return Collections
												.singletonList(pagePartsField);
									}
									return super
											.getItemSubListCandidateFields(itemPosition);
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

	protected IMethodInfo getOpenCommandTestWindowMethod() {
		return new IMethodInfo() {
			@Override
			public Object invoke(Object object, InvocationData invocationData) {
				CommandLine commandLine = (CommandLine) object;
				if ((commandLine.executionDir == null)
						|| (commandLine.executionDir.getPath().trim().length() == 0)) {
					throw new ReflectionUIError("Enter the execution directory");
				}
				JPanel commandLineForm = ReflectionUIUtils.getKeysFromValue(
						getSwingRenderer().getObjectByForm(), commandLine).get(0);
				Window commandLineWindow = SwingUtilities
						.getWindowAncestor(commandLineForm);
				CommandMonitoringDialog dialog = new CommandMonitoringDialog(
						commandLineWindow, null, commandLine.executionDir);
				dialog.setModal(false);
				dialog.setVisible(true);
				return null;
			}

			@Override
			public String getCaption() {
				return "Open Command WIndow";
			}

			@Override
			public Map<String, Object> getSpecificProperties() {
				return Collections.emptyMap();
			}

			@Override
			public ITypeInfo getReturnValueType() {
				return null;
			}

			@Override
			public String getName() {
				return "testCommand";
			}

			@Override
			public String getOnlineHelp() {
				return "Open the command execution window for testing purposes";
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
					InvocationData invocationData) {
				return null;
			}

			@Override
			public void validateParameters(Object object,
					InvocationData invocationData) throws Exception {
			}

		};
	}

	protected IMethodInfo getPreviewMethod() {
		return new IMethodInfo() {
			@Override
			public Object invoke(Object object, InvocationData invocationData) {
				CommandLine commandLine = (CommandLine) object;
				getValidateMethod().invoke(commandLine,
						new InvocationData());
				CommandLineInstance instance = commandLine.createInstance();
				CommandLinePlayer player = new CommandLinePlayer();
				CommandLine model = instance.getModel();
				player.getSwingRenderer().openObjectFrame(instance, model.title,
						getObjectIconImage(model));
				return null;
			}

			@Override
			public String getCaption() {
				return "Preview GUI";
			}

			@Override
			public Map<String, Object> getSpecificProperties() {
				return Collections.emptyMap();
			}

			@Override
			public ITypeInfo getReturnValueType() {
				return null;
			}

			@Override
			public String getName() {
				return "preview";
			}

			@Override
			public String getOnlineHelp() {
				return "Runs a preview of the current command line specification GUI";
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
					InvocationData invocationData) {
				return null;
			}

			@Override
			public void validateParameters(Object object,
					InvocationData invocationData) throws Exception {
			}

		};
	}

	protected IMethodInfo getDistributeMethod() {
		return new IMethodInfo() {
			@Override
			public Object invoke(Object object, InvocationData invocationData) {
				CommandLine commandLine = (CommandLine) object;
				try {
					commandLine.validate();
				} catch (Exception e) {
					throw new ReflectionUIError(e);
				}
				File commandUIExeFile;
				String commandUIExeFilePath = System
						.getProperty(CURRENT_EXE_FILE_PATH_PROPERTY_KEY);
				if (commandUIExeFilePath == null) {
					if ((commandUIExeFile = askCommandUIExecutableLocation()) == null) {
						return null;
					}
				} else {
					commandUIExeFile = new File(commandUIExeFilePath);
				}
				File outputExeFile = (File) invocationData
						.getParameterValue(getParameters().get(0));
				generateOutputFiles(commandLine, commandUIExeFile,
						outputExeFile);
				return null;
			}

			protected void generateOutputFiles(CommandLine commandLine,
					File commandUIExeFile, File outputExeFile) {
				try {
					FileUtils.copy(commandUIExeFile, outputExeFile);
					commandLine.saveToFile(getPlayerFile(outputExeFile));
				} catch (Exception e) {
					throw new ReflectionUIError(e);
				}
			}

			protected File askCommandUIExecutableLocation() {
				FileDialog fd = new FileDialog((JFrame) null, "Locate the '"
						+ APP_NAME + "' executable file:", FileDialog.LOAD);
				fd.setVisible(true);
				if (fd.getFile() == null) {
					return null;
				}
				return new File(fd.getDirectory(), fd.getFile());
			}

			@Override
			public String getCaption() {
				return "Generate GUI";
			}

			@Override
			public ITypeInfo getReturnValueType() {
				return null;
			}

			@Override
			public Map<String, Object> getSpecificProperties() {
				return Collections.emptyMap();
			}

			@Override
			public String getName() {
				return "generate";
			}

			@Override
			public String getOnlineHelp() {
				return "Builds an executable file for the GUI of the current command line specification";
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
							public String getOnlineHelp() {
								return null;
							}

							@Override
							public Map<String, Object> getSpecificProperties() {
								return Collections.emptyMap();
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
					InvocationData invocationData) {
				return null;
			}

			@Override
			public void validateParameters(Object object,
					InvocationData invocationData) throws Exception {
			}

		};
	}

	protected IMethodInfo getValidateMethod() {
		return new IMethodInfo() {
			@Override
			public Object invoke(Object object, InvocationData invocationData) {
				CommandLine commandLine = (CommandLine) object;
				try {
					commandLine.validate();
				} catch (Exception e) {
					throw new ReflectionUIError(e);
				}
				for (JPanel form : getSwingRenderer().getForms(commandLine)) {
					for (Component fieldControl : ((ReflectionUI) CommandLineEditor.this)
							.getSwingRenderer().getFieldControlsByName(form, "arguments")) {
						if (fieldControl instanceof ListControl) {
							final ListControl listControl = (ListControl) fieldControl;
							listControl
									.visitItems(new ListControl.IItemsVisitor() {
										@Override
										public void visitItem(
												ListControl.AutoUpdatingFieldItemPosition itemPosition) {
											Object item = itemPosition
													.getItem();
											try {
												if (item instanceof ArgumentPage) {
													((ArgumentPage) item)
															.validate();
												} else if (item instanceof AbstractCommandLinePart) {
													((AbstractCommandLinePart) item)
															.validate();
												}
											} catch (Exception e) {
												listControl
														.scrollRectToVisible(listControl
																.getBounds());
												listControl
														.setSingleSelection(itemPosition);
												ITypeInfo itemType = getTypeInfo(getTypeInfoSource(item));
												throw new ReflectionUIError(
														"Invalid "
																+ itemType
																		.getCaption()
																+ ": " + e, e);
											}
										}
									});
						}
					}
				}
				return null;
			}

			@Override
			public String getCaption() {
				return "Validate";
			}

			@Override
			public ITypeInfo getReturnValueType() {
				return null;
			}

			@Override
			public Map<String, Object> getSpecificProperties() {
				return Collections.emptyMap();
			}

			@Override
			public String getName() {
				return "validate";
			}

			@Override
			public String getOnlineHelp() {
				return "validate the command line specification";
			}

			@Override
			public List<IParameterInfo> getParameters() {
				return Collections.<IParameterInfo> emptyList();
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
					InvocationData invocationData) {
				return null;
			}

			@Override
			public void validateParameters(Object object,
					InvocationData invocationData) throws Exception {
			}

		};
	}
}
