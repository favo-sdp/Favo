package ch.epfl.favo;

import org.junit.Test;
import org.junit.function.ThrowingRunnable;

import java.util.Random;

import ch.epfl.favo.common.NotImplementedException;
import ch.epfl.favo.favor.FavorUtil;
import ch.epfl.favo.models.UserUtil;

import static org.junit.Assert.assertThrows;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class BackEndUnitTests {
    @Test
    public void userNameIsValid() {
        String username = "as;dfjlasdfkja;skldfm";
        String pw = "valid_pw";
        assertThrows(NotImplementedException.class,
                new ThrowingRunnable() {
                    @Override
                    public void run() throws Throwable {
                        UserUtil.getSingleInstance().createAccount(username, pw);
                    }
                });
    }

    @Test
    public void userShouldNotLoginWithInvalidPassword() {
        String username = "valid_user";
        String pw = generateRandomString(10);
        assertThrows(NotImplementedException.class,
                new ThrowingRunnable() {
                    @Override
                    public void run() throws Throwable {
                        UserUtil.getSingleInstance().logInAccount(username, pw);
                    }
                });
    }


    @Test
    public void favorIsNotLongerThan300Characters() {
        String title = "Sample Favor";
        String description = generateRandomString(305);
        String location = "valid location"; //replace by valid location
        assertThrows(NotImplementedException.class,
                new ThrowingRunnable() {
                    @Override
                    public void run() throws Throwable {
                        FavorUtil.getSingleInstance().postFavor(title, description, location);
                    }
                });
    }

    @Test
    public void userCanLogOutOnlyIfLoggedIn() {

        assertThrows(NotImplementedException.class,
                new ThrowingRunnable() {
                    @Override
                    public void run() throws Throwable {
                        UserUtil.getSingleInstance().logOutAccount();
                    }
                });
    }

    @Test
    public void userCanDeleteAccountOnlyIfAccountExists() {

        assertThrows(NotImplementedException.class,
                new ThrowingRunnable() {
                    @Override
                    public void run() throws Throwable {
                        UserUtil.getSingleInstance().deleteAccount();
                    }
                });
    }

    public String generateRandomString(int targetStringLength) {
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