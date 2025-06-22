package yehor.myinbox.jobs.kinonowe;

import static yehor.myinbox.jobs.kinonowe.KinoNoweItem.MAIN_ELEMENT_SELECTOR;

import java.util.Collection;
import java.util.function.Function;
import java.util.stream.Collectors;
import org.jsoup.Jsoup;
import yehor.myinbox.helpers.HttpClientHelper;
import yehor.myinbox.helpers.ObjectFileMappingHelper;
import yehor.myinbox.jobs.ReportingCondition;
import yehor.myinbox.jobs.ReportingTask;
import yehor.myinbox.translation.ExternalTranslatorService;

public class KinoNoweTask implements ReportingTask {

  private static final String PREVIOUS_ITEMS_FILE = "data/kino_nowe_previous_items.json";
  private static final String BASE_URL = "https://www.kinonh.pl";
  private static final String MOVIE_LIST_URL = BASE_URL.concat("/list.s?typ=ENGLISH_FRIENDLY");
  private static final String RESPONSE_FORMAT =
      """
      🎬🟣 %d new movies found in 'Kino Nowe Horyzonty':

      %s


      See all available at the <a href="%s">site</a>.
      """;

  private final ExternalTranslatorService translatorService;

  private ReportingCondition reportingCondition = () -> false;

  public KinoNoweTask(ExternalTranslatorService translatorService) {
    this.translatorService = translatorService;
  }

  @Override
  public ReportingCondition reportingCondition() {
    return reportingCondition;
  }

  @Override
  public String run() {
    String response = HttpClientHelper.doGet(MOVIE_LIST_URL);

    Collection<KinoNoweItem> items = Jsoup.parse(response)
        .select(MAIN_ELEMENT_SELECTOR).stream()
        .map(KinoNoweItem::fromElement)
        .filter(item -> !item.hasSkipLabel() && item.isScreeningAvailable())
        .collect(Collectors.toMap(KinoNoweItem::getTitle, Function.identity(), KinoNoweItem::merge))
        .values();

    Collection<KinoNoweItem> newItems = excludePreviousItems(items);
    reportingCondition = () -> !newItems.isEmpty();

    Collection<KinoNoweItem> translatedItems = newItems.stream()
        .map(item -> item.replaceTitle(translatorService::translate))
        .toList();

    logTranslationBilling();
    return buildResponse(translatedItems);
  }

  private void logTranslationBilling() {
    if (reportingCondition.isMet()) {
      String billingInfo = translatorService.billingInfo();
      System.out.println(billingInfo);
    }
  }

  private Collection<KinoNoweItem> excludePreviousItems(Collection<KinoNoweItem> items) {
    final Collection<KinoNoweItem> previousItems =
        ObjectFileMappingHelper.readObjectsFromFile(PREVIOUS_ITEMS_FILE, KinoNoweItem.class);

    Collection<KinoNoweItem> newItems = items.stream()
        .filter(item -> !previousItems.contains(item))
        .toList();

    ObjectFileMappingHelper.writeObjectsToFile(items.stream().toList(), PREVIOUS_ITEMS_FILE);

    return newItems;
  }

  private String buildResponse(Collection<KinoNoweItem> items) {
    String moviesResponse = items.stream()
        .map(item -> item.buildDisplayString(BASE_URL))
        .reduce((a, b) -> a + "\n\n" + b)
        .orElse("---");
    return RESPONSE_FORMAT.formatted(items.size(), moviesResponse, MOVIE_LIST_URL);
  }
}
