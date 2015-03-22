package xy.command.model.instance;

import java.util.ArrayList;
import java.util.List;

import xy.command.model.AbstractCommandLinePart;
import xy.command.model.MultiplePart;

public class MultiplePartInstance extends ArgumentGroupInstance {

	public List<MultiplePartInstanceOccurrence> multiPartInstanceOccurrences;

	public MultiplePartInstance(MultiplePart model) {
		super(model);
		multiPartInstanceOccurrences = new ArrayList<MultiplePartInstanceOccurrence>();
	}

	public MultiplePart getModel() {
		return (MultiplePart) model;
	}

	public MultiplePartInstanceOccurrence addOccurrence() {
		MultiplePartInstanceOccurrence occurrence = new MultiplePartInstanceOccurrence(
				getModel(), multiPartInstanceOccurrences.size());
		multiPartInstanceOccurrences.add(occurrence);
		return occurrence;
	}

	@Override
	public List<String> listArgumentValues() {
		List<String> result = new ArrayList<String>();
		for (MultiplePartInstanceOccurrence occurrence : multiPartInstanceOccurrences) {
			for (AbstractCommandLinePartInstance part : occurrence.partInstances) {
				result.addAll(part.listArgumentValues());
			}
		}
		return result;
	}

	@Override
	public void validate() throws Exception {
		try {
			for (MultiplePartInstanceOccurrence occurrence : multiPartInstanceOccurrences) {
				occurrence.validate();
			}
		} catch (Exception e) {
			throw contextualizeFieldValidationError(e, ((MultiplePart) model).title);
		}
	}

	public static class MultiplePartInstanceOccurrence extends
			AbstractCommandLinePartInstance {

		public List<AbstractCommandLinePartInstance> partInstances;
		public int position;

		public MultiplePartInstanceOccurrence(MultiplePart model, int position) {
			super(model);
			partInstances = new ArrayList<AbstractCommandLinePartInstance>();
			for (AbstractCommandLinePart part : getModel().parts) {
				partInstances.add(part.createInstance());
			}
			this.position = position;
		}

		public MultiplePart getModel() {
			return (MultiplePart) model;
		}

		@Override
		public List<String> listArgumentValues() {
			List<String> result = new ArrayList<String>();
			for (AbstractCommandLinePartInstance part : partInstances) {
				result.addAll(part.listArgumentValues());
			}
			return result;
		}

		@Override
		public void validate() throws Exception {
			try {
				for (AbstractCommandLinePartInstance partInstance : partInstances) {
					partInstance.validate();
				}
			} catch (Exception e) {
				throw contextualizeFieldValidationError(e, "Item " + (position + 1));
			}

		}

	}

}
