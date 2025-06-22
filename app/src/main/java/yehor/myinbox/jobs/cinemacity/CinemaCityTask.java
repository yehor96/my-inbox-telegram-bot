package yehor.myinbox.jobs.cinemacity;

import java.util.Collection;
import yehor.myinbox.helpers.HttpClientHelper;
import yehor.myinbox.helpers.ObjectFileMappingHelper;
import yehor.myinbox.helpers.ObjectJsonMappingHelper;
import yehor.myinbox.jobs.ReportingCondition;
import yehor.myinbox.jobs.ReportingTask;

public class CinemaCityTask implements ReportingTask {

  private static final String PREVIOUS_ITEMS_FILE = "data/cinema_city_previous_items.json";
  private static final String WROCLAVIA_ID = "1097";
  private static final String MOVIE_LIST = ("https://www.cinema-city.pl/pl/data-api-service/v1/"
      + "10103/trailers/byCinemaId/%s?attr=&lang=en_GB").formatted(WROCLAVIA_ID);
  private static final String BASE_URL = "https://www.cinema-city.pl/kina/wroclavia/%s?lang=en_GB"
      .formatted(WROCLAVIA_ID);
  private static final String RESPONSE_FORMAT =
      """
      🎬🟠 %d new movies found in 'Cinema City Wroclavia':

      %s


      See all available at the <a href="%s">site</a>.
      """;

  private ReportingCondition reportingCondition = () -> false;

  @Override
  public ReportingCondition reportingCondition() {
    return reportingCondition;
  }

  @Override
  public String run() {
    String response = HttpClientHelper.doGet(MOVIE_LIST);

    Collection<CinemaCityItem> items = ObjectJsonMappingHelper.readObjectsFromJson(
        response, CinemaCityItem.class);

    Collection<CinemaCityItem> newItems = excludePreviousItems(items);
    reportingCondition = () -> !newItems.isEmpty();

    String moviesResponse = newItems.stream()
        .map(CinemaCityItem::buildDisplayString)
        .reduce((a, b) -> a + "\n\n" + b)
        .orElse("---");
    return RESPONSE_FORMAT.formatted(items.size(), moviesResponse, BASE_URL);
  }

  private Collection<CinemaCityItem> excludePreviousItems(Collection<CinemaCityItem> items) {
    final Collection<CinemaCityItem> previousItems =
        ObjectFileMappingHelper.readObjectsFromFile(PREVIOUS_ITEMS_FILE, CinemaCityItem.class);

    Collection<CinemaCityItem> newItems = items.stream()
        .filter(item -> !previousItems.contains(item))
        .toList();

    ObjectFileMappingHelper.writeObjectsToFile(items.stream().toList(), PREVIOUS_ITEMS_FILE);

    return newItems;
  }
}
