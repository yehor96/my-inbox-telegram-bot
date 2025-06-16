package yehor.myinbox.jobs.kinonowe;

import java.util.List;
import java.util.Objects;
import org.jsoup.nodes.Element;

public record KinoNoweItem (String title, String director, String link, List<String> labels) {

  private static final String MOVIE_TITLE_SELECTOR = "h4 a";
  private static final String MOVIE_DIRECTOR_SELECTOR = "h6 a.rezy";
  private static final String LABEL_ATTR = "data-etykieta";
  private static final String LABEL_SELECTOR = "span[%s]".formatted(LABEL_ATTR);

  public static KinoNoweItem fromElement(Element element) {
    var movieElement = element.select(MOVIE_TITLE_SELECTOR);

    var movieTitle = movieElement.text().trim();
    var movieDirector = element.select(MOVIE_DIRECTOR_SELECTOR).text().trim();
    var movieLink = "/".concat(movieElement.attr("href"));
    var labels = element.select(LABEL_SELECTOR).stream()
        .map(el -> el.attr(LABEL_ATTR))
        .toList();

    return new KinoNoweItem(movieTitle, movieDirector, movieLink, labels);
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

  public String title() {
    return title;
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
