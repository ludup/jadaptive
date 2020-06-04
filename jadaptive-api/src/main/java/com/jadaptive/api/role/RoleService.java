package com.jadaptive.api.role;

import java.util.Collection;

import com.jadaptive.api.user.User;

public interface RoleService {

	public static final String READ_PERMISSION = "role.read";
	public static final String READ_WRITE_PERMISSION = "role.readWrite";

	Role getAdministrationRole();

	Role getEveryoneRole();

	void assignRole(Role role, User... user);

	void unassignRole(Role role, User... user);

	Collection<Role> getRoles(User user);

	Role getRoleByName(String name);

	Role createRole(String roleName, User... users);
	
	Role createRole(String roleName, Collection<User> users);

	void grantPermission(Role role, String... permissions);

	void revokePermission(Role role, String... permissions);

	boolean hasRole(User currentUser, Collection<Role> roles);

	boolean hasRole(User currentUser, Role... roles);

	void deleteRole(Role role);

	Collection<Role> listRoles();

	Role getRoleByUUID(String roleUUID);

	Iterable<Role> allRoles();

	Collection<Role> getRolesByUser(User user);

	Collection<Role> getAllUserRoles();
}
