package yehor.myinbox.jobs.cinemacity;

import com.fasterxml.jackson.annotation.JsonCreator;
import java.util.Arrays;
import java.util.Set;

public enum CinemaCityLanguage {

  ENGLISH("original-lang-en", "dubbed-lang-en"),
  POLISH("original-lang-pl", "dubbed-lang-pl"),
  UKRAINIAN("original-lang-uk", "dubbed-lang-uk"),
  RUSSIAN("original-lang-ru", "dubbed-lang-ru"),
  UNKNOWN;

  public static final Set<CinemaCityLanguage> LANGUAGES_TO_INCLUDE = Set.of(
      ENGLISH, UKRAINIAN, RUSSIAN
  );

  private final Set<String> acceptedCodes;

  CinemaCityLanguage(String... acceptedCodes) {
    this.acceptedCodes = Set.of(acceptedCodes);
  }

  @JsonCreator
  public static CinemaCityLanguage fromCode(String code) {
    if (code == null || code.trim().isEmpty()) {
      return UNKNOWN;
    }

    return Arrays.stream(CinemaCityLanguage.values())
        .filter(lang -> lang.acceptedCodes.contains(code.toLowerCase()))
        .findFirst()
        .orElse(UNKNOWN);
  }
}
