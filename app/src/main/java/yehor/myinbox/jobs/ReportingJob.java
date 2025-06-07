package yehor.myinbox.jobs;

import org.quartz.Job;
import org.quartz.JobExecutionContext;

public abstract class ReportingJob implements Job {

  @Override
  public void execute(JobExecutionContext context) {
    try {
      var result = task().run();
      if (task().condition().shouldReport()) {
        reporter().sendReport(result);
      }
    } catch (Exception e) {
      System.out.println("Error executing ReportingJob: " + e.getMessage());
    }
  }

  public abstract String schedule();

  public abstract ReportingTask task();

  public abstract Reporter reporter();
}