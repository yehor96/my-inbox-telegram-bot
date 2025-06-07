package yehor.myinbox;

import java.util.List;
import org.quartz.SchedulerException;
import yehor.myinbox.jobs.JobService;
import yehor.myinbox.jobs.kinonowe.KinoNoweJob;
import yehor.myinbox.jobs.kinonowe.KinoNoweTask;
import yehor.myinbox.telegram.BotService;

public class Starter {

  public static void main(String[] args) throws SchedulerException {

    KinoNoweJob kinoNoweJob = new KinoNoweJob(
        new KinoNoweTask(),
        new BotService()
    );

    JobService jobService = new JobService(List.of(kinoNoweJob));
    jobService.start();
  }
}