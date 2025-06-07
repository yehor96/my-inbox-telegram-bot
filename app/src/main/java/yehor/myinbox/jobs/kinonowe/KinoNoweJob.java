package yehor.myinbox.jobs.kinonowe;

import yehor.myinbox.jobs.Reporter;
import yehor.myinbox.jobs.ReportingJob;
import yehor.myinbox.jobs.ReportingTask;

public class KinoNoweJob extends ReportingJob {

  private final ReportingTask task;
  private final Reporter reporter;

  public KinoNoweJob(ReportingTask task, Reporter reporter) {
    this.task = task;
    this.reporter = reporter;
  }

  @Override
  public String schedule() {
    return "0 * * * * ?"; // Every 1 minute
  }

  @Override
  public ReportingTask task() {
    return task;
  }

  @Override
  public Reporter reporter() {
    return reporter;
  }
}
