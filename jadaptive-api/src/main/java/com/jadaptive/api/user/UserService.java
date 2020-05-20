package com.jadaptive.api.user;

import java.util.Collection;

import com.jadaptive.api.template.EntityTemplate;

public interface UserService {

	public static final String CHANGE_PASSWORD_PERMISSION = "users.changePassword";
	public static final String SET_PASSWORD_PERMISSION = "users.setPassword";
	
	public static final String USER_RESOURCE_KEY = "users";
	
	public static final String READ_PERMISSION = "users.read";
	public static final String READ_WRITE_PERMISSION = "users.readWrite";
	
	boolean verifyPassword(User username, char[] password);

	User getUser(String username);

	void setPassword(User user, char[] newPassword, boolean passwordChangeRequired);

	User getUserByUUID(String uuid);

	void changePassword(User user, char[] oldPassword, char[] newPassword);

	void changePassword(User user, char[] newPassword, boolean passwordChangeRequired);

	Iterable<? extends User> iterateUsers();

	User getUserByEmail(String email);

	Collection<EntityTemplate> getCreateUserTemplates();

	void deleteUser(User confirmedUser);

	void updateUser(User user);

	void createUser(User user, char[] charArray, boolean forceChange);

	boolean supportsLogin(User user);

}
