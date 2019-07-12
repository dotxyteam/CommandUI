package xy.command.ui;

import java.awt.Dimension;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import xy.command.instance.CommandLineInstance;
import xy.command.model.ArgumentPage;
import xy.command.model.Choice;
import xy.command.model.CommandLine;
import xy.reflect.ui.ReflectionUI;
import xy.reflect.ui.info.ColorSpecification;
import xy.reflect.ui.info.ResourcePath;
import xy.reflect.ui.info.field.IFieldInfo;
import xy.reflect.ui.info.menu.MenuModel;
import xy.reflect.ui.info.method.IMethodInfo;
import xy.reflect.ui.info.type.ITypeInfo;
import xy.reflect.ui.info.type.source.ITypeInfoSource;
import xy.reflect.ui.info.type.source.SpecificitiesIdentifier;

public class TypeInfoSourceFromChoice implements ITypeInfoSource {

		private SpecificitiesIdentifier specificitiesIdentifier;
	private Choice choice;
	private ArgumentPage argumentPage;
	
	public TypeInfoSourceFromChoice(SpecificitiesIdentifier specificitiesIdentifier,
			Choice choice, ArgumentPage argumentPage) {
		this.specificitiesIdentifier = specificitiesIdentifier;
		this.choice = choice;
		this.argumentPage = argumentPage;
	}

	@Override
	public SpecificitiesIdentifier getSpecificitiesIdentifier() {
		return specificitiesIdentifier;
	}

	@Override
	public ITypeInfo getTypeInfo(ReflectionUI reflectionUI) {
		return new ITypeInfo() {

			@Override
			public Map<String, Object> getSpecificProperties() {
				return Collections.emptyMap();
			}

			@Override
			public String getOnlineHelp() {
				return null;
			}

			@Override
			public String getName() {
				int indexInArgumentPage = argumentPage.parts.indexOf(choice);
				return argumentPage.title + " - " + indexInArgumentPage + " - type";
			}

			@Override
			public String getCaption() {
				return "";
			}

			@Override
			public void validate(Object object) throws Exception {
			}

			@Override
			public String toString(Object object) {
				return "";
			}

			@Override
			public boolean supportsInstance(Object object) {
				return object instanceof CommandLineInstance;
			}

			@Override
			public void save(Object object, OutputStream out) {
				throw new UnsupportedOperationException();
			}

			@Override
			public boolean onFormVisibilityChange(Object object, boolean visible) {
				return false;
			}

			@Override
			public void load(Object object, InputStream in) {
				throw new UnsupportedOperationException();
			}

			@Override
			public boolean isPrimitive() {
				return false;
			}

			@Override
			public boolean isModificationStackAccessible() {
				return false;
			}

			@Override
			public boolean isImmutable() {
				return false;
			}

			@Override
			public boolean isConcrete() {
				return false;
			}

			@Override
			public ITypeInfoSource getSource() {
				return TypeInfoSourceFromChoice.this;
			}

			@Override
			public List<ITypeInfo> getPolymorphicInstanceSubTypes() {
				List<ITypeInfo> result = new ArrayList<ITypeInfo>();
				for (CommandLine cl : choice.options) {
					ITypeInfo type = reflectionUI
							.getTypeInfo(new TypeInfoSourceFromCommandLine(cl, null));
					result.add(type);
				}
				return result;
			}

			@Override
			public MethodsLayout getMethodsLayout() {
				return MethodsLayout.HORIZONTAL_FLOW;
			}

			@Override
			public List<IMethodInfo> getMethods() {
				return Collections.emptyList();
			}

			@Override
			public MenuModel getMenuModel() {
				return new MenuModel();
			}

			@Override
			public ResourcePath getIconImagePath() {
				return null;
			}

			@Override
			public Dimension getFormPreferredSize() {
				return null;
			}

			@Override
			public ColorSpecification getFormForegroundColor() {
				return null;
			}

			@Override
			public ResourcePath getFormBackgroundImagePath() {
				return null;
			}

			@Override
			public ColorSpecification getFormBackgroundColor() {
				return null;
			}

			@Override
			public FieldsLayout getFieldsLayout() {
				return FieldsLayout.VERTICAL_FLOW;
			}

			@Override
			public List<IFieldInfo> getFields() {
				return Collections.emptyList();
			}

			@Override
			public List<IMethodInfo> getConstructors() {
				return Collections.emptyList();
			}

			@Override
			public CategoriesStyle getCategoriesStyle() {
				return CategoriesStyle.CLASSIC;
			}

			@Override
			public Object copy(Object object) {
				throw new UnsupportedOperationException();
			}

			@Override
			public boolean canPersist() {
				return false;
			}

			@Override
			public boolean canCopy(Object object) {
				return false;
			}
		};
	}

}
