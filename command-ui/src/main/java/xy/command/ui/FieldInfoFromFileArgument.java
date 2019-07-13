package xy.command.ui;

import java.io.File;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import xy.command.instance.CommandLineInstance;
import xy.command.instance.FileArgumentInstance;
import xy.command.model.ArgumentPage;
import xy.command.model.CommandLine;
import xy.command.model.FileArgument;
import xy.reflect.ui.ReflectionUI;
import xy.reflect.ui.control.swing.plugin.FileBrowserPlugin;
import xy.reflect.ui.info.InfoCategory;
import xy.reflect.ui.info.ValueReturnMode;
import xy.reflect.ui.info.field.IFieldInfo;
import xy.reflect.ui.info.filter.IInfoFilter;
import xy.reflect.ui.info.type.DefaultTypeInfo;
import xy.reflect.ui.info.type.ITypeInfo;
import xy.reflect.ui.info.type.source.JavaTypeInfoSource;
import xy.reflect.ui.info.type.source.SpecificitiesIdentifier;
import xy.reflect.ui.util.ReflectionUIUtils;

public class FieldInfoFromFileArgument implements IFieldInfo {

	private FileArgument fileArgument;
	private ArgumentPage argumentPage;
	private CommandLine commandLine;
	private ReflectionUI reflectionUI;
	private ITypeInfo commandLineTypeInfo;

	public FieldInfoFromFileArgument(ReflectionUI reflectionUI, FileArgument fileArgument, ArgumentPage argumentPage,
			CommandLine commandLine, ITypeInfo commandLineTypeInfo) {
		this.reflectionUI = reflectionUI;
		this.fileArgument = fileArgument;
		this.argumentPage = argumentPage;
		this.commandLine = commandLine;
		this.commandLineTypeInfo = commandLineTypeInfo;
	}

	@Override
	public String getName() {
		int indexInArgumentPage = argumentPage.parts.indexOf(fileArgument);
		return argumentPage.title + " - " + indexInArgumentPage;
	}

	@Override
	public String getCaption() {
		return fileArgument.title;
	}

	@Override
	public String getOnlineHelp() {
		return fileArgument.description;
	}

	@Override
	public Map<String, Object> getSpecificProperties() {
		return Collections.emptyMap();
	}

	@Override
	public ITypeInfo getType() {
		return new DefaultTypeInfo(reflectionUI, new JavaTypeInfoSource(File.class,
				new SpecificitiesIdentifier(commandLineTypeInfo.getName(), getName()))) {

			@Override
			public Map<String, Object> getSpecificProperties() {
				Map<String, Object> result = new HashMap<String, Object>(super.getSpecificProperties());
				ReflectionUIUtils.setFieldControlPluginIdentifier(result, new FileBrowserPlugin().getIdentifier());
				return result;
			}

		};
	}

	@Override
	public Object getValue(Object object) {
		CommandLineInstance commandLineInstance = (CommandLineInstance) object;
		CommandLine commandLine = commandLineInstance.model;
		int argumentPageIndex = commandLine.arguments.indexOf(argumentPage);
		int indexInArgumentPage = argumentPage.parts.indexOf(fileArgument);
		FileArgumentInstance fileArgumentInstance = (FileArgumentInstance) commandLineInstance.argumentPageInstances
				.get(argumentPageIndex).partInstances.get(indexInArgumentPage);
		if (fileArgumentInstance.value == null) {
			return null;
		} else {
			return new File(fileArgumentInstance.value);
		}
	}

	@Override
	public void setValue(Object object, Object value) {
		CommandLineInstance commandLineInstance = (CommandLineInstance) object;
		CommandLine commandLine = commandLineInstance.model;
		int argumentPageIndex = commandLine.arguments.indexOf(argumentPage);
		int indexInArgumentPage = argumentPage.parts.indexOf(fileArgument);
		FileArgumentInstance fileArgumentInstance = (FileArgumentInstance) commandLineInstance.argumentPageInstances
				.get(argumentPageIndex).partInstances.get(indexInArgumentPage);
		if (value == null) {
			fileArgumentInstance.value = null;
		} else {
			fileArgumentInstance.value = ((File) value).getPath();
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
