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
import xy.command.model.Choice;
import xy.command.model.CommandLine;
import xy.command.model.DirectoryArgument;
import xy.command.model.FileArgument;
import xy.command.model.FixedArgument;
import xy.command.model.InputArgument;
import xy.command.model.OptionalPart;
import xy.reflect.ui.ReflectionUI;
import xy.reflect.ui.info.field.FieldInfoProxy;
import xy.reflect.ui.info.field.IFieldInfo;
import xy.reflect.ui.info.method.IMethodInfo;
import xy.reflect.ui.info.type.ITypeInfo;
import xy.reflect.ui.info.type.ITypeInfoSource;
import xy.reflect.ui.info.type.JavaTypeInfoSource;
import xy.reflect.ui.info.type.SimpleTypeInfoProxy;
import xy.reflect.ui.info.type.StandardListTypeInfo;
import xy.reflect.ui.info.type.StandardMapListTypeInfo;
import xy.reflect.ui.util.ReflectionUIUtils;

;

public class CommandLineEditor extends ReflectionUI {

	public static final String COMMAND_LINE_FILE_EXTENSION = "cml";

	public static void main(String[] args) {
		new CommandLineEditor().openObjectFrame(new CommandLine(), "",
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
	public ITypeInfo getTypeInfo(ITypeInfoSource typeSource) {
		if (typeSource instanceof JavaTypeInfoSource) {
			final JavaTypeInfoSource classTypeSource = (JavaTypeInfoSource) typeSource;
			if (classTypeSource.getJavaType().equals(
					AbstractCommandLinePart.class)) {

				return new SimpleTypeInfoProxy(
						super.getTypeInfo(typeSource)) {

					@Override
					public String getCaption() {
						return "Command Line part";
					}

					@Override
					public List<ITypeInfo> getPolymorphicInstanceTypes() {
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

				};

			} else if (ReflectionUIUtils
					.equalsOrBothNull(classTypeSource.getJavaType()
							.getPackage(), CommandLine.class.getPackage())
					&& (classTypeSource.getJavaType().getEnclosingClass() == null)) {

				return new SimpleTypeInfoProxy(
						super.getTypeInfo(typeSource)) {

					@Override
					public List<IMethodInfo> getMethods() {
						List<IMethodInfo> result = new ArrayList<IMethodInfo>(
								super.getMethods());
						result.remove(ReflectionUIUtils.findInfoByName(result,
								"createInstance"));
						result.remove(ReflectionUIUtils.findInfoByName(result,
								"getFieldInfo"));
						return result;
					}

					@Override
					public List<IFieldInfo> getFields() {
						if (classTypeSource.getJavaType().equals(Choice.class)) {
							List<IFieldInfo> result = new ArrayList<IFieldInfo>(
									super.getFields());
							IFieldInfo optionsField = ReflectionUIUtils
									.findInfoByName(result, "options");
							result.remove(optionsField);
							result.add(new FieldInfoProxy(optionsField) {

								@Override
								public ITypeInfo getType() {
									return new StandardMapListTypeInfo(
											CommandLineEditor.this,
											HashMap.class, String.class,
											List.class) {

										@Override
										public StandardMapEntryTypeInfo getItemType() {
											return new StandardMapEntryTypeInfo() {

												@Override
												public List<IFieldInfo> getFields() {
													List<IFieldInfo> result = new ArrayList<IFieldInfo>();
													for (IFieldInfo field : super
															.getFields()) {
														if (field.getName()
																.equals("key")) {
															field = new FieldInfoProxy(
																	field) {

																@Override
																public String getCaption() {
																	return "Title";
																}

															};
														} else if (field
																.getName()
																.equals("value")) {
															field = new FieldInfoProxy(
																	field) {

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
															};
														}
														result.add(field);
													}
													return result;
												}

												@Override
												public ITypeInfo getValueType() {
													return new StandardListTypeInfo(
															CommandLineEditor.this,
															List.class,
															AbstractCommandLinePart.class);
												}

											};
										}

									};
								}

							});
							return result;
						} else {
							return super.getFields();
						}
					}

				};

			} else {
				return super.getTypeInfo(typeSource);
			}
		} else {
			return super.getTypeInfo(typeSource);
		}
	}
}
