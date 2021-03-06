package com.jadaptive.plugins.ssh.management.commands.jobs;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;

import com.jadaptive.api.jobs.Job;
import com.jadaptive.api.jobs.JobService;
import com.jadaptive.api.scheduler.CronSchedule;
import com.jadaptive.api.scheduler.SchedulerService;
import com.jadaptive.plugins.sshd.commands.AbstractTenantAwareCommand;
import com.sshtools.common.permissions.PermissionDeniedException;
import com.sshtools.server.vsession.UsageException;
import com.sshtools.server.vsession.UsageHelper;
import com.sshtools.server.vsession.VirtualConsole;

public class CancelJob extends AbstractTenantAwareCommand {
	
	@Autowired
	private JobService jobService; 
	
	@Autowired
	SchedulerService schedulerService; 
	
	public CancelJob() {
		super("cancel-job", "Automation", 
				UsageHelper.build("cancel-job <id|name>"), 
				"Cancel a scheduled job");
	}

	@Override
	protected void doRun(String[] args, VirtualConsole console)
			throws IOException, PermissionDeniedException, UsageException {
		
		
		if(args.length < 2) {
			throw new UsageException("You must provide a name and template for this Job");
		}
		
		String name = args[1];
				
		Job job = jobService.getJob(name);
		long count = schedulerService.getJobSchedulesCount(job);
	
		if(count==0) {
			console.println(String.format("There are no schedules for job %s", name));
			return;
		}
		
		Iterable<CronSchedule> schedules = schedulerService.getJobSchedules(job);
		
		if(count==1) {
			CronSchedule cs = schedules.iterator().next();
			String resp = console.readLine(String.format("Cancel job %s with schedule %s (y/n): ", name, cs.getExpression()));
			if(resp.toLowerCase().contains("y")) {
				schedulerService.cancelSchedule(cs);
				console.println(String.format("Cancelled schedule for %s", name));
			}
		} else {
			for(CronSchedule cs : schedules) {
				String resp = console.readLine(String.format("Cancel job %s with schedule %s (y/n): ", name, cs.getExpression()));
				if(resp.toLowerCase().contains("y")) {
					schedulerService.cancelSchedule(cs);
				}
			}
		}
	}

}
