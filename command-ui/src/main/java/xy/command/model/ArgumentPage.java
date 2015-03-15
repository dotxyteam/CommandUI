package xy.command.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;


public class ArgumentPage  implements Serializable {

	private static final long serialVersionUID = 1L;
	public String title;
	public List<AbstractCommandLinePart> parts = new ArrayList<AbstractCommandLinePart>();
	


	@Override
	public String toString() {
		return title;
	}
}
