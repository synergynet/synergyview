package synergyviewcore.sharing.model;

import java.util.Date;

import org.tmatesoft.svn.core.SVNURL;

import uk.ac.durham.tel.commons.model.PropertySupportObject;

public class SharingInfo extends PropertySupportObject {
	private String ownerName;
	private String projectName;
	private Date commitDate;
	private SVNURL url;
	public void setOwnerName(String ownerName) {
		this.ownerName = ownerName;
	}
	public String getOwnerName() {
		return ownerName;
	}
	public void setProjectName(String projectName) {
		this.projectName = projectName;
	}
	public String getProjectName() {
		return projectName;
	}
	public void setCommitDate(Date commitDate) {
		this.commitDate = commitDate;
	}
	public Date getCommitDate() {
		return commitDate;
	}
	public void setUrl(SVNURL url) {
		this.url = url;
	}
	public SVNURL getUrl() {
		return url;
	}

}
