package xy.command.ui;

import java.awt.Image;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import javax.imageio.ImageIO;

import xy.command.model.AbstractCommandLinePart;
import xy.command.model.ArgumentGroup;
import xy.command.model.ArgumentPage;
import xy.command.model.Choice;
import xy.command.model.CommandLine;
import xy.command.model.DirectoryArgument;
import xy.command.model.FileArgument;
import xy.command.model.FixedArgument;
import xy.command.model.InputArgument;
import xy.command.model.OptionalPart;
import xy.reflect.ui.ReflectionUI;
import xy.reflect.ui.info.FieldInfoProxy;
import xy.reflect.ui.info.field.IFieldInfo;
import xy.reflect.ui.info.method.IMethodInfo;
import xy.reflect.ui.info.type.DefaultListStructuralInfo;
import xy.reflect.ui.info.type.IListTypeInfo;
import xy.reflect.ui.info.type.IListTypeInfo.IItemPosition;
import xy.reflect.ui.info.type.IListTypeInfo.IListStructuralInfo;
import xy.reflect.ui.info.type.IMapEntryTypeInfo;
import xy.reflect.ui.info.type.ITypeInfo;
import xy.reflect.ui.info.type.ITypeInfoSource;
import xy.reflect.ui.info.type.JavaTypeInfoSource;
import xy.reflect.ui.info.type.StandardCollectionTypeInfo;
import xy.reflect.ui.info.type.StandardMapListTypeInfo;
import xy.reflect.ui.info.type.TypeInfoProxy;
import xy.reflect.ui.util.ReflectionUIUtils;

;

public class CopyOfCommandLineEditor extends ReflectionUI {

	public static final String COMMAND_LINE_FILE_EXTENSION = "cml";

	public static void main(String[] args) {
		new CopyOfCommandLineEditor().openObjectFrame(new CommandLine(), "",
				getClassIconImage(CommandLine.class));
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
		if (typeSource instanceof JavaTypeInfoSource) {
			final JavaTypeInfoSource classTypeSource = (JavaTypeInfoSource) typeSource;
			if (classTypeSource.getJavaType().equals(
					AbstractCommandLinePart.class)) {

				return new TypeInfoProxy() {

					
					@Override
					protected String getTypeCaption(ITypeInfo type) {
						return "Command Line part";
					}

					@Override
					public List<ITypeInfo> getTypePolymorphicInstanceTypes(ITypeInfo type) {
						return Arrays
								.asList(getTypeInfo(new JavaTypeInfoSource(
										InputArgument.class)),
										getTypeInfo(new JavaTypeInfoSource(
												OptionalPart.class)),
										getTypeInfo(new JavaTypeInfoSource(
												Choice.class)),
										getTypeInfo(new JavaTypeInfoSource(
												FixedArgument.class)),
										getTypeInfo(new JavaTypeInfoSource(
												FileArgument.class)),
										getTypeInfo(new JavaTypeInfoSource(
												DirectoryArgument.class)),
										getTypeInfo(new JavaTypeInfoSource(
												ArgumentGroup.class)));
					}

				}.get(super.getTypeInfo(typeSource));

			} else if (ReflectionUIUtils
					.equalsOrBothNull(classTypeSource.getJavaType()
							.getPackage(), CommandLine.class.getPackage())
					&& (classTypeSource.getJavaType().getEnclosingClass() == null)) {

				return new TypeInfoProxy() {

					@Override
					public List<IMethodInfo> getTypeMethods(ITypeInfo type) {
						List<IMethodInfo> result = new ArrayList<IMethodInfo>(
								type.getMethods());
						result.remove(ReflectionUIUtils.findInfoByName(result,
								"createInstance"));
						return result;
					}

					@Override
					public List<IFieldInfo> getTypeFields(ITypeInfo type) {
						if (classTypeSource.getJavaType().equals(Choice.class)) {
							List<IFieldInfo> result = new ArrayList<IFieldInfo>(
									type.getFields());
							IFieldInfo optionsField = ReflectionUIUtils
									.findInfoByName(result, "options");
							result.remove(optionsField);
							result.add(new FieldInfoProxy(optionsField) {

								@Override
								public ITypeInfo getType() {
									return new StandardMapListTypeInfo(
											CopyOfCommandLineEditor.this,
											HashMap.class, String.class,
											List.class) {

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
														public Object getValue(
																Object object) {
															ArgumentGroup group = (ArgumentGroup) super
																	.getValue(object);
															if (group == null) {
																return null;
															}
															return group.parts;
														}

														@Override
														public void setValue(
																Object object,
																Object value) {
															@SuppressWarnings("unchecked")
															List<AbstractCommandLinePart> parts = (List<AbstractCommandLinePart>) value;
															ArgumentGroup group;
															if (parts == null) {
																group = null;
															}
															group = new ArgumentGroup();
															group.parts = parts;
															super.setValue(
																	object,
																	group);
														}

														@Override
														public ITypeInfo getType() {
															return new StandardCollectionTypeInfo(
																	CopyOfCommandLineEditor.this,
																	List.class,
																	AbstractCommandLinePart.class);
														}

													};
												}

											};
										}

									};
								}

							});
							return result;
						} else if (classTypeSource.getJavaType().equals(
								CommandLine.class)) {
							List<IFieldInfo> result = new ArrayList<IFieldInfo>(
									type.getFields());
							IFieldInfo pagesField = ReflectionUIUtils
									.findInfoByName(result, "pages");
							result.remove(pagesField);
							result.add(new FieldInfoProxy(pagesField) {

								@Override
								public ITypeInfo getType() {
									return new TypeInfoProxy() {										
										
										@Override
										protected IListStructuralInfo getListTypeStructuralInfo(
												IListTypeInfo type) {
											return new DefaultListStructuralInfo(
													CopyOfCommandLineEditor.this,
													type.getItemType()) {

												@Override
												protected boolean isTabular() {
													return false;
												}

												@Override
												public int getColumnCount() {
													return 2;
												}

												@Override
												public String getColumnCaption(
														int columnIndex) {
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
													Object item = itemPosition
															.getItem();
													ITypeInfo type = getTypeInfo(getTypeInfoSource(item));
													if (columnIndex == 0) {
														if (type instanceof IMapEntryTypeInfo) {
															Object key = ((IMapEntryTypeInfo) type)
																	.getKeyField()
																	.getValue(
																			item);
															return (key == null) ? ""
																	: CopyOfCommandLineEditor.this
																			.toString(key);
														} else {
															return type
																	.getCaption();
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

									}.get((IListTypeInfo) super.getType());
								}

							});
							return result;
						} else {
							return type.getFields();
						}
					}

				}.get(super.getTypeInfo(typeSource));

			} else {
				return super.getTypeInfo(typeSource);
			}
		} else {
			return super.getTypeInfo(typeSource);
		}
	}
}
