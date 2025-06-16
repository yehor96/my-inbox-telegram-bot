package yehor.myinbox.jobs.kinonowe;

import java.util.List;
import java.util.Objects;
import java.util.function.Function;
import org.jsoup.nodes.Element;

public record KinoNoweItem (String title,
                            String director,
                            String link,
                            boolean isScreeningAvailable,
                            List<String> labels) {

  public static final String MAIN_ELEMENT_SELECTOR = ".film-box .opis";
  private static final String MOVIE_TITLE_SELECTOR = "h4 a";
  private static final String MOVIE_DIRECTOR_SELECTOR = "h6 a.rezy";
  private static final String LABEL_ATTR = "data-etykieta";
  private static final String LABEL_SELECTOR = "span[%s]".formatted(LABEL_ATTR);
  private static final String SCREENING_SELECTOR = "[title=\"kup / rezerwuj bilet\"]";
  private static final List<String> SKIP_LABELS = List.of(
      "dubbing",
      "transmisja na żywo",
      "retransmisja",
      "lektor na żywo"
  );

  public static KinoNoweItem fromElement(Element element) {
    var movieElement = element.select(MOVIE_TITLE_SELECTOR);

    var movieTitle = movieElement.text().trim();
    var movieDirector = element.select(MOVIE_DIRECTOR_SELECTOR).text().trim();
    var movieLink = "/".concat(movieElement.attr("href"));
    var isScreeningAvailable = !element.select(SCREENING_SELECTOR).isEmpty();
    var labels = element.select(LABEL_SELECTOR).stream()
        .map(el -> el.attr(LABEL_ATTR))
        .toList();

    return new KinoNoweItem(movieTitle, movieDirector, movieLink, isScreeningAvailable, labels);
  }

  public String buildString(String baseLink) {
    String directorToDisplay = (Objects.nonNull(director) && !director.isBlank())
        ? ", dir. %s".formatted(director)
        : "";
    return "- %s%s (<a href=\"%s\">link</a>)".formatted(
        title,
        directorToDisplay,
        baseLink.concat(link));
  }

  public KinoNoweItem replaceTitle(Function<String, String> titleReplacer) {
    return new KinoNoweItem(
        titleReplacer.apply(this.title),
        this.director,
        this.link,
        this.isScreeningAvailable,
        this.labels);
  }

  public boolean hasSkipLabel() {
    return labels.stream().anyMatch(SKIP_LABELS::contains);
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) return true;
    if (obj instanceof KinoNoweItem other) {
      return title.equals(other.title);
    }
    return false;
  }

  @Override
  public int hashCode() {
    return title.hashCode();
  }
}
