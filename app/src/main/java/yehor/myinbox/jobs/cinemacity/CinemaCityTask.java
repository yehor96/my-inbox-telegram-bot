package yehor.myinbox.jobs.cinemacity;

import static yehor.myinbox.jobs.cinemacity.CinemaCityLanguage.LANGUAGES_TO_INCLUDE;

import java.util.Collection;
import java.util.List;
import yehor.myinbox.helpers.HttpClientHelper;
import yehor.myinbox.helpers.ObjectFileMappingHelper;
import yehor.myinbox.helpers.ObjectJsonMappingHelper;
import yehor.myinbox.jobs.ReportingCondition;
import yehor.myinbox.jobs.ReportingTask;

public class CinemaCityTask implements ReportingTask {

  private static final String PREVIOUS_ITEMS_FILE = "data/cinema_city_previous_items.json";
  private static final String WROCLAVIA_ID = "1097";
  private static final String CINEMA_CITY_SERVICE_API = "https://www.cinema-city.pl/pl/data-api-service/v1/10103";
  private static final String WROCLAW_CINEMA_URL_SUFFIX = "#/buy-tickets-by-film?in-cinema=wroclaw";
  private static final String MOVIE_LIST = CINEMA_CITY_SERVICE_API.concat("/trailers/byCinemaId/%s?attr=&lang=en_GB").formatted(WROCLAVIA_ID);
  private static final String MOVIE_BY_ID_URL = CINEMA_CITY_SERVICE_API.concat("/films/byDistributorCode/%s?lang=en_GB");
  private static final String WEBSITE_URL = "https://www.cinema-city.pl/kina/wroclavia/%s?lang=en_GB".formatted(WROCLAVIA_ID);
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

    List<CinemaCityItem> newItems = excludePreviousItems(items).stream()
        .map(this::buildMovieItem)
        .filter(this::filterByLanguage)
        .toList();

    reportingCondition = () -> !newItems.isEmpty();

    String moviesResponse = newItems.stream()
        .map(item -> item.buildDisplayString(WROCLAW_CINEMA_URL_SUFFIX))
        .reduce((a, b) -> a + "\n\n" + b)
        .orElse("---");
    return RESPONSE_FORMAT.formatted(newItems.size(), moviesResponse, WEBSITE_URL);
  }

  private boolean filterByLanguage(CinemaCityItem cinemaCityItem) {
    return cinemaCityItem.getDubbingLanguages().stream().anyMatch(LANGUAGES_TO_INCLUDE::contains)
        || cinemaCityItem.getOriginalLanguages().stream().anyMatch(LANGUAGES_TO_INCLUDE::contains);
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

  private CinemaCityItem buildMovieItem(CinemaCityItem initialItem) {
    String url = MOVIE_BY_ID_URL.formatted(initialItem.getId());
    String response = HttpClientHelper.doGet(url);
    String movieDetails = ObjectJsonMappingHelper
        .readStringFromJson(response, "/body/filmDetails");
    return ObjectJsonMappingHelper.readObjectFromJson(movieDetails, CinemaCityItem.class);
  }
}
