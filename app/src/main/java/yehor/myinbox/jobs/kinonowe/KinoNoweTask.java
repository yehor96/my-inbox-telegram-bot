package yehor.myinbox.jobs.kinonowe;

import static java.util.stream.Collectors.toCollection;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import org.jsoup.Jsoup;
import yehor.myinbox.helpers.HttpClientHelper;
import yehor.myinbox.jobs.ReportingCondition;
import yehor.myinbox.jobs.ReportingTask;
import yehor.myinbox.translation.ExternalTranslatorService;

public class KinoNoweTask implements ReportingTask {

  private static final String BASE_URL = "https://www.kinonh.pl";
  private static final String MOVIE_LIST_URL = BASE_URL.concat("/list.s?typ=ENGLISH_FRIENDLY");
  private static final String MAIN_ELEMENTS_SELECTOR = ".film-box .opis";
  private static final List<String> SKIP_LABELS = List.of(
      "dubbing",
      "transmisja na żywo",
      "retransmisja",
      "lektor na żywo"
  );
  private static final String RESPONSE_FORMAT =
      """
      🎬 %d new movies found in 'Kino Nowe Horyzonty':

      %s

      See all available at the <a href="%s">site</a>.
      """;

  private final ExternalTranslatorService translatorService;

  private Set<KinoNoweItem> previousItems = new LinkedHashSet<>();
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

    Set<KinoNoweItem> items = Jsoup.parse(response)
        .select(MAIN_ELEMENTS_SELECTOR).stream()
        .map(KinoNoweItem::fromElement)
        .filter(item -> item.labels().stream().noneMatch(SKIP_LABELS::contains))
        .collect(toCollection(LinkedHashSet::new));

    Set<KinoNoweItem> newItems = excludePreviousItems(items);
    reportingCondition = () -> !newItems.isEmpty();

    Set<KinoNoweItem> translatedItems = newItems.stream()
        .map(item -> new KinoNoweItem(
            translatorService.translate(item.title()),
            item.link(),
            item.labels()))
        .collect(toCollection(LinkedHashSet::new));

    logTranslationBilling();
    return buildResponse(translatedItems);
  }

  private void logTranslationBilling() {
    if (reportingCondition.isMet()) {
      String billingInfo = translatorService.billingInfo();
      System.out.println(billingInfo);
    }
  }

  private Set<KinoNoweItem> excludePreviousItems(Set<KinoNoweItem> items) {
    Set<KinoNoweItem> newItems = items.stream()
        .filter(item -> !previousItems.contains(item))
        .collect(toCollection(LinkedHashSet::new));

    previousItems = items;
    return newItems;
  }

  private String buildResponse(Set<KinoNoweItem> items) {
    String moviesResponse = items.stream()
        .map(item -> item.buildString(BASE_URL))
        .reduce((a, b) -> a + "\n" + b)
        .orElse("---");
    return RESPONSE_FORMAT.formatted(items.size(), moviesResponse, MOVIE_LIST_URL);
  }
}
