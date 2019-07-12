package xy.command.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import xy.command.instance.ArgumentPageInstance;



public class ArgumentPage  implements Serializable {

	protected static final long serialVersionUID = 1L;
	
	// @OnlineHelp("This title will identify the current element")
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



	public ArgumentPageInstance instanciate() {
		return  new ArgumentPageInstance(this);
	}
}
