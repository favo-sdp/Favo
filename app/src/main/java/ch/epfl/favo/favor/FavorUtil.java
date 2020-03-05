package ch.epfl.favo.favor;

import ch.epfl.favo.common.NotImplementedException;
/*
This models the favor request.
*/
public class FavorUtil {
    /**Singleton pattern.
     TODO: Figure out singleton constructor
     */

    //Single class instance
    private static final FavorUtil SINGLE_INSTANCE = new FavorUtil();

    //Private Constructor
    private FavorUtil(){
        return;
    }

    //Single instance getter
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
