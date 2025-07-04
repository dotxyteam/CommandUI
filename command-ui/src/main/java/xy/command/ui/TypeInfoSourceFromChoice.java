package xy.command.ui;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import xy.command.instance.CommandLineInstance;
import xy.command.model.ArgumentGroup;
import xy.command.model.Choice;
import xy.reflect.ui.ReflectionUI;
import xy.reflect.ui.info.ColorSpecification;
import xy.reflect.ui.info.ITransaction;
import xy.reflect.ui.info.ResourcePath;
import xy.reflect.ui.info.ValidationSession;
import xy.reflect.ui.info.field.IFieldInfo;
import xy.reflect.ui.info.menu.MenuModel;
import xy.reflect.ui.info.method.IMethodInfo;
import xy.reflect.ui.info.type.ITypeInfo;
import xy.reflect.ui.info.type.source.ITypeInfoSource;
import xy.reflect.ui.info.type.source.SpecificitiesIdentifier;

/**
 * Specifies how a {@link Choice} children should be displayed in a GUI.
 * 
 * @author olitank
 *
 */
public class TypeInfoSourceFromChoice implements ITypeInfoSource {

	public class TypeInfoFromChoice implements ITypeInfo {
		private final CommandLineUI commandLineUI;

		private TypeInfoFromChoice(CommandLineUI commandLineUI) {
			this.commandLineUI = commandLineUI;
		}

		@Override
		public boolean isValidationRequired() {
			return false;
		}

		@Override
		public ITransaction createTransaction(Object object) {
			return null;
		}

		@Override
		public void onFormRefresh(Object object) {
		}

		@Override
		public Runnable getLastFormRefreshStateRestorationJob(Object object) {
			return null;
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
			return choice.getClass().getName() + choice.hashCode();
		}

		@Override
		public String getCaption() {
			return "";
		}

		@Override
		public void validate(Object object, ValidationSession session) throws Exception {
		}

		@Override
		public String toString(Object object) {
			return "";
		}

		@Override
		public boolean supports(Object object) {
			return object instanceof CommandLineInstance;
		}

		@Override
		public boolean onFormVisibilityChange(Object object, boolean visible) {
			return false;
		}

		@Override
		public void save(Object object, File outputFile) {
			throw new UnsupportedOperationException();
		}

		@Override
		public void load(Object object, File inputFile) {
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
			for (ArgumentGroup argumentGroup : choice.options) {
				ITypeInfo type = commandLineUI.getTypeInfo(new TypeInfoSourceFromArgumentGroup(argumentGroup, null));
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
		public ResourcePath getIconImagePath(Object object) {
			return null;
		}

		@Override
		public int getFormPreferredWidth() {
			return -1;
		}

		@Override
		public int getFormPreferredHeight() {
			return -1;
		}

		@Override
		public int getFormSpacing() {
			return ITypeInfo.DEFAULT_FORM_SPACING;
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
		public ColorSpecification getFormEditorForegroundColor() {
			return null;
		}

		@Override
		public ColorSpecification getFormEditorBackgroundColor() {
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
			return Collections.emptyList();
		}

		@Override
		public List<IMethodInfo> getConstructors() {
			return Collections.emptyList();
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
	}

	private CommandLineUI commandLineUI;
	private SpecificitiesIdentifier specificitiesIdentifier;
	private Choice choice;

	public TypeInfoSourceFromChoice(CommandLineUI commandLineUI, SpecificitiesIdentifier specificitiesIdentifier,
			Choice choice) {
		this.commandLineUI = commandLineUI;
		this.specificitiesIdentifier = specificitiesIdentifier;
		this.choice = choice;
	}

	@Override
	public SpecificitiesIdentifier getSpecificitiesIdentifier() {
		return specificitiesIdentifier;
	}

	@Override
	public ITypeInfo buildTypeInfo(ReflectionUI reflectionUI) {
		return new TypeInfoFromChoice(commandLineUI);
	}

}
