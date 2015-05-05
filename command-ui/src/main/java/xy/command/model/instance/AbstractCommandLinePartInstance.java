package xy.command.model.instance;

import java.util.List;

import xy.command.model.AbstractCommandLinePart;
import xy.command.ui.util.ValidationError;

public abstract class AbstractCommandLinePartInstance {

	protected  AbstractCommandLinePart model;

	public abstract List<String> listArgumentValues();
	public abstract void validate() throws Exception;

	protected  static Exception contextualizeFieldValidationError(Exception error, String fieldTitle) {
		if(fieldTitle == null){
			System.out.println("debug");
		}
		return new ValidationError("("+fieldTitle + ") "
				+ error.toString(), error);
	}
	protected  AbstractCommandLinePartInstance(AbstractCommandLinePart model) {
		this.model = model;
	}
}
