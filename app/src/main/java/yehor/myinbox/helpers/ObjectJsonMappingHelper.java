package yehor.myinbox.helpers;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.type.CollectionType;
import java.util.Collection;
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
        String error = "JSON format not recognized. "
            + "Expected a JSON array or an object with a 'body' array.";
        throw new RuntimeException(error);
      }
      CollectionType type = MAPPER.getTypeFactory().constructCollectionType(List.class, klass);
      return MAPPER.treeToValue(dataNode, type);
    } catch (Exception e) {
      String error = "Unable to parse JSON to collection of objects: "
          + truncatedJson(json) + "... " + e.getMessage();
      throw new RuntimeException(error);
    }
  }

  public static <T> T readObjectFromJson(String json, Class<T> klass) {
    try {
      return MAPPER.readValue(json, klass);
    } catch (Exception e) {
      String error = "Unable to parse JSON to object: "
          + truncatedJson(json) + "... " + e.getMessage();
      throw new RuntimeException(error);
    }
  }

  public static String readStringFromJson(String json, String path) {
    try {
      JsonNode rootNode = MAPPER.readTree(json);
      JsonNode dataNode = rootNode.at(path);
      if (!dataNode.isMissingNode() && dataNode.isObject()) {
        return dataNode.toString();
      } else {
        String error = "Unable to find an object in a json at path: " + path;
        throw new RuntimeException(error);
      }
    } catch (Exception e) {
      String error = "Unable to parse JSON to String: "
          + truncatedJson(json) + "... " + e.getMessage();
      throw new RuntimeException(error);
    }
  }

  private static String truncatedJson(String json) {
    return json.length() > 100 ? json.substring(0, 100) : json;
  }
}
