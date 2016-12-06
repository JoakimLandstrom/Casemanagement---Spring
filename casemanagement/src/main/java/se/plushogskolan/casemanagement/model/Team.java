package se.plushogskolan.casemanagement.model;

import java.util.Collection;

import javax.persistence.Entity;
import javax.persistence.OneToMany;

@Entity
public final class Team extends AbstractEntity {

	private String name;

	private boolean active;

	@OneToMany(mappedBy = "team")
	private Collection<User> users;

	protected Team() {
	}

	public Team(String name) {
		this.active = true;
		this.name = name;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + name.hashCode();
		return result;
	}

	@Override
	public boolean equals(Object other) {

		if (this == other) {
			return true;
		}

		if (null == other) {
			return false;
		}

		if (other instanceof Team) {
			Team otherTeam = (Team) other;
			return name.equals(otherTeam.getName()) && active == otherTeam.isActive();
		}
		return false;
	}

	public boolean isActive() {
		return active;
	}
	
	public Team setActive(boolean active) {
		this.active = active;
		return this;
	}

	public String getName() {
		return name;
	}
	
	public Team setName(String name) {
		this.name = name;
		return this;
	}

	public Collection<User> getUsers() {
		return users;
	}
	
	public void addUser(User user){
		users.add(user);
	}
}
