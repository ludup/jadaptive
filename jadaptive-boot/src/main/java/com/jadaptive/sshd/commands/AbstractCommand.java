package com.jadaptive.sshd.commands;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;

import com.jadaptive.tenant.Tenant;
import com.jadaptive.tenant.TenantService;
import com.jadaptive.user.User;
import com.jadaptive.user.UserService;
import com.sshtools.common.permissions.PermissionDeniedException;
import com.sshtools.common.ssh.SshConnection;
import com.sshtools.server.vsession.Environment;
import com.sshtools.server.vsession.ShellCommand;
import com.sshtools.server.vsession.UsageException;
import com.sshtools.server.vsession.VirtualConsole;

public abstract class AbstractCommand extends ShellCommand {

	@Autowired
	TenantService tenantService; 
	
	@Autowired
	UserService userService; 
	
	protected VirtualConsole console;
	protected String[] args;
	
	public AbstractCommand(String name, String subsystem, String signature, String description) {
		super(name, subsystem, signature, description);
	}

	@Override
	public void run(String[] args, VirtualConsole console)
			throws IOException, PermissionDeniedException, UsageException {
		
		this.console = console;
		this.args = args;
		
		tenantService.setCurrentTenant(resolveTenant(console.getConnection(), console.getEnvironment()));
		
		try {
			doRun(args, console);
		} catch(UsageException e) { 
			console.println(e.getMessage());
		} catch(Throwable e) { 
			console.println(String.format("Unexpected error: %s", e.getMessage()));
		} finally {
			tenantService.clearCurrentTenant();
		}
		
	}

	protected abstract void doRun(String[] args, VirtualConsole console) 
				throws IOException, PermissionDeniedException, UsageException;

	protected Tenant resolveTenant(SshConnection connection, Environment environment) {
		return tenantService.getSystemTenant();
	}
	
	protected char[] promptForPassword(String prompt) {
		String str = console.getLineReader().readLine(prompt, '*');
		return str.toCharArray();
	}

	protected void assertAdministrationPermission() {
		
	}
	


}