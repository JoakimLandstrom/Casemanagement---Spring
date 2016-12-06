package se.plushogskolan.casemanagement.model;

import java.util.Collection;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;

@Entity
public class User extends AbstractEntity {

	@Column(unique = true)
	private String username;

	@ManyToOne
	private Team team;

	@OneToMany(mappedBy = "user")
	private Collection<WorkItem> workItems;

	private boolean isActive = true;

	private String firstName = "";

	private String lastName = "";

	public User(String username) {
		this.username = username;
		this.team = null;
	}

	protected User() {
	}

	@Override
	public boolean equals(Object other) {

		if (this == other) {
			return true;
		}

		if (null == other) {
			return false;
		}

		if (other instanceof User) {
			User otherUser = (User) other;
			return firstName.equals(otherUser.getFirstName()) && lastName.equals(otherUser.getLastName())
					&& username.equals(otherUser.getUsername()) && isActive == otherUser.isActive();
		}
		return false;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((username == null) ? 0 : username.hashCode());
		return result;
	}

	public boolean isActive() {
		return isActive;
	}

	public Team getTeam() {
		return team;
	}

	public Collection<WorkItem> getWorkItems() {
		return workItems;
	}

	public String getUsername() {
		return username;
	}

	public String getFirstName() {
		return firstName;
	}

	public String getLastName() {
		return lastName;
	}

	public User setFirstName(String firstName) {
		this.firstName = firstName;
		return this;
	}

	public User setLastName(String lastName) {
		this.lastName = lastName;
		return this;
	}

	public User setUsername(String username) {
		this.username = username;
		return this;
	}

	public User setTeam(Team team) {
		this.team = team;
		return this;
	}

	public User setActive(boolean isActive) {
		this.isActive = isActive;
		return this;
	}

}
