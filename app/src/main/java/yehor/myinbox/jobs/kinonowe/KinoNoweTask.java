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
  private static final String RESPONSE_FORMAT =
      "Current movies in 'Kino Nowe Horyzonty':\n\n%s\n\nTotal: %d movies found.";

  private static final String MAIN_ELEMENTS_SELECTOR = ".film-box .opis";

  private static final List<String> SKIP_LABELS = List.of(
      "dubbing",
      "transmisja na żywo",
      "retransmisja",
      "lektor na żywo"
  );

  private ReportingCondition reportingCondition;

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

    String movieList = items.stream()
        .map(item -> item.buildString(BASE_URL))
        .reduce((a, b) -> a + "\n" + b)
        .orElse("---");

    reportingCondition = () -> !items.isEmpty();
    return RESPONSE_FORMAT.formatted(movieList, items.size());
  }
}
