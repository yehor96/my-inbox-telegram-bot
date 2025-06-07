package yehor.myinbox.jobs.kinonowe;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import yehor.myinbox.http.HttpClientHelper;
import yehor.myinbox.jobs.ReportingCondition;
import yehor.myinbox.jobs.ReportingTask;

public class KinoNoweTask implements ReportingTask {

  private static final String URL = "https://www.kinonh.pl/list.s?typ=ENGLISH_FRIENDLY";
  private static final String RESPONSE_FORMAT =
      "Current movies in 'Kino Nowe Horyzonty':\n\n%s\n\nTotal: %d movies found.";

  private static final String MAIN_ELEMENT_SELECTOR = ".film-box .opis";
  private static final String TITLE_SELECTOR = "h4 a";
  private static final String LABEL_ATTR = "data-etykieta";
  private static final String LABEL_SELECTOR = "span[%s]".formatted(LABEL_ATTR);

  private static final List<String> SKIP_LABELS = List.of(
      "dubbing",
      "transmisja na żywo",
      "retransmisja",
      "lektor na żywo"
  );

  @Override
  public ReportingCondition condition() {
    return () -> true;
  }

  @Override
  public String run() {
    String response = HttpClientHelper.doGet(URL);

    Set<String> extractedValues = extractTitles(response);

    String movieList = extractedValues.stream()
        .reduce((a, b) -> a + "\n" + b)
        .orElse("No films found");

    return RESPONSE_FORMAT.formatted(movieList, extractedValues.size());
  }

  public static Set<String> extractTitles(String htmlString) {
    Document doc = Jsoup.parse(htmlString);

    Elements elements = doc.select(MAIN_ELEMENT_SELECTOR);

    Set<String> titles = new LinkedHashSet<>();
    for (Element element : elements) {
      var labels = element.select(LABEL_SELECTOR);
      if (labels.stream().anyMatch(label -> SKIP_LABELS.contains(label.attr(LABEL_ATTR)))) {
        continue;
      }
      titles.add(element.select(TITLE_SELECTOR).text().trim());
    }

    return titles;
  }

}
