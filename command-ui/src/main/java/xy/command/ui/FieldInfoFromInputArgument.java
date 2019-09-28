package xy.command.ui;

import java.util.Collections;
import java.util.Map;

import xy.command.instance.ArgumentGroupInstance;
import xy.command.instance.CommandLineInstance;
import xy.command.instance.InputArgumentInstance;
import xy.command.model.AbstractCommandLinePart;
import xy.command.model.ArgumentGroup;
import xy.command.model.ArgumentPage;
import xy.command.model.CommandLine;
import xy.command.model.InputArgument;
import xy.reflect.ui.ReflectionUI;
import xy.reflect.ui.info.InfoCategory;
import xy.reflect.ui.info.ValueReturnMode;
import xy.reflect.ui.info.field.IFieldInfo;
import xy.reflect.ui.info.filter.IInfoFilter;
import xy.reflect.ui.info.type.ITypeInfo;
import xy.reflect.ui.info.type.source.JavaTypeInfoSource;
import xy.reflect.ui.info.type.source.SpecificitiesIdentifier;
import xy.reflect.ui.util.ReflectionUIError;

public class FieldInfoFromInputArgument implements IFieldInfo {

	private InputArgument inputArgument;
	private ArgumentPage argumentPage;
	private AbstractCommandLinePart containingPart;
	private ReflectionUI reflectionUI;
	private ITypeInfo containingTypeInfo;

	public FieldInfoFromInputArgument(ReflectionUI reflectionUI, InputArgument inputArgument, ArgumentPage argumentPage,
			AbstractCommandLinePart containingPart, ITypeInfo containingTypeInfo) {
		this.reflectionUI = reflectionUI;
		this.inputArgument = inputArgument;
		this.argumentPage = argumentPage;
		this.containingPart = containingPart;
		if (containingPart == null) {
			System.out.println("debug");
		}
		this.containingTypeInfo = containingTypeInfo;
	}

	@Override
	public String getName() {
		return inputArgument.getClass().getName() + inputArgument.hashCode();
	}

	@Override
	public String getCaption() {
		return inputArgument.title;
	}

	@Override
	public String getOnlineHelp() {
		return inputArgument.description;
	}

	@Override
	public Map<String, Object> getSpecificProperties() {
		return Collections.emptyMap();
	}

	@Override
	public ITypeInfo getType() {
		return reflectionUI.getTypeInfo(new JavaTypeInfoSource(String.class,
				new SpecificitiesIdentifier(containingTypeInfo.getName(), getName())));
	}

	@Override
	public Object getValue(Object object) {
		if (containingPart instanceof CommandLine) {
			CommandLineInstance commandLineInstance = (CommandLineInstance) object;
			int argumentPageIndex = ((CommandLine) containingPart).arguments.indexOf(argumentPage);
			int indexInArgumentPage = argumentPage.parts.indexOf(inputArgument);
			InputArgumentInstance inputArgumentInstance = (InputArgumentInstance) commandLineInstance.argumentPageInstances
					.get(argumentPageIndex).partInstances.get(indexInArgumentPage);
			return inputArgumentInstance.value;
		} else if (containingPart instanceof ArgumentGroup) {
			ArgumentGroupInstance argumentGroupInstance = (ArgumentGroupInstance) object;
			int indexInArgumentGroup = ((ArgumentGroup) containingPart).parts.indexOf(inputArgument);
			InputArgumentInstance inputArgumentInstance = (InputArgumentInstance) argumentGroupInstance.partInstances
					.get(indexInArgumentGroup);
			return inputArgumentInstance.value;
		} else {
			throw new ReflectionUIError();
		}
	}

	@Override
	public void setValue(Object object, Object value) {
		if (containingPart instanceof CommandLine) {
			CommandLineInstance commandLineInstance = (CommandLineInstance) object;
			int argumentPageIndex = ((CommandLine) containingPart).arguments.indexOf(argumentPage);
			int indexInArgumentPage = argumentPage.parts.indexOf(inputArgument);
			InputArgumentInstance inputArgumentInstance = (InputArgumentInstance) commandLineInstance.argumentPageInstances
					.get(argumentPageIndex).partInstances.get(indexInArgumentPage);
			inputArgumentInstance.value = (String) value;
		} else if (containingPart instanceof ArgumentGroup) {
			ArgumentGroupInstance argumentGroupInstance = (ArgumentGroupInstance) object;
			int indexInArgumentGroup = ((ArgumentGroup) containingPart).parts.indexOf(inputArgument);
			InputArgumentInstance inputArgumentInstance = (InputArgumentInstance) argumentGroupInstance.partInstances
					.get(indexInArgumentGroup);
			inputArgumentInstance.value = (String) value;
		} else {
			throw new ReflectionUIError();
		}
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
