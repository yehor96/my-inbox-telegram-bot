package yehor.myinbox.telegram;

import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;
import yehor.myinbox.jobs.Reporter;

public class BotService implements Reporter {

  private final MyInboxTelegramBot myInboxTelegramBot;

  public BotService() {
    myInboxTelegramBot = new MyInboxTelegramBot();
    try {
      TelegramBotsApi botsApi = new TelegramBotsApi(DefaultBotSession.class);
      botsApi.registerBot(myInboxTelegramBot);
    } catch (Exception e) {
      throw new RuntimeException("Failed to start the bot, not able to continue.", e);
    }
  }

  @Override
  public void sendReport(String report) {
    myInboxTelegramBot.sendMessage(report);
  }

  @Override
  public void sendFailure(String errorMessage) {
    myInboxTelegramBot.sendMessage("\uD83D\uDFE5 ".concat(errorMessage));
  }
}
