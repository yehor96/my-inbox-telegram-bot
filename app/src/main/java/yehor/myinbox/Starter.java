package yehor.myinbox;

import java.util.List;
import org.quartz.SchedulerException;
import yehor.myinbox.jobs.JobService;
import yehor.myinbox.jobs.Reporter;
import yehor.myinbox.jobs.cinemacity.CinemaCityJob;
import yehor.myinbox.jobs.cinemacity.CinemaCityTask;
import yehor.myinbox.jobs.kinonowe.KinoNoweJob;
import yehor.myinbox.jobs.kinonowe.KinoNoweTask;
import yehor.myinbox.telegram.BotService;
import yehor.myinbox.translation.DeepLTranslatorService;
import yehor.myinbox.translation.ExternalTranslatorService;

public class Starter {

  public static void main(String[] args) throws SchedulerException {
    Reporter botService = telegramBotService();
    ExternalTranslatorService translatorService = translatorService();

    KinoNoweJob kinoNoweJob = new KinoNoweJob(new KinoNoweTask(translatorService), botService);
    CinemaCityJob cinemaCityJob = new CinemaCityJob(new CinemaCityTask(), botService);

    JobService jobService = new JobService(List.of(
        kinoNoweJob,
        cinemaCityJob
    ));
    jobService.start();
  }

  private static Reporter telegramBotService() {
    return new BotService();
  }

  private static ExternalTranslatorService translatorService() {
    return new DeepLTranslatorService(
        "pl",
        "en-US",
        "Movie title"
    );
  }
}