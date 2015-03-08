package xy.command.model.instance;

import java.util.Collections;
import java.util.List;

import xy.command.model.OptionalPart;

public class OptionalPartInstance extends ArgumentGroupInstance {

	public boolean active;

	public OptionalPartInstance(OptionalPart model) {
		super(model);
		this.active = model.activeByDefault;
	}

	@Override
	public List<String> listArgumentValues() {
		if (active) {
			return listArgumentValues();
		} else {
			return Collections.emptyList();
		}
	}

}
