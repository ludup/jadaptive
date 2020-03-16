package com.jadaptive.api.tenant;

import java.util.HashSet;
import java.util.Set;

import com.jadaptive.api.repository.AbstractUUIDEntity;

public class Tenant extends AbstractUUIDEntity {

	String name;
	String hostname;
	Set<String> alternativeDomains = new HashSet<>();
	
	public Tenant() {
		
	}
	
	public Tenant(String uuid, String name, String hostname, boolean system) {
		this(uuid, name, hostname);
		setSystem(system);
	}

	public Tenant(String uuid, String name, String hostname) {
		this.setUuid(uuid);
		this.name = name;
		this.hostname = hostname;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDomain() {
		return hostname;
	}

	public void setDomain(String domain) {
		this.hostname = domain;
	}
	
	public String getHostname() {
		return hostname;
	}
	
	public void setHostname(String domain) {
		this.hostname = domain;
	}

	public Set<String> getAlternativeDomains() {
		return alternativeDomains;
	}

	public void setAlternativeDomains(Set<String> alternativeDomains) {
		this.alternativeDomains = alternativeDomains;
	}
	
	
}
