package ch.epfl.favo.common;

import java.util.Random;

public final class TestUtil {

  public static String generateRandomString(int targetStringLength) {
    String title = "sample_favor";
    int leftLimit = 97; // letter 'a'
    int rightLimit = 122; // letter 'z'
    String generatedString =
        new Random()
            .ints(leftLimit, rightLimit + 1)
            .limit(targetStringLength)
            .collect(StringBuilder::new, StringBuilder::appendCodePoint, StringBuilder::append)
            .toString();
    return generatedString;
  }
}
