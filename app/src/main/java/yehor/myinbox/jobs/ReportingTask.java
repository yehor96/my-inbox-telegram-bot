package yehor.myinbox.jobs;

public interface ReportingTask {

  ReportingCondition condition();

  String run();
}
