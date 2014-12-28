package xy.command.ui;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.WeakHashMap;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import xy.command.model.AbstractCommandLinePart;
import xy.command.model.ArgumentGroup;
import xy.command.model.ArgumentPage;
import xy.command.model.Choice;
import xy.command.model.CommandLine;
import xy.command.model.FileArgument;
import xy.command.model.FixedArgument;
import xy.command.model.InputArgument;
import xy.command.model.MultiplePart;
import xy.command.model.OptionalPart;
import xy.command.model.instance.AbstractCommandLinePartInstance;
import xy.command.model.instance.ArgumentGroupInstance;
import xy.command.model.instance.ChoiceInstance;
import xy.command.model.instance.CommandLineInstance;
import xy.command.model.instance.FileArgumentInstance;
import xy.command.model.instance.InputArgumentInstance;
import xy.command.model.instance.MultiplePartInstance;
import xy.command.model.instance.OptionalPartInstance;
import xy.command.ui.component.CommandMonitoringDialog;
import xy.reflect.ui.ReflectionUI;
import xy.reflect.ui.control.EmbeddedFormControl;
import xy.reflect.ui.control.ListControl;
import xy.reflect.ui.control.PolymorphicEmbeddedForm;
import xy.reflect.ui.info.IInfoCollectionSettings;
import xy.reflect.ui.info.field.IFieldInfo;
import xy.reflect.ui.info.InfoCategory;
import xy.reflect.ui.info.method.AbstractConstructorMethodInfo;
import xy.reflect.ui.info.method.IMethodInfo;
import xy.reflect.ui.info.parameter.IParameterInfo;
import xy.reflect.ui.info.type.DefaultListStructuralInfo;
import xy.reflect.ui.info.type.DefaultTextualTypeInfo;
import xy.reflect.ui.info.type.DefaultTypeInfo;
import xy.reflect.ui.info.type.FileTypeInfo;
import xy.reflect.ui.info.type.IListTypeInfo;
import xy.reflect.ui.info.type.ITypeInfo;
import xy.reflect.ui.info.type.ITypeInfoSource;
import xy.reflect.ui.util.ReflectionUIUtils;

public class CommandLinePlayer extends ReflectionUI {

	protected static Map<AbstractCommandLinePart, ArgumentPage> pageByPart = new WeakHashMap<AbstractCommandLinePart, ArgumentPage>();
	protected static Map<ArgumentPage, CommandLine> commandLineByPage = new WeakHashMap<ArgumentPage, CommandLine>();

	public static void main(String[] args) {
		CommandLine commandLine = new CommandLine();
		commandLine.load(new File("example.cml"));
		CommandLinePlayer player = new CommandLinePlayer();
		player.openObjectFrame(commandLine.createInstance(), commandLine.title,
				null);
	}

	@Override
	public void fillForm(Object object, JPanel form,
			IInfoCollectionSettings settings) {
		if (object instanceof CommandLineInstance) {
			CommandLineInstance instance = (CommandLineInstance) object;
			for (ArgumentPage page : instance.getModel().arguments) {
				setPageCommandLine(page, instance.getModel());
				setPartsPage(page.parts, page);
			}
		}
		super.fillForm(object, form, settings);
	}

	private void setPageCommandLine(ArgumentPage page, CommandLine commandLine) {
		commandLineByPage.put(page, commandLine);
	}

	private void setPartsPage(List<AbstractCommandLinePart> parts,
			ArgumentPage page) {
		for (AbstractCommandLinePart part : parts) {
			pageByPart.put(part, page);
			if (part instanceof ArgumentGroup) {
				setPartsPage(((ArgumentGroup) part).parts, page);
			} else if (part instanceof Choice) {
				for (ArgumentGroup group : ((Choice) part).options.values()) {
					setPartsPage(group.parts, page);
				}
			}
		}
	}

	@Override
	public ITypeInfoSource getTypeInfoSource(final Object object) {
		if (object instanceof CommandLineInstance) {
			final CommandLine model = ((CommandLineInstance) object).getModel();
			return new CommandLineAsTypeInfoSource(this, model);
		} else if (object instanceof ArgumentGroupInstance) {
			final ArgumentGroup model = ((ArgumentGroupInstance) object)
					.getModel();
			return new ArgumentGroupAsTypeInfoSource(this, model);
		} else {
			return super.getTypeInfoSource(object);
		}
	}

	@Override
	public ITypeInfo getTypeInfo(final ITypeInfoSource typeSource) {
		if (typeSource instanceof IPartsAsTypeInfoSource) {
			return new PartsAsTypeInfo(this,
					(IPartsAsTypeInfoSource) typeSource);
		} else {
			return super.getTypeInfo(typeSource);
		}
	}

	@Override
	public List<Component> createCommonToolbarControls(final JPanel form) {
		List<Component> result = new ArrayList<Component>(
				super.createCommonToolbarControls(form));

		Object object = getObjectByForm().get(form);
		if (object instanceof CommandLineInstance) {
			final JButton runButton = new JButton("Execute");
			result.add(runButton);
			runButton.setToolTipText("Start the execution");
			runButton.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					try {
						CommandLineInstance instance = (CommandLineInstance) getObjectByForm()
								.get(form);
						launchCommandLine(instance, form);
					} catch (Throwable t) {
						handleExceptionsFromDisplayedUI(runButton, t);
					}
				}
			});
		}

		return result;
	}

	private static IFieldInfo getFieldInfo(AbstractCommandLinePart part,
			IPartsAsTypeInfoSource typeInfoSource, int partIndex) {
		if (part instanceof Choice) {
			return getChoiceFieldInfo((Choice) part, typeInfoSource, partIndex);
		} else if (part instanceof FileArgument) {
			return getFileArgumentFieldInfo((FileArgument) part,
					typeInfoSource, partIndex);
		} else if (part instanceof FixedArgument) {
			return getFixedArgumentFieldInfo((FixedArgument) part,
					typeInfoSource, partIndex);
		} else if (part instanceof InputArgument) {
			return getInputArgumentFieldInfo((InputArgument) part,
					typeInfoSource, partIndex);
		} else if (part instanceof OptionalPart) {
			return getOptionalPartFieldInfo((OptionalPart) part,
					typeInfoSource, partIndex);
		} else if (part instanceof MultiplePart) {
			return getMultiplePartFieldInfo((MultiplePart) part,
					typeInfoSource, partIndex);
		} else if (part instanceof ArgumentGroup) {
			return getArgumentGroupFieldInfo((ArgumentGroup) part,
					typeInfoSource, partIndex);
		} else {
			throw new AssertionError();
		}
	}

	private static IFieldInfo getOptionalPartFieldInfo(final OptionalPart part,
			final IPartsAsTypeInfoSource typeInfoSource, final int partIndex) {
		final IFieldInfo groupFieldInfo = getArgumentGroupFieldInfo(part,
				typeInfoSource, partIndex);
		return new IFieldInfo() {

			@Override
			public String getName() {
				return groupFieldInfo.getName();
			}

			@Override
			public String getCaption() {
				return groupFieldInfo.getCaption();
			}

			@Override
			public Object getValue(Object object) {
				OptionalPartInstance instance = (OptionalPartInstance) typeInfoSource
						.getFieldValueSources(object).get(partIndex);
				if (instance.value == false) {
					return null;
				} else {
					return instance;
				}
			}

			@Override
			public void setValue(Object object, Object value) {
				OptionalPartInstance instance = (OptionalPartInstance) typeInfoSource
						.getFieldValueSources(object).get(partIndex);
				if (value == null) {
					instance.value = false;
				} else {
					instance.value = true;
				}
			}

			@Override
			public boolean isReadOnly() {
				return false;
			}

			@Override
			public boolean isNullable() {
				return true;
			}

			@Override
			public ITypeInfo getType() {
				return groupFieldInfo.getType();
			}

			@Override
			public InfoCategory getCategory() {
				return getPartCategory(part);
			}

			@Override
			public String getDocumentation() {
				return part.documentation;
			}
		};
	}

	protected static InfoCategory getPartCategory(AbstractCommandLinePart part) {
		ArgumentPage page = pageByPart.get(part);
		CommandLine cmdLine = commandLineByPage.get(page);
		int pageIndex = cmdLine.arguments.indexOf(page);
		return new InfoCategory(page.title, pageIndex);
	}

	private static IFieldInfo getInputArgumentFieldInfo(
			final InputArgument part,
			final IPartsAsTypeInfoSource typeInfoSource, final int partIndex) {
		return new IFieldInfo() {

			@Override
			public String getName() {
				return part.title;
			}

			@Override
			public String getCaption() {
				return part.title;
			}

			@Override
			public Object getValue(Object object) {
				InputArgumentInstance instance = (InputArgumentInstance) typeInfoSource
						.getFieldValueSources(object).get(partIndex);
				return instance.value;
			}

			@Override
			public void setValue(Object object, Object value) {
				InputArgumentInstance instance = (InputArgumentInstance) typeInfoSource
						.getFieldValueSources(object).get(partIndex);
				instance.value = (String) value;
			}

			@Override
			public boolean isReadOnly() {
				return false;
			}

			@Override
			public boolean isNullable() {
				return false;
			}

			@Override
			public ITypeInfo getType() {
				return new DefaultTextualTypeInfo(typeInfoSource.getPlayer(),
						String.class);
			}

			@Override
			public InfoCategory getCategory() {
				return getPartCategory(part);
			}

			@Override
			public String getDocumentation() {
				return part.documentation;
			}
		};
	}

	private static IFieldInfo getFixedArgumentFieldInfo(
			final FixedArgument part,
			final IPartsAsTypeInfoSource typeInfoSource, final int partIndex) {
		return null;
	}

	private static IFieldInfo getFileArgumentFieldInfo(final FileArgument part,
			final IPartsAsTypeInfoSource typeInfoSource, final int partIndex) {
		return new IFieldInfo() {

			@Override
			public String getName() {
				return part.title;
			}

			@Override
			public String getCaption() {
				return part.title;
			}

			@Override
			public Object getValue(Object object) {
				FileArgumentInstance instance = (FileArgumentInstance) typeInfoSource
						.getFieldValueSources(object).get(partIndex);
				return new File(instance.value);
			}

			@Override
			public void setValue(Object object, Object value) {
				File file = (File) value;
				FileArgumentInstance instance = (FileArgumentInstance) typeInfoSource
						.getFieldValueSources(object).get(partIndex);
				instance.value = file.getPath();
			}

			@Override
			public boolean isReadOnly() {
				return false;
			}

			@Override
			public boolean isNullable() {
				return false;
			}

			@Override
			public ITypeInfo getType() {
				return new FileTypeInfo(typeInfoSource.getPlayer()) {

					@Override
					public void configureFileChooser(JFileChooser fileChooser,
							File currentFile) {
						super.configureFileChooser(fileChooser, currentFile);
						part.configureFileChooser(fileChooser);
					}

				};
			}

			@Override
			public InfoCategory getCategory() {
				return getPartCategory(part);
			}

			@Override
			public String getDocumentation() {
				return part.documentation;
			}
		};
	}

	private static IFieldInfo getChoiceFieldInfo(final Choice part,
			final IPartsAsTypeInfoSource typeInfoSource, final int partIndex) {
		return new IFieldInfo() {

			@Override
			public String getName() {
				return part.title;
			}

			@Override
			public String getCaption() {
				return part.title;
			}

			@Override
			public Object getValue(Object object) {
				ChoiceInstance instance = (ChoiceInstance) typeInfoSource
						.getFieldValueSources(object).get(partIndex);
				if (instance.value == null) {
					return null;
				} else {
					ArgumentGroupInstance groupInstance = instance.optionInstances
							.get(instance.value);
					return groupInstance;
				}
			}

			@Override
			public void setValue(Object object, Object value) {
				ChoiceInstance instance = (ChoiceInstance) typeInfoSource
						.getFieldValueSources(object).get(partIndex);
				ArgumentGroupInstance groupInstance = (ArgumentGroupInstance) value;
				if (groupInstance == null) {
					instance.value = null;
				} else {
					ArgumentGroup group = groupInstance.getModel();
					instance.value = ReflectionUIUtils.getKeysFromValue(
							part.options, group).get(0);
				}
			}

			@Override
			public boolean isReadOnly() {
				return false;
			}

			@Override
			public boolean isNullable() {
				return true;
			}

			@Override
			public InfoCategory getCategory() {
				return getPartCategory(part);
			}

			@Override
			public String getDocumentation() {
				return part.documentation;
			}

			@Override
			public ITypeInfo getType() {
				return new DefaultTypeInfo(typeInfoSource.getPlayer(),
						ArgumentGroupInstance.class) {

					@Override
					public boolean isConcrete() {
						return false;
					}

					@Override
					public List<ITypeInfo> getPolymorphicInstanceSubTypes() {
						List<ITypeInfo> result = new ArrayList<ITypeInfo>();
						for (final Map.Entry<String, ArgumentGroup> optionEntry : part.options
								.entrySet()) {
							ArgumentGroup group = optionEntry.getValue();
							ITypeInfoSource subTypeSource = new CommandLinePlayer.ArgumentGroupAsTypeInfoSource(
									typeInfoSource.getPlayer(), group){

										@Override
										public String getTypeCaption() {
											return optionEntry.getKey();
										}
								
							};
							ITypeInfo subType = typeInfoSource.getPlayer()
									.getTypeInfo(subTypeSource);
							result.add(subType);
						}
						return result;
					}

					@Override
					public List<IMethodInfo> getConstructors() {
						return Collections.emptyList();
					}

					@Override
					public Component createFieldControl(Object object,
							IFieldInfo field) {
						return new PolymorphicEmbeddedForm(
								typeInfoSource.getPlayer(), object, field) {

							private static final long serialVersionUID = 1L;

							@Override
							protected String getEnumerationValueCaption(
									ITypeInfo actualFieldValueType) {
								PartsAsTypeInfo type = (PartsAsTypeInfo) actualFieldValueType;
								ArgumentGroupAsTypeInfoSource typeSource = (ArgumentGroupAsTypeInfoSource) type
										.getTypeInfoSource();
								ArgumentGroup model = typeSource.getModel();
								String optionKey = ReflectionUIUtils
										.getKeysFromValue(part.options, model)
										.get(0);
								return optionKey;
							}

						};
					}
				};
			}
		};
	}

	private static IFieldInfo getArgumentGroupFieldInfo(
			final ArgumentGroup part,
			final IPartsAsTypeInfoSource typeInfoSource, final int partIndex) {
		return new IFieldInfo() {

			@Override
			public String getName() {
				return part.title;
			}

			@Override
			public String getDocumentation() {
				return part.documentation;
			}

			@Override
			public String getCaption() {
				return part.title;
			}

			@Override
			public void setValue(Object object, Object value) {
				ArgumentGroupInstance instance = (ArgumentGroupInstance) value;
				typeInfoSource.getFieldValueSources(object).set(partIndex,
						instance);
			}

			@Override
			public boolean isReadOnly() {
				return false;
			}

			@Override
			public boolean isNullable() {
				return false;
			}

			@Override
			public Object getValue(Object object) {
				ArgumentGroupInstance instance = (ArgumentGroupInstance) typeInfoSource
						.getFieldValueSources(object).get(partIndex);
				return instance;
			}

			@Override
			public ITypeInfo getType() {
				CommandLinePlayer player = typeInfoSource.getPlayer();
				return new PartsAsTypeInfo(player,
						new ArgumentGroupAsTypeInfoSource(player, part));
			}

			@Override
			public InfoCategory getCategory() {
				return getPartCategory(part);
			}
		};
	}
	
	
	private static IFieldInfo getMultiplePartFieldInfo(
			final MultiplePart part,
			final IPartsAsTypeInfoSource typeInfoSource, final int partIndex) {
		return new IFieldInfo() {
	
			@Override
			public String getName() {
				return part.title;
			}

			@Override
			public String getDocumentation() {
				return part.documentation;
			}
	
			@Override
			public String getCaption() {
				return part.title;
			}
	
			@Override
			public void setValue(Object object, Object value) {
				ArgumentGroupInstance instance = (ArgumentGroupInstance) value;
				typeInfoSource.getFieldValueSources(object).set(partIndex,
						instance);
			}
	
			@Override
			public boolean isReadOnly() {
				return false;
			}
	
			@Override
			public boolean isNullable() {
				return false;
			}
	
			@Override
			public Object getValue(Object object) {
				ArgumentGroupInstance instance = (ArgumentGroupInstance) typeInfoSource
						.getFieldValueSources(object).get(partIndex);
				return instance;
			}
	
			@Override
			public ITypeInfo getType() {
				return new MultiplePartListTypeInfo(
						typeInfoSource.getPlayer(), part);
			}
	
			@Override
			public InfoCategory getCategory() {
				return getPartCategory(part);
			}
		};
	}

	protected void launchCommandLine(CommandLineInstance instance, JPanel form) {
		String cmd = instance.getCommandlineString();
		String workingDirPath = instance.getModel().executionDir;
		File workingDir = (workingDirPath != null) ? new File(
				instance.getModel().executionDir) : null;
		CommandMonitoringDialog cmdDialog = new CommandMonitoringDialog(
				SwingUtilities.getWindowAncestor(form), cmd, workingDir);
		cmdDialog.setLocationRelativeTo(null);
		cmdDialog.setVisible(true);
	}



	public static interface IPartsAsTypeInfoSource extends ITypeInfoSource {

		List<AbstractCommandLinePart> getFieldTypeInfoSources();

		String getName();

		void setFieldValueSources(Object object,
				List<AbstractCommandLinePartInstance> data);

		List<AbstractCommandLinePartInstance> getFieldValueSources(Object object);

		CommandLinePlayer getPlayer();

		Object instanciate();

		String getTypeCaption();

		String getTypeDocumentation();
	}

	public static class PartsAsTypeInfo extends DefaultTypeInfo {

		protected CommandLinePlayer player;
		protected IPartsAsTypeInfoSource typeInfoSource;

		public PartsAsTypeInfo(CommandLinePlayer player,
				IPartsAsTypeInfoSource typeInfoSource) {
			super(player, IPartsAsTypeInfoSource.class);
			this.player = player;
			this.typeInfoSource = typeInfoSource;
		}

		public IPartsAsTypeInfoSource getTypeInfoSource() {
			return typeInfoSource;
		}

		@Override
		public String getName() {
			return typeInfoSource.getName();
		}

		@Override
		public String getCaption() {
			return typeInfoSource.getTypeCaption();
		}

		@Override
		public boolean supportsValue(Object value) {
			return isCommandLinePartInstanceList(value);
		}

		protected boolean isCommandLinePartInstanceList(Object object) {
			if (!(object instanceof List)) {
				return false;
			}
			Class<?> itemType = ReflectionUIUtils.getJavaTypeParameter(
					object.getClass(), null, List.class, 0);
			if (!AbstractCommandLinePartInstance.class.equals(itemType)) {
				return false;
			}
			return true;
		}

		@Override
		public boolean isConcrete() {
			return true;
		}

		@Override
		public List<IMethodInfo> getMethods() {
			return Collections.emptyList();
		}

		@Override
		public List<IFieldInfo> getFields() {
			List<IFieldInfo> result = new ArrayList<IFieldInfo>();
			int partIndex = 0;
			for (AbstractCommandLinePart part : typeInfoSource
					.getFieldTypeInfoSources()) {
				IFieldInfo field = getFieldInfo(part, typeInfoSource, partIndex);
				if (field != null) {
					result.add(field);
				}
				partIndex++;
			}
			return result;
		}

		@Override
		public List<IMethodInfo> getConstructors() {

			return Collections
					.<IMethodInfo> singletonList(new AbstractConstructorMethodInfo(
							PartsAsTypeInfo.this) {

						@Override
						public Object invoke(Object object,
								Map<String, Object> valueByParameterName) {
							return typeInfoSource.instanciate();
						}

						@Override
						public List<IParameterInfo> getParameters() {
							return Collections.emptyList();
						}
					});
		}

		@Override
		public Component createNonNullFieldValueControl(Object object,
				IFieldInfo field) {
			return new EmbeddedFormControl(player, object, field);
		}

		@Override
		public String getDocumentation() {
			return typeInfoSource.getTypeDocumentation();
		}
		
		
	}

	public static class CommandLineAsTypeInfoSource implements
			IPartsAsTypeInfoSource {
		private CommandLine model;
		private CommandLinePlayer player;

		public CommandLineAsTypeInfoSource(CommandLinePlayer player,
				CommandLine model) {
			this.player = player;
			this.model = model;
		}

		public CommandLine getModel() {
			return model;
		}

		@Override
		public List<AbstractCommandLinePart> getFieldTypeInfoSources() {
			List<AbstractCommandLinePart> result = new ArrayList<AbstractCommandLinePart>();
			for (ArgumentPage page : model.arguments) {
				for (AbstractCommandLinePart part : page.parts) {
					result.add(part);
				}
			}
			return result;
		}

		@Override
		public List<AbstractCommandLinePartInstance> getFieldValueSources(
				Object object) {
			final CommandLineInstance instance = (CommandLineInstance) object;
			return instance.partInstances;
		}

		@Override
		public void setFieldValueSources(Object object,
				List<AbstractCommandLinePartInstance> data) {
			final CommandLineInstance instance = (CommandLineInstance) object;
			instance.partInstances = data;
		}

		@Override
		public String getName() {
			return model.toString();
		}

		@Override
		public String getTypeCaption() {
			return model.toString();
		}

		@Override
		public CommandLinePlayer getPlayer() {
			return player;
		}

		@Override
		public Object instanciate() {
			return new CommandLineInstance(model);
		}

		@Override
		public String getTypeDocumentation() {
			return model.documentation;
		}

	}

	public static class ArgumentGroupAsTypeInfoSource implements
			IPartsAsTypeInfoSource {

		private CommandLinePlayer player;
		private ArgumentGroup model;

		public ArgumentGroupAsTypeInfoSource(CommandLinePlayer player,
				ArgumentGroup model) {
			this.player = player;
			this.model = model;
		}

		public ArgumentGroup getModel() {
			return model;
		}

		@Override
		public List<AbstractCommandLinePart> getFieldTypeInfoSources() {
			return model.parts;
		}

		@Override
		public List<AbstractCommandLinePartInstance> getFieldValueSources(
				Object object) {
			final ArgumentGroupInstance instance = (ArgumentGroupInstance) object;
			return instance.partInstances;
		}

		@Override
		public void setFieldValueSources(Object object,
				List<AbstractCommandLinePartInstance> data) {
			final ArgumentGroupInstance instance = (ArgumentGroupInstance) object;
			instance.partInstances = data;
		}

		@Override
		public String getName() {
			return model.toString();
		}

		@Override
		public String getTypeCaption() {
			return model.toString();
		}

		@Override
		public CommandLinePlayer getPlayer() {
			return player;
		}

		@Override
		public Object instanciate() {
			return new ArgumentGroupInstance(model);
		}

		@Override
		public String getTypeDocumentation() {
			return null;
		}
	}

	public static class MultiplePartListTypeInfo extends DefaultTypeInfo
			implements IListTypeInfo {

		protected MultiplePart multiplePart;
		protected CommandLinePlayer player;

		public MultiplePartListTypeInfo(CommandLinePlayer player,
				MultiplePart multiplePart) {
			super(player, ArgumentGroupInstance.class);
			this.player = player;
			this.multiplePart = multiplePart;
		}

		@Override
		public String getName() {
			return "";
		}

		@Override
		public String getCaption() {
			return "";
		}

		@Override
		public boolean supportsValue(Object value) {
			return value instanceof ArgumentGroupInstance;
		}

		@Override
		public boolean isConcrete() {
			return false;
		}

		@Override
		public List<IMethodInfo> getMethods() {
			return Collections.emptyList();
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
		public Component createNonNullFieldValueControl(Object object,
				IFieldInfo field) {
			return new ListControl(player, object, field);
		}

		@Override
		public List<?> toStandardList(Object value) {
			MultiplePartInstance instance = (MultiplePartInstance) value;
			List<ArgumentGroupInstance> result = new ArrayList<ArgumentGroupInstance>();
			for (List<AbstractCommandLinePartInstance> partInstances : instance.multiPartInstances) {
				ArgumentGroupInstance occurence = new ArgumentGroupInstance(
						multiplePart);
				occurence.partInstances = partInstances;
				result.add(occurence);
			}
			return result;
		}

		@Override
		public Object fromStandardList(List<?> list) {
			List<List<AbstractCommandLinePartInstance>> multiPartInstances = new ArrayList<List<AbstractCommandLinePartInstance>>();
			for (Object item : list) {
				ArgumentGroupInstance occurence = (ArgumentGroupInstance) item;
				multiPartInstances.add(occurence.partInstances);
			}
			MultiplePartInstance instance = new MultiplePartInstance(
					multiplePart);
			instance.multiPartInstances = multiPartInstances;
			return instance;
		}

		@Override
		public IListStructuralInfo getStructuralInfo() {
			return new DefaultListStructuralInfo(player, getItemType());
		}

		@Override
		public ITypeInfo getItemType() {
			return new PartsAsTypeInfo(player,
					new ArgumentGroupAsTypeInfoSource(player, multiplePart)) {

				@Override
				public List<IMethodInfo> getConstructors() {
					return Collections
							.<IMethodInfo> singletonList(new AbstractConstructorMethodInfo(
									MultiplePartListTypeInfo.this) {

								@Override
								public Object invoke(Object object,
										Map<String, Object> valueByParameterName) {
									ArgumentGroupInstance instance = new ArgumentGroupInstance(
											multiplePart);
									return instance;
								}

								@Override
								public List<IParameterInfo> getParameters() {
									return Collections.emptyList();
								}
							});
				}

			};
		}

		@Override
		public boolean isOrdered() {
			return true;
		}
	};

}
