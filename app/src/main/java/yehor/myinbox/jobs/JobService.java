package yehor.myinbox.jobs;

import java.util.List;
import org.quartz.CronScheduleBuilder;
import org.quartz.JobBuilder;
import org.quartz.JobDetail;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.Trigger;
import org.quartz.TriggerBuilder;
import org.quartz.impl.StdSchedulerFactory;

public class JobService {

  private static final String JOB_GROUP = "my_inbox_jobs";

  private final Scheduler scheduler;
  private final JobFactory jobFactory;

  public JobService(List<ReportingJob> jobs) throws SchedulerException {
    this.scheduler = new StdSchedulerFactory().getScheduler();
    this.jobFactory = new JobFactory();
    scheduler.setJobFactory(jobFactory);

    jobs.forEach(job -> {

      JobDetail cronJobDetail = JobBuilder.newJob(job.getClass())
          .withIdentity(job.getClass().getName(), JOB_GROUP)
          .build();

      Trigger cronTrigger = TriggerBuilder.newTrigger()
          .withIdentity(job.getClass().getName(), JOB_GROUP)
          .startNow()
          .withSchedule(CronScheduleBuilder.cronSchedule(job.schedule()))
          .forJob(cronJobDetail)
          .build();

      jobFactory.setDependencies(job);

      try {
        scheduler.scheduleJob(cronJobDetail, cronTrigger);
      } catch (SchedulerException e) {
        throw new RuntimeException(e);
      }
    });
  }

  public void start() throws SchedulerException {
    scheduler.start();
  }

}
