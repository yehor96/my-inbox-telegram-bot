package yehor.myinbox.jobs;

import org.quartz.Job;
import org.quartz.JobExecutionContext;

public abstract class ReportingJob implements Job {

  @Override
  public void execute(JobExecutionContext context) {
    String result;
    try {
      result = task().run();
    } catch (Exception e) {
      String error = String.format("Error executing task for %s. %s",
          task().getClass().getSimpleName(), e.getMessage());
      System.out.println(error);
      reporter().sendFailure(error);
      return;
    }
    handleSuccessfulReporting(result);
  }

  private void handleSuccessfulReporting(String result) {
    try {
      if (task().reportingCondition().isMet()) {
        System.out.printf("Sending report for %s\n", task().getClass().getSimpleName());
        reporter().sendReport(result);
      } else {
        System.out.printf("No report sent for %s, condition not met.\n",
            task().getClass().getSimpleName());
      }
    } catch (Exception e) {
      System.out.println("Error reporting a task: " + e.getMessage());
    }
  }

  public abstract String schedule();

  public abstract ReportingTask task();

  public abstract Reporter reporter();
}