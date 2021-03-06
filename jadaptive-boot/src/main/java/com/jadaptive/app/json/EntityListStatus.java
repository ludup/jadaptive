package com.jadaptive.app.json;

import java.util.Collection;

public class EntityListStatus<T> extends RequestStatus {

	Collection<T> resources;
	
	public EntityListStatus(Collection<T> resources) {
		this.resources = resources;
	}

	public EntityListStatus(boolean success, String message) {
		super(success, message);
	}

	public Collection<T> getResources() {
		return resources;
	}

	public void setResource(Collection<T> resources) {
		this.resources = resources;
	}
	
	

}
