package yehor.myinbox.helpers;

import io.github.cdimascio.dotenv.Dotenv;
import io.github.cdimascio.dotenv.DotenvException;

public class EnvVarHelper {

  public static boolean IS_LOCAL_ENV;
  private static Dotenv DOTENV = null;

  static {
    try {
      DOTENV = Dotenv.load();
      IS_LOCAL_ENV = true;
    } catch (DotenvException e) {
      System.out.println("No .env file present. Expecting environment variables");
      IS_LOCAL_ENV = false;
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
