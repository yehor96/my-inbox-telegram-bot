package yehor.myinbox.jobs;

import org.quartz.Job;
import org.quartz.JobExecutionContext;

public abstract class ReportingJob implements Job {

  @Override
  public void execute(JobExecutionContext context) {
    try {
      String result = task().run();
      if (task().condition().shouldReport()) {
        reporter().sendReport(result);
      } else {
        System.out.printf("No report sent for %s, condition not met.\n",
            task().getClass().getSimpleName());
      }
    } catch (Exception e) {
      System.out.println("Error executing ReportingJob: " + e.getMessage());
    }
  }

  public abstract String schedule();

  public abstract ReportingTask task();

  public abstract Reporter reporter();
}