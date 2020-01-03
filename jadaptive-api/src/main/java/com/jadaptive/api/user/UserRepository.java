package com.jadaptive.user;

import com.jadaptive.tenant.AbstractTenantAwareObjectDatabase;

public interface UserRepository extends AbstractTenantAwareObjectDatabase<DefaultUser> {

	void createUser(DefaultUser user, char[] password);

	void setPassword(User user, char[] password);

	boolean verifyPassword(User user, char[] password);

	User findUsername(String username);

}