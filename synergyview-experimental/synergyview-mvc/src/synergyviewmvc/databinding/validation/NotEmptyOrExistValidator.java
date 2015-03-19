package synergyviewmvc.databinding.validation;

import java.util.List;

import org.eclipse.core.databinding.validation.IValidator;
import org.eclipse.core.databinding.validation.ValidationStatus;
import org.eclipse.core.runtime.IStatus;

public class NotEmptyOrExistValidator implements IValidator {
	private final String fieldname;
	private final List<String> existingEntries;
	
	public NotEmptyOrExistValidator(String fieldname, List<String> existingEntries) {
		this.fieldname = fieldname;
		this.existingEntries = existingEntries;
		
	}

	public IStatus validate(Object value) {
		String string = (String) value;
		if (string == null || string.trim().length() == 0) {
			return ValidationStatus.error("Please enter a value for "
					+ fieldname + ".");
		}
		if (existingEntries.contains(value))
			return ValidationStatus.error("Already exist.");
		return ValidationStatus.ok();
	}
}
