package com.jadaptive.api.repository;

import com.jadaptive.api.template.ObjectField;
import com.jadaptive.api.template.FieldType;

public abstract class NamedUUIDEntity extends AbstractUUIDEntity {

	@ObjectField(name = "Name", description = "The name of this object", 
				searchable = true, unique = true, type = FieldType.TEXT)
	String name;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
}
