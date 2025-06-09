package yehor.myinbox.helpers;

import io.github.cdimascio.dotenv.Dotenv;

public class EnvVarHelper {

  private static Dotenv DOTENV = null;

  static {
    try {
      DOTENV = Dotenv.load();
    } catch (io.github.cdimascio.dotenv.DotenvException e) {
      System.out.println("No .env file present. Expecting environment variables");
    }
  }

  public static String get(String key) {
    if (DOTENV != null) {
      return DOTENV.get(key);
    } else {
      return System.getenv(key);
    }
  }
}
