package yehor.myinbox;

import java.util.List;
import org.quartz.SchedulerException;
import yehor.myinbox.jobs.JobService;
import yehor.myinbox.jobs.kinonowe.KinoNoweJob;
import yehor.myinbox.jobs.kinonowe.KinoNoweTask;
import yehor.myinbox.telegram.BotService;
import yehor.myinbox.translation.DeepLTranslatorService;
import yehor.myinbox.translation.ExternalTranslatorService;

public class Starter {

  public static void main(String[] args) throws SchedulerException {

    KinoNoweJob kinoNoweJob = new KinoNoweJob(
        new KinoNoweTask(translatorService()),
        new BotService()
    );

    JobService jobService = new JobService(List.of(kinoNoweJob));
    jobService.start();
  }

  private static ExternalTranslatorService translatorService() {
    return new DeepLTranslatorService(
        "pl",
        "en-US",
        "Movie title"
    );
  }
}