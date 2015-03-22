package xy.command.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import xy.reflect.ui.info.annotation.Documentation;


public class ArgumentPage  implements Serializable {

	private static final long serialVersionUID = 1L;
	
	@Documentation("This title will identify the current element")
	public String title;
	
	public List<AbstractCommandLinePart> parts = new ArrayList<AbstractCommandLinePart>();
	


	@Override
	public String toString() {
		return title;
	}



	public void validate() throws Exception {
		for(AbstractCommandLinePart part: parts){
			part.validate();
		}
		
	}
}
