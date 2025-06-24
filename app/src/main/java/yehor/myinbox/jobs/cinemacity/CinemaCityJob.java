package yehor.myinbox.jobs.cinemacity;

import yehor.myinbox.helpers.EnvVarHelper;
import yehor.myinbox.jobs.Reporter;
import yehor.myinbox.jobs.ReportingJob;
import yehor.myinbox.jobs.ReportingTask;

public class CinemaCityJob extends ReportingJob {

  private final ReportingTask task;
  private final Reporter reporter;

  public CinemaCityJob(ReportingTask task, Reporter reporter) {
    this.task = task;
    this.reporter = reporter;
  }

  @Override
  public String schedule() {
    return EnvVarHelper.IS_LOCAL_ENV ?
        "0 * * * * ?" : // Every minute
        "0 0 19 * * ?"; // Every day at 7 pm
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
