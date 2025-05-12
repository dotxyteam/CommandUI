package xy.command.ui;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import xy.command.instance.ArgumentGroupInstance;
import xy.command.instance.ChoiceInstance;
import xy.command.instance.CommandLineInstance;
import xy.command.model.AbstractCommandLinePart;
import xy.command.model.ArgumentGroup;
import xy.command.model.ArgumentPage;
import xy.command.model.Choice;
import xy.command.model.CommandLine;
import xy.reflect.ui.info.InfoCategory;
import xy.reflect.ui.info.ValueReturnMode;
import xy.reflect.ui.info.field.IFieldInfo;
import xy.reflect.ui.info.filter.IInfoFilter;
import xy.reflect.ui.info.method.IMethodInfo;
import xy.reflect.ui.info.type.ITypeInfo;
import xy.reflect.ui.info.type.source.SpecificitiesIdentifier;
import xy.reflect.ui.util.ReflectionUIError;

/**
 * Specifies how a {@link Choice} should be displayed in a GUI.
 * 
 * @author olitank
 *
 */
public class FieldInfoFromChoice implements IFieldInfo {

	private Choice choice;
	private ArgumentPage argumentPage;
	private AbstractCommandLinePart containingPart;
	private CommandLineUI commandLineUI;
	private ITypeInfo commandLineTypeInfo;

	public FieldInfoFromChoice(CommandLineUI commandLineUI, Choice choice, ArgumentPage argumentPage,
			AbstractCommandLinePart containingPart, ITypeInfo commandLineTypeInfo) {
		this.commandLineUI = commandLineUI;
		this.choice = choice;
		this.argumentPage = argumentPage;
		this.containingPart = containingPart;
		this.commandLineTypeInfo = commandLineTypeInfo;
	}

	@Override
	public String getName() {
		return choice.getClass().getName() + choice.hashCode();
	}

	@Override
	public String getCaption() {
		return choice.title;
	}

	@Override
	public String getOnlineHelp() {
		return choice.description;
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
		return commandLineUI.getTypeInfo(new TypeInfoSourceFromChoice(commandLineUI,
				new SpecificitiesIdentifier(commandLineTypeInfo.getName(), getName()), choice));
	}

	@Override
	public Object getValue(Object object) {
		if (containingPart instanceof CommandLine) {
			CommandLineInstance commandLineInstance = (CommandLineInstance) object;
			int argumentPageIndex = ((CommandLine) containingPart).arguments.indexOf(argumentPage);
			int indexInArgumentPage = argumentPage.parts.indexOf(choice);
			ChoiceInstance choiceInstance = (ChoiceInstance) commandLineInstance.argumentPageInstances
					.get(argumentPageIndex).partInstances.get(indexInArgumentPage);
			return choiceInstance.chosenPartInstance;
		} else if (containingPart instanceof ArgumentGroup) {
			ArgumentGroupInstance argumentGroupInstance = (ArgumentGroupInstance) object;
			int indexInArgumentGroup = ((ArgumentGroup) containingPart).parts.indexOf(choice);
			ChoiceInstance choiceInstance = (ChoiceInstance) argumentGroupInstance.partInstances
					.get(indexInArgumentGroup);
			return choiceInstance.chosenPartInstance;
		} else {
			throw new ReflectionUIError();
		}
	}

	@Override
	public void setValue(Object object, Object value) {
		if (containingPart instanceof CommandLine) {
			CommandLineInstance commandLineInstance = (CommandLineInstance) object;
			int argumentPageIndex = ((CommandLine) containingPart).arguments.indexOf(argumentPage);
			int indexInArgumentPage = argumentPage.parts.indexOf(choice);
			ChoiceInstance choiceInstance = (ChoiceInstance) commandLineInstance.argumentPageInstances
					.get(argumentPageIndex).partInstances.get(indexInArgumentPage);
			choiceInstance.chosenPartInstance = (ArgumentGroupInstance) value;
		} else if (containingPart instanceof ArgumentGroup) {
			ArgumentGroupInstance argumentGroupInstance = (ArgumentGroupInstance) object;
			int indexInArgumentGroup = ((ArgumentGroup) containingPart).parts.indexOf(choice);
			ChoiceInstance choiceInstance = (ChoiceInstance) argumentGroupInstance.partInstances
					.get(indexInArgumentGroup);
			choiceInstance.chosenPartInstance = (ArgumentGroupInstance) value;
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
	public boolean isValueValidityDetectionEnabled() {
		return true;
	}

	@Override
	public boolean isHidden() {
		return false;
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

}
