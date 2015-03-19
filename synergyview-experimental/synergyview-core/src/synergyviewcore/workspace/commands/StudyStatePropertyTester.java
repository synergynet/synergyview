package synergyviewcore.workspace.commands;

import org.eclipse.core.expressions.PropertyTester;

import synergyviewcore.project.ui.model.StudyNode;

public class StudyStatePropertyTester extends PropertyTester {
	@Override
	public boolean test(Object receiver, String property, Object[] args,
			Object expectedValue) {
		if (receiver instanceof StudyNode) {
			boolean expectedBooleanValue = ((Boolean) expectedValue).booleanValue();
			StudyNode projectNode = (StudyNode) receiver;
			if (property.compareTo("isProjectOpened")==0) {
				boolean isProjectResourceOpened = projectNode.getResource().isOpen();
				return (expectedBooleanValue == isProjectResourceOpened) ? true : false;
			} else if (property.compareTo("isProjectShared")==0) {
				boolean isProjectResourceShared = projectNode.isProjectShared();
				return (expectedBooleanValue == isProjectResourceShared) ? true : false;
			}
		}
		return false;
	}

	protected boolean toBoolean(Object expectedValue) {
		if (expectedValue instanceof Boolean) {
			return ((Boolean) expectedValue).booleanValue();
		}
		return true;
	}

}
