package yehor.myinbox.jobs.cinemacity;

import com.fasterxml.jackson.annotation.JsonAlias;

public class CinemaCityItem {

  @JsonAlias({"filmName"})
  private String title;

  @JsonAlias({"filmLink"})
  private String link;

  public CinemaCityItem() {
  }

  public CinemaCityItem(String title, String link) {
    this.title = title;
    this.link = link;
  }

  public String buildDisplayString() {
    return "- %s (<a href=\"%s\">link</a>)".formatted(title, link);
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
}
