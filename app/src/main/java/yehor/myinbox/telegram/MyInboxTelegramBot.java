package yehor.myinbox.telegram;

import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.ParseMode;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import yehor.myinbox.helpers.EnvVarHelper;

public class MyInboxTelegramBot extends TelegramLongPollingBot {

  private final String MY_INBOX_TELEGRAM_BOT_CHAT_ID;
  private final String MY_INBOX_TELEGRAM_BOT_TOKEN;

  public MyInboxTelegramBot() {
    MY_INBOX_TELEGRAM_BOT_CHAT_ID = EnvVarHelper.get("MY_INBOX_TELEGRAM_BOT_CHAT_ID");
    MY_INBOX_TELEGRAM_BOT_TOKEN = EnvVarHelper.get("MY_INBOX_TELEGRAM_BOT_TOKEN");
  }

  public void sendMessage(String text) {
    SendMessage message = new SendMessage();
    message.setChatId(MY_INBOX_TELEGRAM_BOT_CHAT_ID);
    message.setText(text);
    message.setParseMode(ParseMode.HTML);
    message.disableWebPagePreview();
    try {
      execute(message);
    } catch (TelegramApiException e) {
      System.out.println("Failed to send message to Telegram bot: " + e.getMessage());
    }
  }

  @Override
  public void onUpdateReceived(Update update) {
    // ignore
  }

  @Override
  public String getBotUsername() {
    return "inbox_my_bot";
  }

  @Override
  public String getBotToken() {
    return MY_INBOX_TELEGRAM_BOT_TOKEN;
  }
}
