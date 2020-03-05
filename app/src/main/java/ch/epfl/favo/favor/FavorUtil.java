package ch.epfl.favo.favor;

import ch.epfl.favo.common.NotImplementedException;

public class FavorUtil {

    private static final FavorUtil SINGLE_INSTANCE = new FavorUtil();

    private FavorUtil() {}

    public static FavorUtil getSingleInstance() {
        return SINGLE_INSTANCE;
    }

    /** Allows user to post a favor with a title, description and location.
     * @param title Title of favor.
     * @param description String containing 300 char (max) description of text.
     * @param location Address or coordinates at which the favor is requested.
     TODO: post favor in DB linked to user
     */
    public void postFavor(String title,String description,String location){

        throw new NotImplementedException();
    }



}
