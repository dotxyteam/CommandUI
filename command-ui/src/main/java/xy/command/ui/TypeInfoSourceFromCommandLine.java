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
import xy.reflect.ui.util.ReflectionUIError;
import xy.reflect.ui.util.ReflectionUIUtils;

public class TypeInfoSourceFromCommandLine implements ITypeInfoSource {

	private CommandLine commandLine;
	private SpecificitiesIdentifier specificitiesIdentifier;

	public TypeInfoSourceFromCommandLine(CommandLine commandLine, SpecificitiesIdentifier specificitiesIdentifier) {
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
	public ITypeInfo getTypeInfo(ReflectionUI reflectionUI) {
		return new TypeInfo(reflectionUI);
	}

	protected IFieldInfo getFieldInfoFromCommandLinePart(ReflectionUI reflectionUI, AbstractCommandLinePart part,
			ArgumentPage argumentPage, ITypeInfo commandLineTypeInfo) {
		if (part instanceof FixedArgument) {
			return null;
		} else if (part instanceof InputArgument) {
			return new FieldInfoFromInputArgument(reflectionUI, (InputArgument) part, argumentPage, commandLine,
					commandLineTypeInfo);
		} else if (part instanceof DirectoryArgument) {
			return new FieldInfoFromDirectoryArgument(reflectionUI, (DirectoryArgument) part, argumentPage, commandLine,
					commandLineTypeInfo);
		} else if (part instanceof FileArgument) {
			return new FieldInfoFromFileArgument(reflectionUI, (FileArgument) part, argumentPage, commandLine,
					commandLineTypeInfo);
		} else if (part instanceof Choice) {
			return new FieldInfoFromChoice(reflectionUI, (Choice) part, argumentPage, commandLine, commandLineTypeInfo);
		} else if (part instanceof OptionalPart) {
			return new FieldInfoFromOptionalPart(reflectionUI, (OptionalPart) part, argumentPage, commandLine,
					commandLineTypeInfo);
		} else if (part instanceof MultiplePart) {
			return new FieldInfoFromMultiplePart(reflectionUI, (MultiplePart) part, argumentPage, commandLine,
					commandLineTypeInfo);
		} else {
			throw new ReflectionUIError();
		}
	}

	protected class TypeInfo extends DefaultTypeInfo {

		public TypeInfo(ReflectionUI reflectionUI) {
			super(reflectionUI, new JavaTypeInfoSource(CommandLineInstance.class, null));
		}

		@Override
		public String getCaption() {
			return commandLine.title;
		}

		@Override
		public List<IMethodInfo> getMethods() {
			List<IMethodInfo> result = new ArrayList<IMethodInfo>();
			result.add(new ExecutionMethodInfo(reflectionUI));
			if ((commandLine.description != null) && (commandLine.description.trim().length() > 0)) {
				result.add(new HelpMethodInfo(reflectionUI));
			}
			return result;
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
			Capsule result = new Capsule((CommandLineInstance) object);
			reflectionUI.registerPrecomputedTypeInfoObject(result, new CapsuleTypeInfo(reflectionUI));
			return result;
		}

		@Override
		public ITypeInfo getType() {
			return new CapsuleTypeInfo(reflectionUI);
		}

	}

	protected class CapsuleTypeInfo extends DefaultTypeInfo {

		public CapsuleTypeInfo(ReflectionUI reflectionUI) {
			super(reflectionUI, new JavaTypeInfoSource(Capsule.class, new SpecificitiesIdentifier(
					new TypeInfo(reflectionUI).getName(), new CapsuleFieldInfo(reflectionUI).getName())));
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
					IFieldInfo field = getFieldInfoFromCommandLinePart(reflectionUI, part, argumentPage, this);
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

	protected class HelpMethodInfo extends MethodInfoProxy {

		protected ReflectionUI reflectionUI;

		public HelpMethodInfo(ReflectionUI reflectionUI) {
			super(IMethodInfo.NULL_METHOD_INFO);
			this.reflectionUI = reflectionUI;
		}

		@Override
		public String getName() {
			return "showHelp";
		}

		@Override
		public String getSignature() {
			return ReflectionUIUtils.buildMethodSignature(this);
		}

		@Override
		public String getCaption() {
			return "Show Help";
		}

		@Override
		public boolean isReadOnly() {
			return true;
		}

		@Override
		public ITypeInfo getReturnValueType() {
			return reflectionUI.getTypeInfo(new JavaTypeInfoSource(String.class, null));
		}

		@Override
		public Object invoke(Object object, InvocationData invocationData) {
			return commandLine.description;
		}
	}

}
