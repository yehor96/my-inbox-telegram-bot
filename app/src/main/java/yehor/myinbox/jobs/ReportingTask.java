package yehor.myinbox.jobs;

public interface ReportingTask {

  ReportingCondition reportingCondition();

  String run();
}
