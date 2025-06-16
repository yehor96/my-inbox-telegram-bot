package yehor.myinbox.translation;

public interface ExternalTranslatorService {
  String translate(String text, String sourceLang, String targetLang);
  String translate(String text);
  String billingInfo();
}
