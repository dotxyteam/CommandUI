package xy.command.ui;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.swing.SwingUtilities;

import xy.command.instance.CommandLineInstance;
import xy.command.model.AbstractCommandLinePart;
import xy.command.model.ArgumentPage;
import xy.command.model.Choice;
import xy.command.model.CommandLine;
import xy.command.model.CommandLineProject;
import xy.command.model.DirectoryArgument;
import xy.command.model.FileArgument;
import xy.command.model.FixedArgument;
import xy.command.model.InputArgument;
import xy.command.model.MultiplePart;
import xy.command.model.OptionalPart;
import xy.command.ui.util.CommandUIUtils;
import xy.reflect.ui.ReflectionUI;
import xy.reflect.ui.info.field.FieldInfoProxy;
import xy.reflect.ui.info.field.IFieldInfo;
import xy.reflect.ui.info.method.IMethodInfo;
import xy.reflect.ui.info.method.InvocationData;
import xy.reflect.ui.info.method.MethodInfoProxy;
import xy.reflect.ui.info.type.DefaultTypeInfo;
import xy.reflect.ui.info.type.ITypeInfo;
import xy.reflect.ui.info.type.source.ITypeInfoSource;
import xy.reflect.ui.info.type.source.JavaTypeInfoSource;
import xy.reflect.ui.info.type.source.SpecificitiesIdentifier;
import xy.reflect.ui.util.PrecomputedTypeInstanceWrapper;
import xy.reflect.ui.util.ReflectionUIError;
import xy.reflect.ui.util.ReflectionUIUtils;

/**
 * Specifies how a {@link CommandLine} should be displayed in a GUI.
 * 
 * @author olitank
 *
 */
public class TypeInfoSourceFromCommandLine implements ITypeInfoSource {

	private CommandLineUI commandLineUI;
	private CommandLine commandLine;
	private SpecificitiesIdentifier specificitiesIdentifier;

	public TypeInfoSourceFromCommandLine(CommandLineUI commandLineUI, CommandLine commandLine,
			SpecificitiesIdentifier specificitiesIdentifier) {
		this.commandLineUI = commandLineUI;
		this.commandLine = commandLine;
		this.specificitiesIdentifier = specificitiesIdentifier;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((commandLine == null) ? 0 : commandLine.hashCode());
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
		TypeInfoSourceFromCommandLine other = (TypeInfoSourceFromCommandLine) obj;
		if (commandLine == null) {
			if (other.commandLine != null)
				return false;
		} else if (!commandLine.equals(other.commandLine))
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
	public ITypeInfo getTypeInfo() {
		return new TypeInfo(commandLineUI);
	}

	protected IFieldInfo getFieldInfoFromCommandLinePart(CommandLineUI commandLineUI, AbstractCommandLinePart part,
			ArgumentPage argumentPage, ITypeInfo commandLineTypeInfo) {
		if (part instanceof FixedArgument) {
			return null;
		} else if (part instanceof InputArgument) {
			return new FieldInfoFromInputArgument(commandLineUI, (InputArgument) part, argumentPage, commandLine,
					commandLineTypeInfo);
		} else if (part instanceof DirectoryArgument) {
			return new FieldInfoFromDirectoryArgument(commandLineUI, (DirectoryArgument) part, argumentPage,
					commandLine, commandLineTypeInfo);
		} else if (part instanceof FileArgument) {
			return new FieldInfoFromFileArgument(commandLineUI, (FileArgument) part, argumentPage, commandLine,
					commandLineTypeInfo);
		} else if (part instanceof Choice) {
			return new FieldInfoFromChoice(commandLineUI, (Choice) part, argumentPage, commandLine,
					commandLineTypeInfo);
		} else if (part instanceof OptionalPart) {
			return new FieldInfoFromOptionalPart(commandLineUI, (OptionalPart) part, argumentPage, commandLine,
					commandLineTypeInfo);
		} else if (part instanceof MultiplePart) {
			return new FieldInfoFromMultiplePart(commandLineUI, (MultiplePart) part, argumentPage, commandLine,
					commandLineTypeInfo);
		} else {
			throw new ReflectionUIError();
		}
	}

	protected class TypeInfo extends DefaultTypeInfo {

		public TypeInfo(ReflectionUI reflectionUI) {
			super(reflectionUI, new JavaTypeInfoSource(reflectionUI, CommandLineInstance.class, null));
		}

		@Override
		public String getCaption() {
			return commandLine.title;
		}

		@Override
		public String getOnlineHelp() {
			return commandLine.description;
		}

		@Override
		public List<IMethodInfo> getMethods() {
			return Collections.singletonList(new ExecutionMethodInfo(reflectionUI));
		}

		@Override
		public List<IFieldInfo> getFields() {
			return Collections.singletonList(new CapsuleFieldInfo(reflectionUI));
		}

	}

	protected class Capsule {

		protected CommandLineInstance commandLineInstance;

		public Capsule(CommandLineInstance commandLineInstance) {
			super();
			this.commandLineInstance = commandLineInstance;
		}

		public CommandLineInstance getCommandLineInstance() {
			return commandLineInstance;
		}

	}

	protected class CapsuleFieldInfo extends FieldInfoProxy {

		protected ReflectionUI reflectionUI;

		public CapsuleFieldInfo(ReflectionUI reflectionUI) {
			super(IFieldInfo.NULL_FIELD_INFO);
			this.reflectionUI = reflectionUI;
		}

		@Override
		public String getName() {
			return "capsule";
		}

		@Override
		public String getCaption() {
			return "";
		}

		@Override
		public void setValue(Object object, Object value) {
			throw new UnsupportedOperationException();
		}

		@Override
		public boolean isFormControlMandatory() {
			return true;
		}

		@Override
		public boolean isFormControlEmbedded() {
			return true;
		}

		@Override
		public Object getValue(Object object) {
			return new PrecomputedTypeInstanceWrapper(new Capsule((CommandLineInstance) object),
					new CapsuleTypeInfo(reflectionUI));
		}

		@Override
		public ITypeInfo getType() {
			return reflectionUI
					.getTypeInfo(new PrecomputedTypeInstanceWrapper.TypeInfoSource(new CapsuleTypeInfo(reflectionUI)));
		}

	}

	protected class CapsuleTypeInfo extends DefaultTypeInfo {

		public CapsuleTypeInfo(ReflectionUI reflectionUI) {
			super(reflectionUI, new JavaTypeInfoSource(reflectionUI, Capsule.class, new SpecificitiesIdentifier(
					new TypeInfo(reflectionUI).getName(), new CapsuleFieldInfo(reflectionUI).getName())));
		}

		@Override
		public String getCaption() {
			return commandLine.title + " - Settings";
		}

		@Override
		public List<IMethodInfo> getMethods() {
			return Collections.emptyList();
		}

		@Override
		public List<IFieldInfo> getFields() {
			List<IFieldInfo> result = new ArrayList<IFieldInfo>();
			for (ArgumentPage argumentPage : commandLine.arguments) {
				for (AbstractCommandLinePart part : argumentPage.parts) {
					IFieldInfo field = getFieldInfoFromCommandLinePart(commandLineUI, part, argumentPage, this);
					if (field == null) {
						continue;
					}
					field = new FieldInfoProxy(field) {

						@Override
						public Object getValue(Object object) {
							return super.getValue(((Capsule) object).getCommandLineInstance());
						}

						@Override
						public void setValue(Object object, Object value) {
							super.setValue(((Capsule) object).getCommandLineInstance(), value);
						}

					};
					result.add(field);
				}
			}
			return result;
		}

	}

	protected class ExecutionMethodInfo extends MethodInfoProxy {

		protected ReflectionUI reflectionUI;

		public ExecutionMethodInfo(ReflectionUI reflectionUI) {
			super(IMethodInfo.NULL_METHOD_INFO);
			this.reflectionUI = reflectionUI;
		}

		@Override
		public String getName() {
			return "execute";
		}

		@Override
		public String getSignature() {
			return ReflectionUIUtils.buildMethodSignature(this);
		}

		@Override
		public String getCaption() {
			return "Execute";
		}

		@Override
		public boolean isReadOnly() {
			return true;
		}

		@Override
		public Object invoke(Object object, InvocationData invocationData) {
			final CommandLineProject project = (CommandLineProject) commandLine;
			final CommandLineInstance instance = (CommandLineInstance) object;
			final String commandText = CommandUIUtils.quoteArgument(project.executablePath.getPath()) + " "
					+ instance.getExecutionText();
			SwingUtilities.invokeLater(new Runnable() {
				@Override
				public void run() {
					CommandMonitoringDialog d = new CommandMonitoringDialog(null, commandText, project.executionDir);
					d.setVisible(true);
				}
			});
			return null;
		}
	}

}
