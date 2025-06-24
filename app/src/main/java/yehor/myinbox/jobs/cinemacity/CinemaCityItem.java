package yehor.myinbox.jobs.cinemacity;

import com.fasterxml.jackson.annotation.JsonAlias;
import java.util.List;
import java.util.Objects;

public class CinemaCityItem {

  @JsonAlias({"filmName", "name"})
  private String title;

  @JsonAlias({"filmLink"})
  private String link;

  @JsonAlias({"filmId", "id"})
  private String id;

  @JsonAlias({"directors"})
  private String director;

  @JsonAlias({"dubbedLanguage"})
  private List<CinemaCityLanguage> dubbingLanguages;

  @JsonAlias({"originalLanguage"})
  private List<CinemaCityLanguage> originalLanguages;

  public CinemaCityItem() {
  }

  public CinemaCityItem(
      String title, String link, String id, String director,
      List<CinemaCityLanguage> dubbingLanguages,
      List<CinemaCityLanguage> originalLanguages) {
    this.title = title;
    this.link = link;
    this.id = id;
    this.director = director;
    this.dubbingLanguages = dubbingLanguages;
    this.originalLanguages = originalLanguages;
  }

  public String buildDisplayString(String cinemaUrlSuffix) {
    String linkForDisplay = link.concat(cinemaUrlSuffix);
    return "- %s, dir. %s (<a href=\"%s\">link</a>)".formatted(
        titleForDisplay(), director, linkForDisplay);
  }

  public String titleForDisplay() {
    if (Objects.isNull(dubbingLanguages) || dubbingLanguages.isEmpty()) {
      return title;
    }
    return dubbingLanguages.stream()
        .anyMatch(language -> language == CinemaCityLanguage.UKRAINIAN)
        ? "\uD83C\uDDFA\uD83C\uDDE6 %s".formatted(title)
        : title;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) return true;
    if (obj instanceof CinemaCityItem other) {
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

  public String getLink() {
    return link;
  }

  public void setLink(String link) {
    this.link = link;
  }

  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public String getDirector() {
    return director;
  }

  public void setDirector(String director) {
    this.director = director;
  }

  public List<CinemaCityLanguage> getDubbingLanguages() {
    return dubbingLanguages;
  }

  public void setDubbingLanguages(List<CinemaCityLanguage> dubbingLanguages) {
    this.dubbingLanguages = dubbingLanguages;
  }

  public List<CinemaCityLanguage> getOriginalLanguages() {
    return originalLanguages;
  }

  public void setOriginalLanguages(List<CinemaCityLanguage> originalLanguages) {
    this.originalLanguages = originalLanguages;
  }
}
