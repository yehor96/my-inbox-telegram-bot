package yehor.myinbox.helpers;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.type.CollectionType;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

public class ObjectJsonMappingHelper {

  private static final ObjectMapper MAPPER;

  static {
    MAPPER = new ObjectMapper();
    MAPPER.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    MAPPER.enable(SerializationFeature.INDENT_OUTPUT);
  }

  public static <T> Collection<T> readObjectsFromJson(String json, Class<T> klass) {
    try {
      JsonNode rootNode = MAPPER.readTree(json);
      JsonNode dataNode;

      if (rootNode.isArray()) {
        dataNode = rootNode;
      } else if (rootNode.isObject() && rootNode.has("body") && rootNode.get("body").isArray()) {
        dataNode = rootNode.get("body");
      } else {
        System.err.println("JSON format not recognized. "
            + "Expected a JSON array or an object with a 'body' array.");
        return Collections.emptyList();
      }
      CollectionType type = MAPPER.getTypeFactory().constructCollectionType(List.class, klass);
      return MAPPER.treeToValue(dataNode, type);
    } catch (Exception e) {
      String truncatedJson = json.length() > 100 ? json.substring(0, 100) : json;
      System.err.println("Unable to parse JSON: " + truncatedJson + "...");
      return Collections.emptyList();
    }
  }
}
