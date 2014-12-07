package xy.command.model.instance;

import java.util.Collections;
import java.util.List;

import xy.command.model.OptionalPart;
import xy.command.ui.CommandLinePlayer;

public class OptionalPartInstance extends ArgumentGroupInstance {

	public boolean value;

	public OptionalPartInstance(CommandLinePlayer player, OptionalPart model) {
		super(player, model);
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
