package se.plushogskolan.casemanagement.model;

import javax.persistence.Entity;
import javax.persistence.OneToOne;

@Entity
public final class Issue extends AbstractEntity {

	@OneToOne(mappedBy = "issue")
	private WorkItem workItem;

	private String description;

	protected Issue() {
	}

	public Issue(WorkItem workItem, String description) {
		this.workItem = workItem;
		this.description = description;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj instanceof Issue) {
			Issue otherIssue = (Issue) obj;
			return description.equals(otherIssue.description);
		}
		return false;
	}

	@Override
	public int hashCode() {
		int result = 17;
		result += 31 * id;
		result += 31 * description.hashCode();
		return result;
	}

	public WorkItem getWorkitem() {
		return workItem;
	}
	
	public Issue setWorkItem(WorkItem workItem) {
		this.workItem = workItem;
		return this;
	}

	public String getDescription() {
		return description;
	}
	
	public Issue setDescription(String description) {
		this.description = description;
		return this;
	}
}
