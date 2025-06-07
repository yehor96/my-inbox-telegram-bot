package yehor.myinbox.http;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class HttpClientHelper {

  public static String doGet(String url) {
    try (HttpClient client = HttpClient.newHttpClient()) {
      HttpRequest request = HttpRequest.newBuilder()
          .uri(URI.create(url))
          .GET()
          .build();
      HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

      if (response.statusCode() != 200) {
        throw new RuntimeException("Failed to fetch data: " + response.statusCode());
      }

      return response.body();

    } catch (IOException | InterruptedException e) {
      throw new RuntimeException(e);
    }
  }
}
