package xy.command.ui;

import java.util.Collections;
import java.util.Map;

import xy.command.instance.ArgumentGroupInstance;
import xy.command.instance.CommandLineInstance;
import xy.command.instance.OptionalPartInstance;
import xy.command.model.AbstractCommandLinePart;
import xy.command.model.ArgumentGroup;
import xy.command.model.ArgumentPage;
import xy.command.model.CommandLine;
import xy.command.model.FixedArgument;
import xy.command.model.OptionalPart;
import xy.reflect.ui.ReflectionUI;
import xy.reflect.ui.info.InfoCategory;
import xy.reflect.ui.info.ValueReturnMode;
import xy.reflect.ui.info.field.IFieldInfo;
import xy.reflect.ui.info.filter.IInfoFilter;
import xy.reflect.ui.info.type.ITypeInfo;
import xy.reflect.ui.info.type.source.JavaTypeInfoSource;
import xy.reflect.ui.info.type.source.SpecificitiesIdentifier;
import xy.reflect.ui.util.ReflectionUIError;

public class FieldInfoFromOptionalPart implements IFieldInfo {

	private OptionalPart optionalPart;
	private ArgumentPage argumentPage;
	private AbstractCommandLinePart containingPart;
	private ReflectionUI reflectionUI;
	private ITypeInfo commandLineTypeInfo;

	public FieldInfoFromOptionalPart(ReflectionUI reflectionUI, OptionalPart optionalPart, ArgumentPage argumentPage,
			AbstractCommandLinePart containingPart, ITypeInfo commandLineTypeInfo) {
		this.reflectionUI = reflectionUI;
		this.optionalPart = optionalPart;
		this.argumentPage = argumentPage;
		this.containingPart = containingPart;
		this.commandLineTypeInfo = commandLineTypeInfo;
	}

	@Override
	public String getName() {
		return optionalPart.getClass().getName() + optionalPart.hashCode();
	}

	@Override
	public String getCaption() {
		return optionalPart.title;
	}

	@Override
	public String getOnlineHelp() {
		return optionalPart.description;
	}

	@Override
	public Map<String, Object> getSpecificProperties() {
		return Collections.emptyMap();
	}

	private boolean isBoolean() {
		for (AbstractCommandLinePart childPart : optionalPart.parts) {
			if (!(childPart instanceof FixedArgument)) {
				return false;
			}
		}
		return true;
	}

	@Override
	public ITypeInfo getType() {
		if (isBoolean()) {
			return reflectionUI.getTypeInfo(new JavaTypeInfoSource(Boolean.class,
					new SpecificitiesIdentifier(commandLineTypeInfo.getName(), getName())));
		} else {
			return reflectionUI.getTypeInfo(new TypeInfoSourceFromArgumentGroup(optionalPart,
					new SpecificitiesIdentifier(commandLineTypeInfo.getName(), getName())));
		}
	}

	@Override
	public boolean isNullValueDistinct() {
		if (isBoolean()) {
			return false;
		} else {
			return true;
		}
	}

	@Override
	public Object getValue(Object object) {
		if (containingPart instanceof CommandLine) {
			CommandLineInstance commandLineInstance = (CommandLineInstance) object;
			int argumentPageIndex = ((CommandLine) containingPart).arguments.indexOf(argumentPage);
			int indexInArgumentPage = argumentPage.parts.indexOf(optionalPart);
			OptionalPartInstance optionalPartInstance = (OptionalPartInstance) commandLineInstance.argumentPageInstances
					.get(argumentPageIndex).partInstances.get(indexInArgumentPage);
			if (isBoolean()) {
				return (optionalPartInstance.argumentGroupInstance != null);
			} else {
				return optionalPartInstance.argumentGroupInstance;
			}
		} else if (containingPart instanceof ArgumentGroup) {
			ArgumentGroupInstance argumentGroupInstance = (ArgumentGroupInstance) object;
			int indexInArgumentGroup = ((ArgumentGroup) containingPart).parts.indexOf(optionalPart);
			OptionalPartInstance optionalPartInstance = (OptionalPartInstance) argumentGroupInstance.partInstances
					.get(indexInArgumentGroup);
			if (isBoolean()) {
				return (optionalPartInstance.argumentGroupInstance != null);
			} else {
				return optionalPartInstance.argumentGroupInstance;
			}
		} else {
			throw new ReflectionUIError();
		}
	}

	@Override
	public void setValue(Object object, Object value) {
		if (containingPart instanceof CommandLine) {
			CommandLineInstance commandLineInstance = (CommandLineInstance) object;
			int argumentPageIndex = ((CommandLine) containingPart).arguments.indexOf(argumentPage);
			int indexInArgumentPage = argumentPage.parts.indexOf(optionalPart);
			OptionalPartInstance optionalPartInstance = (OptionalPartInstance) commandLineInstance.argumentPageInstances
					.get(argumentPageIndex).partInstances.get(indexInArgumentPage);
			if (isBoolean()) {
				optionalPartInstance.argumentGroupInstance = ((Boolean) value) ? new ArgumentGroupInstance(optionalPart)
						: null;
			} else {
				optionalPartInstance.argumentGroupInstance = (ArgumentGroupInstance) value;
			}
		} else if (containingPart instanceof ArgumentGroup) {
			ArgumentGroupInstance argumentGroupInstance = (ArgumentGroupInstance) object;
			int indexInArgumentGroup = ((ArgumentGroup) containingPart).parts.indexOf(optionalPart);
			OptionalPartInstance optionalPartInstance = (OptionalPartInstance) argumentGroupInstance.partInstances
					.get(indexInArgumentGroup);
			if (isBoolean()) {
				optionalPartInstance.argumentGroupInstance = ((Boolean) value) ? new ArgumentGroupInstance(optionalPart)
						: null;
			} else {
				optionalPartInstance.argumentGroupInstance = (ArgumentGroupInstance) value;
			}
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
		return true;
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
