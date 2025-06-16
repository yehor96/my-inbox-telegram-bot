package yehor.myinbox.translation;

import static java.util.Objects.requireNonNull;

import com.deepl.api.DeepLClient;
import com.deepl.api.DeepLException;
import com.deepl.api.TextResult;
import com.deepl.api.TextTranslationOptions;
import yehor.myinbox.helpers.EnvVarHelper;

public class DeepLTranslatorService implements ExternalTranslatorService {

  private final TextTranslationOptions textTranslationOptions = new TextTranslationOptions();
  private final String sourceLang;
  private final String targetLang;
  private final DeepLClient client;

  public DeepLTranslatorService(String sourceLang, String targetLang, String context) {
    this.sourceLang = sourceLang;
    this.targetLang = targetLang;
    textTranslationOptions.setContext(context);
    this.client = new DeepLClient(EnvVarHelper.get("DEEPL_API_KEY"));
  }

  @Override
  public String translate(String text, String sourceLang, String targetLang) {
    try {
      TextResult result = client
          .translateText(text, sourceLang, targetLang, textTranslationOptions);
      return result.getText();
    } catch (DeepLException | InterruptedException e) {
      System.out.println("Failed to translate text: " + e.getMessage());
      return text; // Return original text on failure to avoid disruption
    }
  }

  @Override
  public String translate(String text) {
    return translate(text, sourceLang, targetLang);
  }

  @Override
  public String billingInfo() {
    try {
      var details = client.getUsage().getCharacter();
      requireNonNull(details);
      return "DeepL character usage: " + details.getCount() + "/" + details.getLimit();
    } catch (DeepLException | InterruptedException | NullPointerException e) {
      return "DeepL character usage details not available.";
    }
  }
}
