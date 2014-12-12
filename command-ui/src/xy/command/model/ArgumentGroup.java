package xy.command.model;

import java.util.ArrayList;
import java.util.List;

import xy.command.model.instance.ArgumentGroupInstance;

public class ArgumentGroup extends AbstractCommandLinePart {

	protected static final long serialVersionUID = 1L;
	public Layout layout = Layout.COLUMN;
	public Cardinality cardinality = Cardinality.ONE;
	public List<AbstractCommandLinePart> parts = new ArrayList<AbstractCommandLinePart>();

	public enum Layout {
		ROW, COLUMN
	}

	public enum Cardinality {
		ONE, MANY
	}

	@Override
	public ArgumentGroupInstance createInstance() {
		return new ArgumentGroupInstance(this);
	}

	


}
