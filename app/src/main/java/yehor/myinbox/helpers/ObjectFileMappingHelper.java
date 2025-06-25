package yehor.myinbox.helpers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.type.CollectionType;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
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
    Path path = Paths.get(fileName);
    createDirIfMissing(path);

    try (BufferedWriter writer = Files.newBufferedWriter(path)) {
      MAPPER.writeValue(writer, objects);
    } catch (IOException e) {
      String error = "Unable to write objects to file: " + fileName + " - " + e.getMessage();
      throw new RuntimeException(error);
    }
  }

  public static <T> Collection<T> readObjectsFromFile(String fileName, Class<T> klass) {
    Path path = Paths.get(fileName);
    if (!Files.exists(path)) {
      System.out.println("File not found: " + fileName + ". Returning empty collection.");
      return Collections.emptyList();
    }

    CollectionType type = MAPPER.getTypeFactory().constructCollectionType(List.class, klass);
    try (BufferedReader reader = Files.newBufferedReader(path)) {
      return MAPPER.readValue(reader, type);
    } catch (IOException e) {
      String error = "Unable to read objects from file: " + fileName;
      throw new RuntimeException(error, e);
    }
  }

  private static void createDirIfMissing(Path dir) {
    Path parentDir = dir.getParent();
    if (parentDir != null) {
      try {
        Files.createDirectories(parentDir);
      } catch (IOException e) {
        String error = "Unable to create a directory: " + parentDir;
        throw new RuntimeException(error, e);
      }
    }
  }
}