package yehor.myinbox.jobs;

import java.util.HashMap;
import java.util.Map;
import org.quartz.Job;
import org.quartz.Scheduler;
import org.quartz.simpl.SimpleJobFactory;
import org.quartz.spi.TriggerFiredBundle;

public class JobFactory extends SimpleJobFactory {

  private final Map<Class<? extends ReportingJob>, ReportingTask> tasks = new HashMap<>();
  private final Map<Class<? extends ReportingJob>, Reporter> reporters = new HashMap<>();

  public void setDependencies(ReportingJob job) {
    var jobClass = job.getClass();
    tasks.put(jobClass, job.task());
    reporters.put(jobClass, job.reporter());
  }

  @Override
  public Job newJob(TriggerFiredBundle bundle, Scheduler scheduler) {
    try {
      var jobClass = bundle.getJobDetail().getJobClass();
      var constructor = jobClass.getDeclaredConstructor(ReportingTask.class, Reporter.class);

      return constructor.newInstance(tasks.get(jobClass), reporters.get(jobClass));
    } catch (Exception e) {
      System.out.println("Not able to create a job instance: " + e.getMessage());
      throw new RuntimeException("Failed to create job instance", e);
    }
  }
}
