package synergyviewmvc.annotations.format;

import synergyviewmvc.annotations.model.AnnotationSet;

public interface IFormatter {
	void format(AnnotationSet transcript, StringBuilder bufferToAppend);
}
