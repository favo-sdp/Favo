package ch.epfl.favo.user;

import org.junit.Test;

import java.util.Random;

import ch.epfl.favo.favor.FavorUtil;
import ch.epfl.favo.user.UserUtil;
import ch.epfl.favo.TestUtil;
import ch.epfl.favo.common.NotImplementedException;


import static org.junit.Assert.*;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class UserUnitTests {
    @Test
    public void userNameIsValid(){
        String username = "as;dfjlasdfkja;skldfm";
        String pw = "valid_pw";
        assertThrows(NotImplementedException.class,
                ()->{UserUtil.getSingleInstance().createAccount(username,pw);});
    }
    @Test
    public void userShouldNotLoginWithInvalidPassword(){
        String username = "valid_user";
        String pw = TestUtil.generateRandomString(10);
        assertThrows(NotImplementedException.class,
                ()->{UserUtil.getSingleInstance().logInAccount(username,pw);});
    }
    @Test
    public void userCanLogOutOnlyIfLoggedIn(){

        assertThrows(NotImplementedException.class,
                ()->{UserUtil.getSingleInstance().logOutAccount();});
    }
    @Test
    public void userCanDeleteAccountOnlyIfAccountExists(){

        assertThrows(NotImplementedException.class,
                ()->{UserUtil.getSingleInstance().deleteAccount();});
    }
}