package com.jadaptive.api.sshd.commands;

import java.io.IOException;

import org.jline.utils.Log;
import org.springframework.beans.factory.annotation.Autowired;

import com.jadaptive.api.sshd.SSHDService;
import com.jadaptive.api.tenant.Tenant;
import com.jadaptive.api.tenant.TenantService;
import com.jadaptive.api.user.User;
import com.jadaptive.api.user.UserService;
import com.sshtools.common.permissions.PermissionDeniedException;
import com.sshtools.common.ssh.SshConnection;
import com.sshtools.server.vsession.Environment;
import com.sshtools.server.vsession.ShellCommand;
import com.sshtools.server.vsession.UsageException;
import com.sshtools.server.vsession.VirtualConsole;

public abstract class AbstractTenantAwareCommand extends ShellCommand {

	@Autowired
	private TenantService tenantService; 
	
	@Autowired
	protected UserService userService; 
	
	protected VirtualConsole console;
	protected String[] args;
	protected User user;
	
	public AbstractTenantAwareCommand(String name, String subsystem, String signature, String description) {
		super(name, subsystem, signature, description);
	}

	@Override
	public void run(String[] args, VirtualConsole console)
			throws IOException, PermissionDeniedException, UsageException {
		
		tenantService.setCurrentTenant(resolveTenant(console.getConnection(), console.getEnvironment()));
		
		this.console = console;
		this.args = args;
		this.user = userService.findUsername(console.getConnection().getUsername());
		
		try {
			doRun(args, console);
		} catch(UsageException e) { 
			console.println(e.getMessage());
		} catch(Throwable e) { 
			Log.error(e);
			console.println(e.getMessage());
		} finally {
			tenantService.clearCurrentTenant();
		}
		
	}
	
	protected void printUsage() {
		console.println(getUsage());
	}

	protected abstract void doRun(String[] args, VirtualConsole console) 
				throws IOException, PermissionDeniedException, UsageException;

	protected Tenant resolveTenant(SshConnection connection, Environment environment) {
		return (Tenant) connection.getProperty(SSHDService.TENANT);
	}
	
	protected char[] promptForPassword(String prompt) {
		String str = console.getLineReader().readLine(prompt, '*');
		return str.toCharArray();
	}

	protected void assertAdministrationPermission() {
		
	}
	


}