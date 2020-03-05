package ch.epfl.favo.favor;

import org.junit.Test;

import java.util.Random;


import ch.epfl.favo.TestUtil;
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
        String description = TestUtil.generateRandomString(305);
        String location = "valid location"; //replace by valid location
        assertThrows(NotImplementedException.class,
                ()->{FavorUtil.getSingleInstance().postFavor(title,description,location);});
    }
    @Test
    public void favorCanRetrieveDetailsFromDatabase(){

        String favorId = "2negoinr3";
        assertThrows(NotImplementedException.class,
                ()->{FavorDatabase.getSingleInstance().getFavorDetailsFromDB(favorId);});
    }
    @Test
    public void favorCanRemoveDetailsFromDatabase(){

        String favorId = "2negoinr3";
        assertThrows(NotImplementedException.class,
                ()->{FavorDatabase.getSingleInstance().removeFavorFromDB(favorId);});
    }
    
}