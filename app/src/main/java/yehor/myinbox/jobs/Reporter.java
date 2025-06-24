package yehor.myinbox.jobs;

public interface Reporter {
  void sendReport(String report);
  void sendFailure(String errorMessage);
}
