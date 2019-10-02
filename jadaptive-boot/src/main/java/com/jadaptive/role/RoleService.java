package com.jadaptive.role;

import java.util.Collection;

import com.jadaptive.tenant.AbstractTenantAwareObjectService;
import com.jadaptive.tenant.events.TenantCreatedEvent;
import com.jadaptive.user.User;

public interface RoleService extends AbstractTenantAwareObjectService<Role> {

	void onTenantCreated(TenantCreatedEvent evt);

	Role getAdministrationRole();

	Role getEveryoneRole();

	void assignRole(Role role, User user);

	void unassignRole(Role role, User user);

	Collection<Role> getRoles(User user);

}