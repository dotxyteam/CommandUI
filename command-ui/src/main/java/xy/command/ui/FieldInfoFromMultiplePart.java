package xy.command.ui;

import java.awt.Dimension;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import xy.command.instance.CommandLineInstance;
import xy.command.instance.MultiplePartInstance;
import xy.command.model.ArgumentPage;
import xy.command.model.CommandLine;
import xy.command.model.MultiplePart;
import xy.reflect.ui.ReflectionUI;
import xy.reflect.ui.info.ColorSpecification;
import xy.reflect.ui.info.InfoCategory;
import xy.reflect.ui.info.ResourcePath;
import xy.reflect.ui.info.ValueReturnMode;
import xy.reflect.ui.info.field.IFieldInfo;
import xy.reflect.ui.info.filter.IInfoFilter;
import xy.reflect.ui.info.menu.MenuModel;
import xy.reflect.ui.info.method.IMethodInfo;
import xy.reflect.ui.info.type.ITypeInfo;
import xy.reflect.ui.info.type.iterable.IListTypeInfo;
import xy.reflect.ui.info.type.iterable.item.DetachedItemDetailsAccessMode;
import xy.reflect.ui.info.type.iterable.item.IListItemDetailsAccessMode;
import xy.reflect.ui.info.type.iterable.item.ItemPosition;
import xy.reflect.ui.info.type.iterable.structure.DefaultListStructuralInfo;
import xy.reflect.ui.info.type.iterable.structure.IListStructuralInfo;
import xy.reflect.ui.info.type.iterable.util.IDynamicListAction;
import xy.reflect.ui.info.type.iterable.util.IDynamicListProperty;
import xy.reflect.ui.info.type.source.ITypeInfoSource;
import xy.reflect.ui.info.type.source.PrecomputedTypeInfoSource;
import xy.reflect.ui.info.type.source.SpecificitiesIdentifier;
import xy.reflect.ui.undo.ListModificationFactory;
import xy.reflect.ui.util.Mapper;

public class FieldInfoFromMultiplePart implements IFieldInfo {

	private MultiplePart multiplePart;
	private ArgumentPage argumentPage;
	private CommandLine commandLine;
	private ReflectionUI reflectionUI;
	private ITypeInfo commandLineTypeInfo;

	public FieldInfoFromMultiplePart(ReflectionUI reflectionUI, MultiplePart multiplePart, ArgumentPage argumentPage,
			CommandLine commandLine, ITypeInfo commandLineTypeInfo) {
		this.reflectionUI = reflectionUI;
		this.multiplePart = multiplePart;
		this.argumentPage = argumentPage;
		this.commandLine = commandLine;
		this.commandLineTypeInfo = commandLineTypeInfo;
	}

	@Override
	public String getName() {
		int indexInArgumentPage = argumentPage.parts.indexOf(multiplePart);
		return argumentPage.title + " - " + indexInArgumentPage;
	}

	@Override
	public String getCaption() {
		return multiplePart.title;
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
	public ITypeInfo getType() {
		return new IListTypeInfo() {

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
				return FieldInfoFromMultiplePart.this.getName() + " - type";
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
				return object.toString();
			}

			@Override
			public boolean supportsInstance(Object object) {
				return object instanceof CommandLineInstance[];
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
				return true;
			}

			@Override
			public ITypeInfoSource getSource() {
				return new PrecomputedTypeInfoSource(this,
						new SpecificitiesIdentifier(commandLineTypeInfo.getName(), getName()));
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

			@Override
			public void replaceContent(Object listValue, Object[] array) {
			}

			@Override
			public void onSelection(List<? extends ItemPosition> newSelection) {
			}

			@Override
			public boolean isRemovalAllowed() {
				return true;
			}

			@Override
			public boolean isOrdered() {
				return false;
			}

			@Override
			public boolean isItemNullValueDistinct() {
				return false;
			}

			@Override
			public boolean isItemConstructorSelectable() {
				return false;
			}

			@Override
			public boolean isInsertionAllowed() {
				return true;
			}

			@Override
			public IListStructuralInfo getStructuralInfo() {
				return new DefaultListStructuralInfo(reflectionUI);
			}

			@Override
			public ITypeInfo getItemType() {
				return reflectionUI.getTypeInfo(new TypeInfoSourceFromCommandLine(multiplePart, null));
			}

			@Override
			public ValueReturnMode getItemReturnMode() {
				return ValueReturnMode.DIRECT_OR_PROXY;
			}

			@Override
			public List<IDynamicListProperty> getDynamicProperties(List<? extends ItemPosition> selection,
					Mapper<ItemPosition, ListModificationFactory> listModificationFactoryAccessor) {
				return Collections.emptyList();
			}

			@Override
			public List<IDynamicListAction> getDynamicActions(List<? extends ItemPosition> selection,
					Mapper<ItemPosition, ListModificationFactory> listModificationFactoryAccessor) {
				return Collections.emptyList();
			}

			@Override
			public IListItemDetailsAccessMode getDetailsAccessMode() {
				return new DetachedItemDetailsAccessMode();
			}

			@Override
			public Object fromArray(Object[] array) {
				CommandLineInstance[] result = new CommandLineInstance[array.length];
				for (int i = 0; i < array.length; i++) {
					result[i] = (CommandLineInstance) array[i];
				}
				return result;
			}

			@Override
			public Object[] toArray(Object listValue) {
				CommandLineInstance[] array = (CommandLineInstance[]) listValue;
				Object[] result = new Object[array.length];
				for (int i = 0; i < array.length; i++) {
					result[i] = array[i];
				}
				return result;
			}

			@Override
			public boolean canViewItemDetails() {
				return true;
			}

			@Override
			public boolean canReplaceContent() {
				return false;
			}

			@Override
			public boolean canInstanciateFromArray() {
				return true;
			}
		};

	}

	@Override
	public Object getValue(Object object) {
		CommandLineInstance commandLineInstance = (CommandLineInstance) object;
		CommandLine commandLine = commandLineInstance.model;
		int argumentPageIndex = commandLine.arguments.indexOf(argumentPage);
		int indexInArgumentPage = argumentPage.parts.indexOf(multiplePart);
		MultiplePartInstance multiplePartInstance = (MultiplePartInstance) commandLineInstance.argumentPageInstances
				.get(argumentPageIndex).partInstances.get(indexInArgumentPage);
		return multiplePartInstance.commandLineInstances;
	}

	@Override
	public void setValue(Object object, Object value) {
		CommandLineInstance commandLineInstance = (CommandLineInstance) object;
		CommandLine commandLine = commandLineInstance.model;
		int argumentPageIndex = commandLine.arguments.indexOf(argumentPage);
		int indexInArgumentPage = argumentPage.parts.indexOf(multiplePart);
		MultiplePartInstance multiplePartInstance = (MultiplePartInstance) commandLineInstance.argumentPageInstances
				.get(argumentPageIndex).partInstances.get(indexInArgumentPage);
		multiplePartInstance.commandLineInstances = (CommandLineInstance[]) value;
	}

	@Override
	public Object[] getValueOptions(Object object) {
		return null;
	}

	@Override
	public Runnable getNextUpdateCustomUndoJob(Object object, Object newValue) {
		return null;
	}

	@Override
	public boolean isNullValueDistinct() {
		return false;
	}

	@Override
	public boolean isGetOnly() {
		return false;
	}

	@Override
	public String getNullValueLabel() {
		return null;
	}

	@Override
	public ValueReturnMode getValueReturnMode() {
		return ValueReturnMode.CALCULATED;
	}

	@Override
	public InfoCategory getCategory() {
		int argumentPageIndex = commandLine.arguments.indexOf(argumentPage);
		return new InfoCategory(argumentPage.title, argumentPageIndex);
	}

	@Override
	public boolean isFormControlMandatory() {
		return false;
	}

	@Override
	public boolean isFormControlEmbedded() {
		return false;
	}

	@Override
	public IInfoFilter getFormControlFilter() {
		return IInfoFilter.DEFAULT;
	}

	@Override
	public long getAutoUpdatePeriodMilliseconds() {
		return -1;
	}

	@Override
	public boolean isHidden() {
		return false;
	}

	@Override
	public double getDisplayAreaHorizontalWeight() {
		return 0;
	}

	@Override
	public double getDisplayAreaVerticalWeight() {
		return 0;
	}

	@Override
	public void onControlVisibilityChange(Object object, boolean visible) {
	}

}