package ch.epfl.favo.favor;

import org.junit.Test;

import java.util.Random;

import ch.epfl.favo.favor.FavorUtil;
import ch.epfl.favo.user.UserUtil;
import ch.epfl.favo.common.NotImplementedException;

import static org.junit.Assert.*;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class FavorUnitTests {
    @Test
    public void favorIsNotLongerThan300Characters(){
        String title = "Sample Favor";
        String description = generateRandomString(305);
        String location = "valid location"; //replace by valid location
        assertThrows(NotImplementedException.class,
                ()->{FavorUtil.getSingleInstance().postFavor(title,description,location);});
    }
    public String generateRandomString(int targetStringLength){
        String title = "sample_favor";
        int leftLimit = 97; // letter 'a'
        int rightLimit = 122; // letter 'z'
        String generatedString = new Random().ints(leftLimit, rightLimit + 1)
                .limit(targetStringLength)
                .collect(StringBuilder::new, StringBuilder::appendCodePoint, StringBuilder::append)
                .toString();
        return generatedString;
    }
}