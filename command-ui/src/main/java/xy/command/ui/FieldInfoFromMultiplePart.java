package xy.command.ui;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import xy.command.instance.ArgumentGroupInstance;
import xy.command.instance.CommandLineInstance;
import xy.command.instance.MultiplePartInstance;
import xy.command.model.AbstractCommandLinePart;
import xy.command.model.ArgumentGroup;
import xy.command.model.ArgumentPage;
import xy.command.model.CommandLine;
import xy.command.model.MultiplePart;
import xy.reflect.ui.ReflectionUI;
import xy.reflect.ui.info.ColorSpecification;
import xy.reflect.ui.info.ITransaction;
import xy.reflect.ui.info.InfoCategory;
import xy.reflect.ui.info.ResourcePath;
import xy.reflect.ui.info.ValidationSession;
import xy.reflect.ui.info.ValueReturnMode;
import xy.reflect.ui.info.field.IFieldInfo;
import xy.reflect.ui.info.filter.IInfoFilter;
import xy.reflect.ui.info.menu.MenuModel;
import xy.reflect.ui.info.method.IMethodInfo;
import xy.reflect.ui.info.type.ITypeInfo;
import xy.reflect.ui.info.type.ITypeInfo.IValidationJob;
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
import xy.reflect.ui.util.MiscUtils;
import xy.reflect.ui.util.ReflectionUIError;
import xy.reflect.ui.util.ReflectionUIUtils;

/**
 * Specifies how a {@link MultiplePart} should be displayed in a GUI.
 * 
 * @author olitank
 *
 */
public class FieldInfoFromMultiplePart implements IFieldInfo {

	private MultiplePart multiplePart;
	private ArgumentPage argumentPage;
	private AbstractCommandLinePart containingPart;
	private CommandLineUI commandLineUI;
	private ITypeInfo commandLineTypeInfo;

	public FieldInfoFromMultiplePart(CommandLineUI commandLineUI, MultiplePart multiplePart, ArgumentPage argumentPage,
			AbstractCommandLinePart containingPart, ITypeInfo commandLineTypeInfo) {
		this.commandLineUI = commandLineUI;
		this.multiplePart = multiplePart;
		this.argumentPage = argumentPage;
		this.containingPart = containingPart;
		this.commandLineTypeInfo = commandLineTypeInfo;
	}

	@Override
	public String getName() {
		return multiplePart.getClass().getName() + multiplePart.hashCode();
	}

	@Override
	public String getCaption() {
		return multiplePart.title;
	}

	@Override
	public String getOnlineHelp() {
		return multiplePart.description;
	}

	@Override
	public Map<String, Object> getSpecificProperties() {
		return Collections.emptyMap();
	}

	@Override
	public List<IMethodInfo> getAlternativeConstructors(Object object) {
		return null;
	}

	@Override
	public List<IMethodInfo> getAlternativeListItemConstructors(Object object) {
		return null;
	}

	@Override
	public ITypeInfo getType() {
		return new IListTypeInfo() {

			@Override
			public IValidationJob getListItemAbstractFormValidationJob(ItemPosition itemPosition) {
				return null;
			}

			@Override
			public boolean isItemNodeValidityDetectionEnabled(ItemPosition itemPosition) {
				return true;
			}

			@Override
			public boolean isValidationRequired() {
				return false;
			}

			@Override
			public IFieldInfo getSelectionTargetField(ITypeInfo objectType) {
				return null;
			}

			@Override
			public Map<String, Object> getSpecificProperties() {
				return Collections.emptyMap();
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
			public String getOnlineHelp() {
				return null;
			}

			@Override
			public String getName() {
				return FieldInfoFromMultiplePart.this.getName() + "-type";
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
				List<String> result = new ArrayList<String>();
				for (Object item : toArray(object)) {
					result.add(ReflectionUIUtils.toString(commandLineUI, item));
				}
				return MiscUtils.stringJoin(result, ", ");
			}

			@Override
			public boolean supports(Object object) {
				return object instanceof CommandLineInstance[];
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

			@Override
			public void replaceContent(Object listValue, Object[] array) {
			}

			@Override
			public boolean isRemovalAllowed() {
				return true;
			}

			@Override
			public boolean isMoveAllowed() {
				return true;
			}

			@Override
			public boolean areItemsAutomaticallyPositioned() {
				return false;
			}

			@Override
			public boolean isItemNullValueSupported() {
				return false;
			}

			@Override
			public ItemCreationMode getItemCreationMode() {
				return ItemCreationMode.UNDEFINED;
			}

			@Override
			public ToolsLocation getToolsLocation() {
				return ToolsLocation.EAST;
			}

			@Override
			public boolean isInsertionAllowed() {
				return true;
			}

			@Override
			public IListStructuralInfo getStructuralInfo() {
				return new DefaultListStructuralInfo(commandLineUI);
			}

			@Override
			public ITypeInfo getItemType() {
				return commandLineUI.getTypeInfo(new TypeInfoSourceFromArgumentGroup(multiplePart, null) {

					@Override
					public ITypeInfo buildTypeInfo(ReflectionUI reflectionUI) {
						return new TypeInfoFromArgumentGroup(commandLineUI) {

							@Override
							public String getName() {
								return super.getName() + "-item";
							}

							@Override
							public String getCaption() {
								return ReflectionUIUtils.composeMessage(super.getCaption(), "Item");
							}

						};
					}

				});
			}

			@Override
			public ValueReturnMode getItemReturnMode() {
				return ValueReturnMode.DIRECT;
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
				ArgumentGroupInstance[] result = new ArgumentGroupInstance[array.length];
				for (int i = 0; i < array.length; i++) {
					result[i] = (ArgumentGroupInstance) array[i];
				}
				return result;
			}

			@Override
			public Object[] toArray(Object listValue) {
				ArgumentGroupInstance[] array = (ArgumentGroupInstance[]) listValue;
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
			public boolean canInstantiateFromArray() {
				return true;
			}
		};

	}

	@Override
	public Object getValue(Object object) {
		if (containingPart instanceof CommandLine) {
			CommandLineInstance commandLineInstance = (CommandLineInstance) object;
			int argumentPageIndex = ((CommandLine) containingPart).arguments.indexOf(argumentPage);
			int indexInArgumentPage = argumentPage.parts.indexOf(multiplePart);
			MultiplePartInstance multiplePartInstance = (MultiplePartInstance) commandLineInstance.argumentPageInstances
					.get(argumentPageIndex).partInstances.get(indexInArgumentPage);
			return multiplePartInstance.argumentGroupInstances;
		} else if (containingPart instanceof ArgumentGroup) {
			ArgumentGroupInstance argumentGroupInstance = (ArgumentGroupInstance) object;
			int indexInArgumentGroup = ((ArgumentGroup) containingPart).parts.indexOf(multiplePart);
			MultiplePartInstance multiplePartInstance = (MultiplePartInstance) argumentGroupInstance.partInstances
					.get(indexInArgumentGroup);
			return multiplePartInstance.argumentGroupInstances;
		} else {
			throw new ReflectionUIError();
		}
	}

	@Override
	public void setValue(Object object, Object value) {
		if (containingPart instanceof CommandLine) {
			CommandLineInstance commandLineInstance = (CommandLineInstance) object;
			int argumentPageIndex = ((CommandLine) containingPart).arguments.indexOf(argumentPage);
			int indexInArgumentPage = argumentPage.parts.indexOf(multiplePart);
			MultiplePartInstance multiplePartInstance = (MultiplePartInstance) commandLineInstance.argumentPageInstances
					.get(argumentPageIndex).partInstances.get(indexInArgumentPage);
			multiplePartInstance.argumentGroupInstances = (ArgumentGroupInstance[]) value;
		} else if (containingPart instanceof ArgumentGroup) {
			ArgumentGroupInstance argumentGroupInstance = (ArgumentGroupInstance) object;
			int indexInArgumentGroup = ((ArgumentGroup) containingPart).parts.indexOf(multiplePart);
			MultiplePartInstance multiplePartInstance = (MultiplePartInstance) argumentGroupInstance.partInstances
					.get(indexInArgumentGroup);
			multiplePartInstance.argumentGroupInstances = (ArgumentGroupInstance[]) value;
		} else {
			throw new ReflectionUIError();
		}
	}

	@Override
	public boolean hasValueOptions(Object object) {
		return false;
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
	public Runnable getPreviousUpdateCustomRedoJob(Object object, Object newValue) {
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
	public boolean isTransient() {
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
		if (!(containingPart instanceof CommandLine)) {
			return null;
		}
		int argumentPageIndex = ((CommandLine) containingPart).arguments.indexOf(argumentPage);
		return new InfoCategory(argumentPage.title, argumentPageIndex, null);
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
	public boolean isRelevant(Object object) {
		return true;
	}

	@Override
	public double getDisplayAreaHorizontalWeight() {
		return 1;
	}

	@Override
	public double getDisplayAreaVerticalWeight() {
		return 0;
	}

	@Override
	public boolean isDisplayAreaHorizontallyFilled() {
		return true;
	}

	@Override
	public boolean isDisplayAreaVerticallyFilled() {
		return false;
	}

	@Override
	public void onControlVisibilityChange(Object object, boolean visible) {
	}

	@Override
	public boolean isControlValueValiditionEnabled() {
		return false;
	}

	@Override
	public IValidationJob getValueAbstractFormValidationJob(Object object) {
		return null;
	}
}
