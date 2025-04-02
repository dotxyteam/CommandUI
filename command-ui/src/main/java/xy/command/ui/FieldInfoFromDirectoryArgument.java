package xy.command.ui;

import java.io.File;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import xy.command.instance.ArgumentGroupInstance;
import xy.command.instance.CommandLineInstance;
import xy.command.instance.DirectoryArgumentInstance;
import xy.command.model.AbstractCommandLinePart;
import xy.command.model.ArgumentGroup;
import xy.command.model.ArgumentPage;
import xy.command.model.CommandLine;
import xy.command.model.DirectoryArgument;
import xy.reflect.ui.ReflectionUI;
import xy.reflect.ui.control.swing.plugin.FileBrowserPlugin;
import xy.reflect.ui.control.swing.plugin.FileBrowserPlugin.SelectionModeConfiguration;
import xy.reflect.ui.info.InfoCategory;
import xy.reflect.ui.info.ValueReturnMode;
import xy.reflect.ui.info.field.IFieldInfo;
import xy.reflect.ui.info.filter.IInfoFilter;
import xy.reflect.ui.info.method.IMethodInfo;
import xy.reflect.ui.info.type.DefaultTypeInfo;
import xy.reflect.ui.info.type.ITypeInfo;
import xy.reflect.ui.info.type.source.JavaTypeInfoSource;
import xy.reflect.ui.info.type.source.SpecificitiesIdentifier;
import xy.reflect.ui.util.ReflectionUIError;
import xy.reflect.ui.util.ReflectionUIUtils;

/**
 * Specifies how a {@link DirectoryArgument} should be displayed in a GUI.
 * 
 * @author olitank
 *
 */
public class FieldInfoFromDirectoryArgument implements IFieldInfo {

	private DirectoryArgument directoryArgument;
	private ArgumentPage argumentPage;
	private AbstractCommandLinePart containingPart;
	private ReflectionUI reflectionUI;
	private ITypeInfo commandLineTypeInfo;

	public FieldInfoFromDirectoryArgument(ReflectionUI reflectionUI, DirectoryArgument directoryArgument,
			ArgumentPage argumentPage, AbstractCommandLinePart containingPart, ITypeInfo commandLineTypeInfo) {
		this.reflectionUI = reflectionUI;
		this.directoryArgument = directoryArgument;
		this.argumentPage = argumentPage;
		this.containingPart = containingPart;
		this.commandLineTypeInfo = commandLineTypeInfo;
	}

	@Override
	public String getName() {
		return directoryArgument.getClass().getName() + directoryArgument.hashCode();
	}

	@Override
	public String getCaption() {
		return directoryArgument.title;
	}

	@Override
	public String getOnlineHelp() {
		return directoryArgument.description;
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
		return new DefaultTypeInfo(reflectionUI, new JavaTypeInfoSource(File.class,
				new SpecificitiesIdentifier(commandLineTypeInfo.getName(), getName()))) {

			@Override
			public Map<String, Object> getSpecificProperties() {
				Map<String, Object> result = new HashMap<String, Object>(super.getSpecificProperties());
				FileBrowserPlugin plugin = new FileBrowserPlugin();
				FileBrowserPlugin.FileBrowserConfiguration c = new FileBrowserPlugin.FileBrowserConfiguration();
				c.selectionMode = SelectionModeConfiguration.DIRECTORIES_ONLY;
				ReflectionUIUtils.setFieldControlPluginIdentifier(result, plugin.getIdentifier());
				ReflectionUIUtils.setFieldControlPluginConfiguration(result, plugin.getIdentifier(), c);
				return result;
			}

		};
	}

	@Override
	public Object getValue(Object object) {
		if (containingPart instanceof CommandLine) {
			CommandLineInstance commandLineInstance = (CommandLineInstance) object;
			int argumentPageIndex = ((CommandLine) containingPart).arguments.indexOf(argumentPage);
			int indexInArgumentPage = argumentPage.parts.indexOf(directoryArgument);
			DirectoryArgumentInstance directoryArgumentInstance = (DirectoryArgumentInstance) commandLineInstance.argumentPageInstances
					.get(argumentPageIndex).partInstances.get(indexInArgumentPage);
			if (directoryArgumentInstance.value == null) {
				return null;
			} else {
				return new File(directoryArgumentInstance.value);
			}
		} else if (containingPart instanceof ArgumentGroup) {
			ArgumentGroupInstance argumentGroupInstance = (ArgumentGroupInstance) object;
			int indexInArgumentGroup = ((ArgumentGroup) containingPart).parts.indexOf(directoryArgument);
			DirectoryArgumentInstance directoryArgumentInstance = (DirectoryArgumentInstance) argumentGroupInstance.partInstances
					.get(indexInArgumentGroup);
			if (directoryArgumentInstance.value == null) {
				return null;
			} else {
				return new File(directoryArgumentInstance.value);
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
			int indexInArgumentPage = argumentPage.parts.indexOf(directoryArgument);
			DirectoryArgumentInstance directoryArgumentInstance = (DirectoryArgumentInstance) commandLineInstance.argumentPageInstances
					.get(argumentPageIndex).partInstances.get(indexInArgumentPage);
			if (value == null) {
				directoryArgumentInstance.value = null;
			} else {
				directoryArgumentInstance.value = ((File) value).getPath();
			}
		} else if (containingPart instanceof ArgumentGroup) {
			ArgumentGroupInstance argumentGroupInstance = (ArgumentGroupInstance) object;
			int indexInArgumentGroup = ((ArgumentGroup) containingPart).parts.indexOf(directoryArgument);
			DirectoryArgumentInstance directoryArgumentInstance = (DirectoryArgumentInstance) argumentGroupInstance.partInstances
					.get(indexInArgumentGroup);
			if (value == null) {
				directoryArgumentInstance.value = null;
			} else {
				directoryArgumentInstance.value = ((File) value).getPath();
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
