package ch.epfl.favo;

import java.util.Random;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class BackEndUnitTests {

  public String generateRandomString(int targetStringLength) {
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
