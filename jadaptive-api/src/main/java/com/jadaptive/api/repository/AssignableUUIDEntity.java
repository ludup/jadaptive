package com.jadaptive.api.repository;

import java.util.Collection;
import java.util.HashSet;

public abstract class AssignableUUIDEntity extends AbstractUUIDEntity {

	private static final long serialVersionUID = -5734236381558890213L;
	
	Collection<String> roles = new HashSet<>();
	Collection<String> users = new HashSet<>();
	
	public Collection<String> getRoles() {
		return roles;
	}
	public void setRoles(Collection<String> roles) {
		this.roles = roles;
	}
	public Collection<String> getUsers() {
		return users;
	}
	public void setUsers(Collection<String> users) {
		this.users = users;
	}
	
	
}
