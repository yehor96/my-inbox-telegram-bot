package yehor.myinbox.jobs.kinonowe;

import static java.util.stream.Collectors.toCollection;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import org.jsoup.Jsoup;
import yehor.myinbox.http.HttpClientHelper;
import yehor.myinbox.jobs.ReportingCondition;
import yehor.myinbox.jobs.ReportingTask;

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

  private Set<KinoNoweItem> previousItems = new LinkedHashSet<>();
  private ReportingCondition reportingCondition = () -> false;

  @Override
  public ReportingCondition condition() {
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
    return buildResponse(newItems);
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
