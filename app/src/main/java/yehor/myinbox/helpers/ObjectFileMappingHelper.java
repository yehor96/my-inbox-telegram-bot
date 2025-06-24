package yehor.myinbox.helpers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.type.CollectionType;
import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

public class ObjectFileMappingHelper {

  private static final ObjectMapper MAPPER;

  static {
    MAPPER = new ObjectMapper();
    MAPPER.enable(SerializationFeature.INDENT_OUTPUT);
  }

  public static <T> void writeObjectsToFile(List<T> objects, String fileName) {
    File file = new File(fileName);
    try {
      MAPPER.writeValue(file, objects);
    } catch (IOException e) {
      String error = "Unable to write objects to file: " + fileName + " - " + e.getMessage();
      throw new RuntimeException(error);
    }
  }

  public static <T> Collection<T> readObjectsFromFile(String fileName, Class<T> klass) {
    File file = new File(fileName);
    if (!file.exists()) {
      System.out.println("File not found: " + fileName + ". Returning empty list.");
      return Collections.emptyList();
    }

    CollectionType type = MAPPER.getTypeFactory().constructCollectionType(List.class, klass);
    try {
      return MAPPER.readValue(file, type);
    } catch (Exception e) {
      String error = "Unable to read objects from file: " + fileName + " - " + e.getMessage();
      throw new RuntimeException(error);
    }
  }
}