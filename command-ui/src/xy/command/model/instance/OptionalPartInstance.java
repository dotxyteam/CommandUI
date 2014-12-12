package xy.command.model.instance;

import java.util.Collections;
import java.util.List;

import xy.command.model.OptionalPart;

public class OptionalPartInstance extends ArgumentGroupInstance {

	public boolean value;

	public OptionalPartInstance(OptionalPart model) {
		super(model);
		this.value = model.defaultValue;
	}

	@Override
	public List<String> listArgumentValues() {
		if (value) {
			return listArgumentValues();
		} else {
			return Collections.emptyList();
		}
	}

}
