package ch.epfl.favo.favor;

import android.location.Location;

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
    @Test
    public void favorGettersReturnCorrectValues(){

        String title = "Flat tire";
        String description = "Tiire popped while turning left on Avenue Rhodanie";
        String requesterId = "2362489";
        Location location = new Location("Dummy provider");
        int statusId = 0;

        Favor favor = new Favor(title, description, requesterId, location, statusId);

        assertEquals(title, favor.getTitle());
        assertEquals(description, favor.getDescription());
        assertEquals(requesterId, favor.getRequesterId());
        assertEquals(location, favor.getLocation());
        assertEquals(statusId, favor.getStatusId());
    }
    @Test
    public void favorSettersCorrectlyUpdateValues(){

        Favor favor = new Favor();
        int statusId = 3;
        Location location = new Location("Dummy provider 2");
        String accepterId = "2364652";
        favor.setStatusId(3);
        favor.setLocation(location);
        favor.setAccepterID(accepterId);

        assertEquals(location, favor.getLocation());
        assertEquals(statusId, favor.getStatusId());
        assertEquals(accepterId,favor.getAccepterID());
    }

}