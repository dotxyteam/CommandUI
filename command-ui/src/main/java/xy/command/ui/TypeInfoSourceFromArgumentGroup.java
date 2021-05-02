package xy.command.ui;

import java.awt.Dimension;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import xy.command.instance.ArgumentGroupInstance;
import xy.command.model.AbstractCommandLinePart;
import xy.command.model.ArgumentGroup;
import xy.command.model.ArgumentPage;
import xy.command.model.Choice;
import xy.command.model.DirectoryArgument;
import xy.command.model.FileArgument;
import xy.command.model.FixedArgument;
import xy.command.model.InputArgument;
import xy.command.model.MultiplePart;
import xy.command.model.OptionalPart;
import xy.reflect.ui.ReflectionUI;
import xy.reflect.ui.info.ColorSpecification;
import xy.reflect.ui.info.ResourcePath;
import xy.reflect.ui.info.field.IFieldInfo;
import xy.reflect.ui.info.menu.MenuModel;
import xy.reflect.ui.info.method.AbstractConstructorInfo;
import xy.reflect.ui.info.method.IMethodInfo;
import xy.reflect.ui.info.method.InvocationData;
import xy.reflect.ui.info.type.ITypeInfo;
import xy.reflect.ui.info.type.source.ITypeInfoSource;
import xy.reflect.ui.info.type.source.SpecificitiesIdentifier;
import xy.reflect.ui.util.ReflectionUIError;

/**
 * Specifies how an {@link ArgumentGroup} should be displayed in a GUI.
 * 
 * @author olitank
 *
 */
public class TypeInfoSourceFromArgumentGroup implements ITypeInfoSource {

	private ArgumentGroup argumentGroup;
	private SpecificitiesIdentifier specificitiesIdentifier;

	public TypeInfoSourceFromArgumentGroup(ArgumentGroup argumentGroup,
			SpecificitiesIdentifier specificitiesIdentifier) {
		this.argumentGroup = argumentGroup;
		this.specificitiesIdentifier = specificitiesIdentifier;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((argumentGroup == null) ? 0 : argumentGroup.hashCode());
		result = prime * result + ((specificitiesIdentifier == null) ? 0 : specificitiesIdentifier.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		TypeInfoSourceFromArgumentGroup other = (TypeInfoSourceFromArgumentGroup) obj;
		if (argumentGroup == null) {
			if (other.argumentGroup != null)
				return false;
		} else if (!argumentGroup.equals(other.argumentGroup))
			return false;
		if (specificitiesIdentifier == null) {
			if (other.specificitiesIdentifier != null)
				return false;
		} else if (!specificitiesIdentifier.equals(other.specificitiesIdentifier))
			return false;
		return true;
	}

	@Override
	public SpecificitiesIdentifier getSpecificitiesIdentifier() {
		return specificitiesIdentifier;
	}

	@Override
	public ITypeInfo getTypeInfo(ReflectionUI reflectionUI) {
		return new TypeInfoFromArgumentGroup(reflectionUI);
	}

	protected IFieldInfo getFieldInfoFromCommandLinePart(ReflectionUI reflectionUI, AbstractCommandLinePart part,
			ArgumentPage argumentPage, ITypeInfo commandLineTypeInfo) {
		if (part instanceof FixedArgument) {
			return null;
		} else if (part instanceof InputArgument) {
			return new FieldInfoFromInputArgument(reflectionUI, (InputArgument) part, null, argumentGroup,
					commandLineTypeInfo);
		} else if (part instanceof DirectoryArgument) {
			return new FieldInfoFromDirectoryArgument(reflectionUI, (DirectoryArgument) part, null, argumentGroup,
					commandLineTypeInfo);
		} else if (part instanceof FileArgument) {
			return new FieldInfoFromFileArgument(reflectionUI, (FileArgument) part, null, argumentGroup,
					commandLineTypeInfo);
		} else if (part instanceof Choice) {
			return new FieldInfoFromChoice(reflectionUI, (Choice) part, null, argumentGroup, commandLineTypeInfo);
		} else if (part instanceof OptionalPart) {
			return new FieldInfoFromOptionalPart(reflectionUI, (OptionalPart) part, null, argumentGroup,
					commandLineTypeInfo);
		} else if (part instanceof MultiplePart) {
			return new FieldInfoFromMultiplePart(reflectionUI, (MultiplePart) part, null, argumentGroup,
					commandLineTypeInfo);
		} else {
			throw new ReflectionUIError();
		}
	}

	public class TypeInfoFromArgumentGroup implements ITypeInfo {

		private ReflectionUI reflectionUI;

		public TypeInfoFromArgumentGroup(ReflectionUI reflectionUI) {
			this.reflectionUI = reflectionUI;
		}

		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + getOuterType().hashCode();
			return result;
		}

		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			TypeInfoFromArgumentGroup other = (TypeInfoFromArgumentGroup) obj;
			if (!getOuterType().equals(other.getOuterType()))
				return false;
			return true;
		}

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
			return argumentGroup.getClass().getName() + argumentGroup.hashCode();
		}

		@Override
		public String getCaption() {
			return argumentGroup.title;
		}

		@Override
		public void validate(Object object) throws Exception {
		}

		@Override
		public String toString(Object object) {
			return object.toString();
		}

		@Override
		public boolean supportsInstance(Object object) {
			return (object instanceof ArgumentGroupInstance)
					&& (((ArgumentGroupInstance) object).model == argumentGroup);
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
			return true;
		}

		@Override
		public boolean isImmutable() {
			return false;
		}

		@Override
		public boolean isConcrete() {
			return true;
		}

		@Override
		public ITypeInfoSource getSource() {
			return TypeInfoSourceFromArgumentGroup.this;
		}

		@Override
		public List<ITypeInfo> getPolymorphicInstanceSubTypes() {
			return Collections.emptyList();
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
		public ColorSpecification getFormBorderColor() {
			return null;
		}

		@Override
		public ColorSpecification getFormEditorsForegroundColor() {
			return null;
		}

		@Override
		public ColorSpecification getFormEditorsBackgroundColor() {
			return null;
		}

		@Override
		public ColorSpecification getFormButtonBackgroundColor() {
			return null;
		}

		@Override
		public ColorSpecification getFormButtonForegroundColor() {
			return null;
		}

		@Override
		public ResourcePath getFormButtonBackgroundImagePath() {
			return null;
		}

		@Override
		public ColorSpecification getFormButtonBorderColor() {
			return null;
		}

		@Override
		public ColorSpecification getCategoriesBackgroundColor() {
			return null;
		}

		@Override
		public ColorSpecification getCategoriesForegroundColor() {
			return null;
		}

		@Override
		public FieldsLayout getFieldsLayout() {
			return FieldsLayout.VERTICAL_FLOW;
		}

		@Override
		public List<IFieldInfo> getFields() {
			List<IFieldInfo> result = new ArrayList<IFieldInfo>();
			for (AbstractCommandLinePart part : argumentGroup.parts) {
				IFieldInfo field = getFieldInfoFromCommandLinePart(reflectionUI, part, null, this);
				if (field == null) {
					continue;
				}
				result.add(field);
			}
			return result;
		}

		@Override
		public List<IMethodInfo> getConstructors() {
			return Collections.<IMethodInfo>singletonList(new AbstractConstructorInfo() {

				@Override
				public Object invoke(Object parentObject, InvocationData invocationData) {
					return new ArgumentGroupInstance(argumentGroup);
				}

				@Override
				public ITypeInfo getReturnValueType() {
					return reflectionUI.getTypeInfo(TypeInfoSourceFromArgumentGroup.this);
				}

			});
		}

		@Override
		public CategoriesStyle getCategoriesStyle() {
			return CategoriesStyle.MODERN;
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

		private TypeInfoSourceFromArgumentGroup getOuterType() {
			return TypeInfoSourceFromArgumentGroup.this;
		}
	}

}
