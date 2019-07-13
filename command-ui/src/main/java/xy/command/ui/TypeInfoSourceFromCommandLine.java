package xy.command.ui;

import java.awt.Dimension;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

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
import xy.reflect.ui.info.ColorSpecification;
import xy.reflect.ui.info.InfoCategory;
import xy.reflect.ui.info.ResourcePath;
import xy.reflect.ui.info.field.IFieldInfo;
import xy.reflect.ui.info.menu.MenuModel;
import xy.reflect.ui.info.method.AbstractConstructorInfo;
import xy.reflect.ui.info.method.IMethodInfo;
import xy.reflect.ui.info.method.InvocationData;
import xy.reflect.ui.info.method.MethodInfoProxy;
import xy.reflect.ui.info.type.ITypeInfo;
import xy.reflect.ui.info.type.source.ITypeInfoSource;
import xy.reflect.ui.info.type.source.SpecificitiesIdentifier;
import xy.reflect.ui.util.ReflectionUIError;
import xy.reflect.ui.util.ReflectionUIUtils;

public class TypeInfoSourceFromCommandLine implements ITypeInfoSource {

	private class TypeInfoFromCommandLine implements ITypeInfo {

		private ReflectionUI reflectionUI;

		public TypeInfoFromCommandLine(ReflectionUI reflectionUI) {
			this.reflectionUI = reflectionUI;
		}

		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + getOuterType().hashCode();
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
			TypeInfoFromCommandLine other = (TypeInfoFromCommandLine) obj;
			if (!getOuterType().equals(other.getOuterType()))
				return false;
			return true;
		}

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
			return "commandLineInstance - " + commandLine.title;
		}

		@Override
		public String getCaption() {
			return commandLine.title;
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
			return object instanceof CommandLineInstance;
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
			return true;
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
			return TypeInfoSourceFromCommandLine.this;
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
			if (commandLine instanceof CommandLineProject) {
				List<IMethodInfo> result = new ArrayList<IMethodInfo>();
				result.add(getExecutionMethod(reflectionUI));
				return result;
			} else {
				return Collections.emptyList();
			}
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
			List<IFieldInfo> result = new ArrayList<IFieldInfo>();
			for (ArgumentPage argumentPage : commandLine.arguments) {
				for (AbstractCommandLinePart part : argumentPage.parts) {
					IFieldInfo field = getFieldInfoFromCommandLinePart(reflectionUI, part, argumentPage, this);
					if (field == null) {
						continue;
					}
					result.add(field);
				}
			}
			return result;
		}

		@Override
		public List<IMethodInfo> getConstructors() {
			return Collections.<IMethodInfo>singletonList(new AbstractConstructorInfo() {

				@Override
				public Object invoke(Object parentObject, InvocationData invocationData) {
					return new CommandLineInstance(commandLine);
				}
			});
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

		private TypeInfoSourceFromCommandLine getOuterType() {
			return TypeInfoSourceFromCommandLine.this;
		}
	}

	private CommandLine commandLine;
	private SpecificitiesIdentifier specificitiesIdentifier;

	public TypeInfoSourceFromCommandLine(CommandLine commandLine, SpecificitiesIdentifier specificitiesIdentifier) {
		this.commandLine = commandLine;
		this.specificitiesIdentifier = specificitiesIdentifier;
	}

	public IMethodInfo getExecutionMethod(ReflectionUI reflectionUI) {
		return new MethodInfoProxy(IMethodInfo.NULL_METHOD_INFO) {

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
			public InfoCategory getCategory() {
				return new InfoCategory("Finish", Integer.MAX_VALUE);
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
						CommandMonitoringDialog d = new CommandMonitoringDialog(null, commandText,
								project.executionDir);
						d.setVisible(true);
					}
				});
				return null;
			}

		};
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
		return new TypeInfoFromCommandLine(reflectionUI);
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

}
