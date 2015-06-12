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
import xy.command.model.instance.MultiplePartInstance.MultiplePartInstanceOccurrence;
import xy.command.ui.util.CommandUIUtils;
import xy.command.ui.util.ValidationError;
import xy.reflect.ui.ReflectionUI;
import xy.reflect.ui.control.EmbeddedFormControl;
import xy.reflect.ui.control.FileControl;
import xy.reflect.ui.control.ListControl;
import xy.reflect.ui.control.PolymorphicEmbeddedForm;
import xy.reflect.ui.info.IInfoCollectionSettings;
import xy.reflect.ui.info.field.IFieldInfo;
import xy.reflect.ui.info.InfoCategory;
import xy.reflect.ui.info.method.AbstractConstructorMethodInfo;
import xy.reflect.ui.info.method.IMethodInfo;
import xy.reflect.ui.info.parameter.IParameterInfo;
import xy.reflect.ui.info.type.DefaultTypeInfo;
import xy.reflect.ui.info.type.ITypeInfo;
import xy.reflect.ui.info.type.custom.FileTypeInfo;
import xy.reflect.ui.info.type.custom.TextualTypeInfo;
import xy.reflect.ui.info.type.iterable.IListTypeInfo;
import xy.reflect.ui.info.type.iterable.util.IListAction;
import xy.reflect.ui.info.type.iterable.util.ItemPosition;
import xy.reflect.ui.info.type.iterable.util.structure.IListStructuralInfo;
import xy.reflect.ui.info.type.iterable.util.structure.TabularTreetStructuralInfo;
import xy.reflect.ui.info.type.source.ITypeInfoSource;
import xy.reflect.ui.info.type.source.JavaTypeInfoSource;
import xy.reflect.ui.util.ReflectionUIUtils;

public class CommandLinePlayer extends ReflectionUI {

	protected  static Map<AbstractCommandLinePart, ArgumentPage> pageByPart = new WeakHashMap<AbstractCommandLinePart, ArgumentPage>();
	protected  static Map<ArgumentPage, CommandLine> commandLineByPage = new WeakHashMap<ArgumentPage, CommandLine>();

	public static void main(String[] args) {
		CommandUIUtils.setupLookAndFeel();
		CommandLine commandLine = new CommandLine();
		if (args.length >= 1) {
			commandLine.loadFromFile(new File(args[0]));
		} else {
			commandLine.loadFromFile(new File("example.cml"));
		}
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

	protected void setPageCommandLine(ArgumentPage page, CommandLine commandLine) {
		commandLineByPage.put(page, commandLine);
	}

	protected void setPartsPage(List<AbstractCommandLinePart> parts,
			ArgumentPage page) {
		for (AbstractCommandLinePart part : parts) {
			pageByPart.put(part, page);
			if (part instanceof ArgumentGroup) {
				setPartsPage(((ArgumentGroup) part).parts, page);
			} else if (part instanceof Choice) {
				for (ArgumentGroup group : ((Choice) part).options) {
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
	public List<Component> createCommonToolbarControls(final JPanel form,
			IInfoCollectionSettings settings) {
		List<Component> result = new ArrayList<Component>(
				super.createCommonToolbarControls(form, settings));

		Object object = getObjectByForm().get(form);
		if (object instanceof CommandLineInstance) {
			result.add(createRunButton(form));
		}

		return result;
	}

	protected Component createRunButton(final JPanel form) {
		final JButton runButton = new JButton("Execute");
		runButton.setToolTipText("Start the execution");
		runButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent ev) {
				try {
					CommandLineInstance instance = (CommandLineInstance) getObjectByForm()
							.get(form);
					try {
						instance.validate();
					} catch (Exception e) {
						throw new ValidationError("Validation Error: "
								+ e.toString(), e);
					}
					launchCommandLine(instance, form);
				} catch (Throwable t) {
					handleExceptionsFromDisplayedUI(runButton, t);
				}
			}
		});
		return runButton;
	}

	protected static IFieldInfo getFieldInfo(AbstractCommandLinePart part,
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

	protected static IFieldInfo getOptionalPartFieldInfo(final OptionalPart part,
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
			public Map<String, Object> getSpecificProperties() {
				return Collections.emptyMap();
			}

			@Override
			public Object getValue(Object object) {
				OptionalPartInstance instance = (OptionalPartInstance) typeInfoSource
						.getFieldValueSources(object).get(partIndex);
				if (instance.active == false) {
					return null;
				} else {
					return instance;
				}
			}

			@Override
			public Object[] getValueOptions(Object object) {
				return null;
			}

			@Override
			public void setValue(Object object, Object value) {
				OptionalPartInstance instance = (OptionalPartInstance) typeInfoSource
						.getFieldValueSources(object).get(partIndex);
				if (value == null) {
					instance.active = false;
				} else {
					instance.active = true;
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
			public String getOnlineHelp() {
				return part.description;
			}
		};
	}

	protected  static InfoCategory getPartCategory(AbstractCommandLinePart part) {
		ArgumentPage page = pageByPart.get(part);
		CommandLine cmdLine = commandLineByPage.get(page);
		int pageIndex = cmdLine.arguments.indexOf(page);
		return new InfoCategory(page.title, pageIndex);
	}

	protected static IFieldInfo getInputArgumentFieldInfo(
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
			public Map<String, Object> getSpecificProperties() {
				return Collections.emptyMap();
			}

			@Override
			public Object getValue(Object object) {
				InputArgumentInstance instance = (InputArgumentInstance) typeInfoSource
						.getFieldValueSources(object).get(partIndex);
				return instance.value;
			}

			@Override
			public Object[] getValueOptions(Object object) {
				return null;
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
				return new TextualTypeInfo(typeInfoSource.getPlayer(),
						String.class);
			}

			@Override
			public InfoCategory getCategory() {
				return getPartCategory(part);
			}

			@Override
			public String getOnlineHelp() {
				return part.description;
			}
		};
	}

	protected static IFieldInfo getFixedArgumentFieldInfo(
			final FixedArgument part,
			final IPartsAsTypeInfoSource typeInfoSource, final int partIndex) {
		return null;
	}

	protected static IFieldInfo getFileArgumentFieldInfo(final FileArgument part,
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
			public Map<String, Object> getSpecificProperties() {
				return Collections.emptyMap();
			}

			@Override
			public Object getValue(Object object) {
				FileArgumentInstance instance = (FileArgumentInstance) typeInfoSource
						.getFieldValueSources(object).get(partIndex);
				return new File(instance.value);
			}

			@Override
			public Object[] getValueOptions(Object object) {
				return null;
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
					public Component createNonNullFieldValueControl(
							Object object, IFieldInfo field) {
						return new FileControl(reflectionUI, object, field) {
							protected static final long serialVersionUID = 1L;

							@Override
							public void configureFileChooser(
									JFileChooser fileChooser, File currentFile) {
								super.configureFileChooser(fileChooser,
										currentFile);
								part.configureFileChooser(fileChooser);
							}
						};
					}

				};
			}

			@Override
			public InfoCategory getCategory() {
				return getPartCategory(part);
			}

			@Override
			public String getOnlineHelp() {
				return part.description;
			}
		};
	}

	protected static IFieldInfo getChoiceFieldInfo(final Choice part,
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
			public Map<String, Object> getSpecificProperties() {
				return Collections.emptyMap();
			}

			@Override
			public Object getValue(Object object) {
				ChoiceInstance instance = (ChoiceInstance) typeInfoSource
						.getFieldValueSources(object).get(partIndex);
				if (instance.chosenOption == -1) {
					return null;
				} else {
					ArgumentGroupInstance groupInstance = instance.optionInstances
							.get(instance.chosenOption);
					return groupInstance;
				}
			}

			@Override
			public Object[] getValueOptions(Object object) {
				return null;
			}

			@Override
			public void setValue(Object object, Object value) {
				ChoiceInstance instance = (ChoiceInstance) typeInfoSource
						.getFieldValueSources(object).get(partIndex);
				ArgumentGroupInstance groupInstance = (ArgumentGroupInstance) value;
				if (groupInstance == null) {
					instance.chosenOption = -1;
				} else {
					ArgumentGroup group = groupInstance.getModel();
					instance.chosenOption = part.options.indexOf(group);
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
			public String getOnlineHelp() {
				return part.description;
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
						for (final ArgumentGroup optionEntry : part.options) {
							ITypeInfoSource subTypeSource = new CommandLinePlayer.ArgumentGroupAsTypeInfoSource(
									typeInfoSource.getPlayer(), optionEntry) {

								@Override
								public String getTypeCaption() {
									return optionEntry.title;
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

							protected static final long serialVersionUID = 1L;

							@Override
							protected  String getEnumerationValueCaption(
									ITypeInfo actualFieldValueType) {
								PartsAsTypeInfo type = (PartsAsTypeInfo) actualFieldValueType;
								ArgumentGroupAsTypeInfoSource typeSource = (ArgumentGroupAsTypeInfoSource) type
										.getTypeInfoSource();
								ArgumentGroup model = typeSource.getModel();
								String optionKey = model.title;
								return optionKey;
							}

						};
					}
				};
			}
		};
	}

	protected static IFieldInfo getArgumentGroupFieldInfo(
			final ArgumentGroup part,
			final IPartsAsTypeInfoSource typeInfoSource, final int partIndex) {
		return new IFieldInfo() {

			@Override
			public String getName() {
				return part.title;
			}

			@Override
			public String getOnlineHelp() {
				return part.description;
			}

			@Override
			public Map<String, Object> getSpecificProperties() {
				return Collections.emptyMap();
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
			public Object[] getValueOptions(Object object) {
				return null;
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

	protected static IFieldInfo getMultiplePartFieldInfo(final MultiplePart part,
			final IPartsAsTypeInfoSource typeInfoSource, final int partIndex) {
		return new IFieldInfo() {

			@Override
			public String getName() {
				return part.title;
			}

			@Override
			public String getOnlineHelp() {
				return part.description;
			}

			@Override
			public Map<String, Object> getSpecificProperties() {
				return Collections.emptyMap();
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
				MultiplePartInstance instance = (MultiplePartInstance) typeInfoSource
						.getFieldValueSources(object).get(partIndex);
				return instance;
			}

			@Override
			public Object[] getValueOptions(Object object) {
				return null;
			}

			@Override
			public ITypeInfo getType() {
				return new MultiplePartListTypeInfo(typeInfoSource.getPlayer(),
						part);
			}

			@Override
			public InfoCategory getCategory() {
				return getPartCategory(part);
			}
		};
	}

	protected  void launchCommandLine(CommandLineInstance instance, JPanel form) {
		String cmd = instance.getCommandlineString();
		File workingDir = instance.getModel().executionDir;
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

		protected  CommandLinePlayer player;
		protected  IPartsAsTypeInfoSource typeInfoSource;

		public PartsAsTypeInfo(CommandLinePlayer player,
				IPartsAsTypeInfoSource typeInfoSource) {
			super(player, Object.class);
			this.player = player;
			this.typeInfoSource = typeInfoSource;
		}

		@Override
		public int hashCode() {
			return typeInfoSource.hashCode();
		}

		@Override
		public boolean equals(Object obj) {
			if (!(obj instanceof PartsAsTypeInfo)) {
				return false;
			}
			PartsAsTypeInfo other = (PartsAsTypeInfo) obj;
			return typeInfoSource.equals(other.typeInfoSource);
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
		public boolean supportsInstance(Object object) {
			return isCommandLinePartInstanceList(object);
		}

		protected  boolean isCommandLinePartInstanceList(Object object) {
			if (!(object instanceof List)) {
				return false;
			}
			Class<?> itemType = ReflectionUIUtils.getJavaGenericTypeParameter(
					new JavaTypeInfoSource(object.getClass()), List.class, 0);
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
								Map<Integer, Object> valueByParameterPosition) {
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
		public String getOnlineHelp() {
			return typeInfoSource.getTypeDocumentation();
		}

		@Override
		public void validate(Object object) throws Exception {
			if (object instanceof CommandLineInstance) {
				((CommandLineInstance) object).validate();
			} else if (object instanceof ArgumentGroupInstance) {
				((ArgumentGroupInstance) object).validate();
			}
		}

	}

	public static class CommandLineAsTypeInfoSource implements
			IPartsAsTypeInfoSource {
		protected CommandLine model;
		protected CommandLinePlayer player;

		public CommandLineAsTypeInfoSource(CommandLinePlayer player,
				CommandLine model) {
			this.player = player;
			this.model = model;
		}

		@Override
		public int hashCode() {
			return model.hashCode();
		}

		@Override
		public boolean equals(Object obj) {
			if (!(obj instanceof CommandLineAsTypeInfoSource)) {
				return false;
			}
			CommandLineAsTypeInfoSource other = (CommandLineAsTypeInfoSource) obj;
			return model.equals(other.model);
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
			return model.description;
		}

	}

	public static class ArgumentGroupAsTypeInfoSource implements
			IPartsAsTypeInfoSource {

		protected CommandLinePlayer player;
		protected ArgumentGroup model;

		public ArgumentGroupAsTypeInfoSource(CommandLinePlayer player,
				ArgumentGroup model) {
			this.player = player;
			this.model = model;
		}

		@Override
		public int hashCode() {
			return model.hashCode();
		}

		@Override
		public boolean equals(Object obj) {
			if (!(obj instanceof ArgumentGroupAsTypeInfoSource)) {
				return false;
			}
			ArgumentGroupAsTypeInfoSource other = (ArgumentGroupAsTypeInfoSource) obj;
			return model.equals(other.model);
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

		protected  MultiplePart multiplePart;
		protected  CommandLinePlayer player;

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
		public List<IListAction> getSpecificActions(Object object,
				IFieldInfo field, List<? extends ItemPosition> selection) {
			return Collections.emptyList();
		}

		@Override
		public boolean supportsInstance(Object object) {
			return object instanceof ArgumentGroupInstance;
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
		public Object[] toArray(Object listValue) {
			MultiplePartInstance instance = (MultiplePartInstance) listValue;
			List<ArgumentGroupInstance> result = new ArrayList<ArgumentGroupInstance>();
			for (MultiplePartInstanceOccurrence occurrence : instance.multiPartInstanceOccurrences) {
				ArgumentGroupInstance occurenceAsGroupInstance = new ArgumentGroupInstance(
						multiplePart);
				occurenceAsGroupInstance.partInstances = occurrence.partInstances;
				result.add(occurenceAsGroupInstance);
			}
			return result.toArray();
		}

		@Override
		public Object fromArray(Object[] array) {
			List<MultiplePartInstanceOccurrence> multiPartInstanceOccurrences = new ArrayList<MultiplePartInstanceOccurrence>();
			for (int i = 0; i < array.length; i++) {
				Object item = array[i];
				ArgumentGroupInstance occurenceAsGroupInstance = (ArgumentGroupInstance) item;
				MultiplePartInstanceOccurrence occurrence = new MultiplePartInstanceOccurrence(
						multiplePart, i);
				occurrence.partInstances = occurenceAsGroupInstance.partInstances;
				multiPartInstanceOccurrences.add(occurrence);
			}
			MultiplePartInstance instance = new MultiplePartInstance(
					multiplePart);
			instance.multiPartInstanceOccurrences = multiPartInstanceOccurrences;
			return instance;
		}

		@Override
		public IListStructuralInfo getStructuralInfo() {
			return new TabularTreetStructuralInfo(player, getItemType());
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
								public Object invoke(
										Object object,
										Map<Integer, Object> valueByParameterPosition) {
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
