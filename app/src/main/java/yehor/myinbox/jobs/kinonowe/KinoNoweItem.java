package yehor.myinbox.jobs.kinonowe;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;
import org.jsoup.nodes.Element;

public class KinoNoweItem {

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

  private String title;
  private String director;
  private List<String> links;
  private boolean isScreeningAvailable;
  private List<String> labels;

  public KinoNoweItem() {
  }

  public KinoNoweItem(String title, String director, List<String> links,
      boolean isScreeningAvailable, List<String> labels) {
    this.title = title;
    this.director = director;
    this.links = links;
    this.isScreeningAvailable = isScreeningAvailable;
    this.labels = labels;
  }

  public static KinoNoweItem fromElement(Element element) {
    var movieElement = element.select(MOVIE_TITLE_SELECTOR);

    var movieTitle = movieElement.text().trim();
    var movieDirector = element.select(MOVIE_DIRECTOR_SELECTOR).text().trim();
    var movieLink = "/".concat(movieElement.attr("href"));
    var isScreeningAvailable = !element.select(SCREENING_SELECTOR).isEmpty();
    var labels = element.select(LABEL_SELECTOR).stream()
        .map(el -> el.attr(LABEL_ATTR))
        .toList();

    return new KinoNoweItem(
        movieTitle, movieDirector, List.of(movieLink), isScreeningAvailable, labels);
  }

  public String buildDisplayString(String baseLink) {
    return "- %s%s (%s)".formatted(title, directorForDisplay(), linksForDisplay(baseLink));
  }

  public KinoNoweItem replaceTitle(Function<String, String> titleReplacer) {
    title = titleReplacer.apply(title);
    return this;
  }

  public boolean hasSkipLabel() {
    return labels.stream().anyMatch(SKIP_LABELS::contains);
  }

  public KinoNoweItem merge(KinoNoweItem other) {
    List<String> mergedLinks = new ArrayList<>(this.links);
    mergedLinks.addAll(other.links);
    setLinks(mergedLinks);
    return this;
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

  public String getTitle() {
    return title;
  }

  public void setTitle(String title) {
    this.title = title;
  }

  public String getDirector() {
    return director;
  }

  public void setDirector(String director) {
    this.director = director;
  }

  public List<String> getLinks() {
    return links;
  }

  public void setLinks(List<String> links) {
    this.links = links;
  }

  public boolean isScreeningAvailable() {
    return isScreeningAvailable;
  }

  public void setScreeningAvailable(boolean screeningAvailable) {
    isScreeningAvailable = screeningAvailable;
  }

  public List<String> getLabels() {
    return labels;
  }

  public void setLabels(List<String> labels) {
    this.labels = labels;
  }

  private String directorForDisplay() {
    return (Objects.nonNull(director) && !director.isBlank())
        ? ", dir. %s".formatted(director)
        : "";
  }

  private String linksForDisplay(String baseLink) {
    return links.stream()
        .map(l -> "<a href=\"%s\">link</a>".formatted(baseLink.concat(l)))
        .reduce((a, b) -> a + ", " + b)
        .orElse("");
  }

}
