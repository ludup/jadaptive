package com.jadaptive.entity;

import java.util.Collection;
import java.util.Map;

import com.jadaptive.entity.template.EntityTemplate;
import com.jadaptive.entity.template.EntityTemplateService;
import com.jadaptive.repository.RepositoryException;

public class MockEntityTemplateService implements EntityTemplateService {

	Map<String,EntityTemplate> templates;
	
	public MockEntityTemplateService(Map<String,EntityTemplate> templates) {
		this.templates = templates;
	}
	@Override
	public EntityTemplate get(String uuid) throws RepositoryException, EntityException {
		return templates.get(uuid);
	}

	@Override
	public Collection<EntityTemplate> list() throws RepositoryException, EntityException {
		return templates.values();
	}

	@Override
	public void saveOrUpdate(EntityTemplate template) throws RepositoryException, EntityException {

	}

	@Override
	public void delete(String uuid) throws EntityException {

	}

}