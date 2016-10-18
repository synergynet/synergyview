package synergyviewcore.databinding.validation;

import java.util.List;

import org.eclipse.core.databinding.validation.IValidator;
import org.eclipse.core.databinding.validation.ValidationStatus;
import org.eclipse.core.runtime.IStatus;

/**
 * The Class NotEmptyOrExistValidator.
 */
public class NotEmptyOrExistValidator implements IValidator {

    /** The existing entries. */
    private final List<String> existingEntries;

    /** The fieldname. */
    private final String fieldname;

    /**
     * Instantiates a new not empty or exist validator.
     * 
     * @param fieldname
     *            the fieldname
     * @param existingEntries
     *            the existing entries
     */
    public NotEmptyOrExistValidator(String fieldname, List<String> existingEntries) {
	this.fieldname = fieldname;
	this.existingEntries = existingEntries;

    }

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.core.databinding.validation.IValidator#validate(java.lang .Object)
     */
    public IStatus validate(Object value) {
	String string = (String) value;
	if ((string == null) || (string.trim().length() == 0)) {
	    return ValidationStatus.error("Please enter a value for " + fieldname + ".");
	}
	if (existingEntries.contains(value)) {
	    return ValidationStatus.error("Already exist.");
	}
	return ValidationStatus.ok();
    }
}
