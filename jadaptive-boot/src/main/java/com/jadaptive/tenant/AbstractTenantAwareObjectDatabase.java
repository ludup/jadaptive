package com.jadaptive.tenant;

import java.util.Collection;

import com.jadaptive.entity.EntityException;
import com.jadaptive.repository.AbstractUUIDEntity;
import com.jadaptive.repository.RepositoryException;

public interface AbstractTenantAwareObjectDatabase<T extends AbstractUUIDEntity> {

	Collection<T> list() throws RepositoryException, EntityException;

	T get(String uuid) throws RepositoryException, EntityException;

	void delete(String uuid) throws RepositoryException, EntityException;

	void saveOrUpdate(T obj) throws RepositoryException, EntityException;

	Collection<T> table(String search, String order, int start, int length);

	long count();

	void delete(T obj) throws RepositoryException, EntityException;

}