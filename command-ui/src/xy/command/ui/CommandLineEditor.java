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
import xy.reflect.ui.info.type.HiddenNullableFacetsTypeInfoProxy;
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
		return new HiddenNullableFacetsTypeInfoProxy(CommandLineEditor.this) {

			@Override
			protected String getTypeCaption(ITypeInfo type) {
				if (type.getName().equals(
						AbstractCommandLinePart.class.getName())) {
					return "Command Line part";
				} else {
					return super.getTypeCaption(type);
				}
			}

			@Override
			protected List<ITypeInfo> getTypePolymorphicInstanceSubTypes(
					ITypeInfo type) {
				if (type.getName().equals(
						AbstractCommandLinePart.class.getName())) {
					return Arrays.asList(getTypeInfo(new JavaTypeInfoSource(
							InputArgument.class)),
							getTypeInfo(new JavaTypeInfoSource(
									OptionalPart.class)),
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
					return super.getTypePolymorphicInstanceSubTypes(type);
				}
			}

			@Override
			protected List<IMethodInfo> getTypeMethods(ITypeInfo type) {
				if (type.getName().startsWith(
						CommandLine.class.getPackage().getName())
						&& !type.getName().contains("$")) {
					List<IMethodInfo> result = new ArrayList<IMethodInfo>(
							super.getTypeMethods(type));
					result.remove(ReflectionUIUtils.findInfoByName(result,
							"createInstance"));
					return result;
				} else {
					return super.getTypeMethods(type);
				}
			}

			@Override
			protected ITypeInfo getFieldType(IFieldInfo field,
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
						&& field.getName().equals("pages")) {
					return new TypeInfoProxy() {

						@Override
						protected IListStructuralInfo getListTypeStructuralInfo(
								IListTypeInfo type) {
							return new DefaultListStructuralInfo(
									CommandLineEditor.this, type.getItemType()) {

								@Override
								protected boolean isTabular() {
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
					return super.getFieldType(field, containingType);
				}
			}

		}.get(super.getTypeInfo(typeSource));
	}
}
