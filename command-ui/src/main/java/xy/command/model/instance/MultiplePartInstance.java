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
		MultiplePartInstanceOccurrence occurrence = new MultiplePartInstanceOccurrence(getModel());
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

	public static class MultiplePartInstanceOccurrence extends AbstractCommandLinePartInstance {

		public List<AbstractCommandLinePartInstance> partInstances;

		public MultiplePartInstanceOccurrence(MultiplePart model) {
			super(model);
			partInstances = new ArrayList<AbstractCommandLinePartInstance>();
			for (AbstractCommandLinePart part : getModel().parts) {
				partInstances.add(part.createInstance());
			}

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

	}

}
