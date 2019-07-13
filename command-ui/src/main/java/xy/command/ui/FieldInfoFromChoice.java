package xy.command.ui;

import java.util.Collections;
import java.util.Map;

import xy.command.instance.ChoiceInstance;
import xy.command.instance.CommandLineInstance;
import xy.command.model.ArgumentPage;
import xy.command.model.Choice;
import xy.command.model.CommandLine;
import xy.reflect.ui.ReflectionUI;
import xy.reflect.ui.info.InfoCategory;
import xy.reflect.ui.info.ValueReturnMode;
import xy.reflect.ui.info.field.IFieldInfo;
import xy.reflect.ui.info.filter.IInfoFilter;
import xy.reflect.ui.info.type.ITypeInfo;
import xy.reflect.ui.info.type.source.SpecificitiesIdentifier;

public class FieldInfoFromChoice implements IFieldInfo {

	private Choice choice;
	private ArgumentPage argumentPage;
	private CommandLine commandLine;
	private ReflectionUI reflectionUI;
	private ITypeInfo commandLineTypeInfo;

	public FieldInfoFromChoice(ReflectionUI reflectionUI, Choice choice, ArgumentPage argumentPage,
			CommandLine commandLine, ITypeInfo commandLineTypeInfo) {
		this.reflectionUI = reflectionUI;
		this.choice = choice;
		this.argumentPage = argumentPage;
		this.commandLine = commandLine;
		this.commandLineTypeInfo = commandLineTypeInfo;
	}

	@Override
	public String getName() {
		int indexInArgumentPage = argumentPage.parts.indexOf(choice);
		return argumentPage.title + " - " + indexInArgumentPage;
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
	public ITypeInfo getType() {
		return reflectionUI.getTypeInfo(new TypeInfoSourceFromChoice(
				new SpecificitiesIdentifier(commandLineTypeInfo.getName(), getName()), choice, argumentPage));
	}

	@Override
	public Object getValue(Object object) {
		CommandLineInstance commandLineInstance = (CommandLineInstance) object;
		CommandLine commandLine = commandLineInstance.model;
		int argumentPageIndex = commandLine.arguments.indexOf(argumentPage);
		int indexInArgumentPage = argumentPage.parts.indexOf(choice);
		ChoiceInstance choiceInstance = (ChoiceInstance) commandLineInstance.argumentPageInstances
				.get(argumentPageIndex).partInstances.get(indexInArgumentPage);
		return choiceInstance.chosenPartInstance;
	}

	@Override
	public void setValue(Object object, Object value) {
		CommandLineInstance commandLineInstance = (CommandLineInstance) object;
		CommandLine commandLine = commandLineInstance.model;
		int argumentPageIndex = commandLine.arguments.indexOf(argumentPage);
		int indexInArgumentPage = argumentPage.parts.indexOf(choice);
		ChoiceInstance choiceInstance = (ChoiceInstance) commandLineInstance.argumentPageInstances
				.get(argumentPageIndex).partInstances.get(indexInArgumentPage);
		choiceInstance.chosenPartInstance = (CommandLineInstance) value;
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
