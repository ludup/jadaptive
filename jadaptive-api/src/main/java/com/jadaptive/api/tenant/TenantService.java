package com.jadaptive.api.tenant;

import javax.servlet.http.HttpServletRequest;

import com.jadaptive.api.entity.ObjectException;
import com.jadaptive.api.permissions.AccessDeniedException;
import com.jadaptive.api.repository.RepositoryException;
import com.jadaptive.api.repository.UUIDObjectService;

public interface TenantService extends UUIDObjectService<Tenant> {

	public static final String SYSTEM_UUID = "4f1b781c-581d-474f-9505-4fea9c5e3909";
	
	Tenant getCurrentTenant() throws RepositoryException, ObjectException;

	void setCurrentTenant(Tenant tenant);

	void clearCurrentTenant();

	Iterable<Tenant> listTenants();

	Tenant getSystemTenant() throws RepositoryException, ObjectException;

	void setCurrentTenant(HttpServletRequest request);

	void setCurrentTenant(String name);

	void deleteTenant(Tenant tenant);

	Tenant getTenantByDomainOrDefault(String name);

	Tenant getTenantByDomain(String name);

	void assertManageTenant() throws AccessDeniedException;

	Tenant resolveTenantName(String username);

	Tenant createTenant(String name, String domain, String... additionalDomains)
			throws RepositoryException, ObjectException;

	Tenant createTenant(String uuid, String name, String primaryDomain, String... additionalDomains)
			throws RepositoryException, ObjectException;

	Tenant createTenant(String uuid, String name, String primaryDomain, boolean system, String... additionalDomains)
			throws RepositoryException, ObjectException;

	Tenant getTenantByName(String name);

	Tenant getTenantByUUID(String uuid);

}
