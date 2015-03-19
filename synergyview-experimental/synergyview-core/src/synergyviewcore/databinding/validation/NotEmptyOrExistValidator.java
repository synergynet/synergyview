package synergyviewcore.databinding.validation;

import java.util.List;

import org.eclipse.core.databinding.validation.IValidator;
import org.eclipse.core.databinding.validation.ValidationStatus;
import org.eclipse.core.runtime.IStatus;

public class NotEmptyOrExistValidator implements IValidator {
	private final String emptyErrorMessage;
	private final List<String> existingEntries;
	private boolean errorOnExist;
	
	public NotEmptyOrExistValidator(String emptyErrorMessage, List<String> existingEntries, boolean errorOnExist) {
		this.emptyErrorMessage = emptyErrorMessage;
		this.existingEntries = existingEntries;
		this.errorOnExist = errorOnExist;
	}

	public IStatus validate(Object value) {
		String string = (String) value;
		if (string == null || string.trim().length() == 0) {
			return ValidationStatus.error(emptyErrorMessage);
		}
		if (existingEntries.contains(value) && errorOnExist)
			return ValidationStatus.error("Already exist.");
		if (!existingEntries.contains(value) && !errorOnExist)
			return ValidationStatus.error("Does not exist.");
		return ValidationStatus.ok();
	}
}
