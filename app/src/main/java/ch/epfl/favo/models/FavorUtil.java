package ch.epfl.favo.models;

import ch.epfl.favo.exceptions.NotImplementedException;
/*
This models the favor request.

 */
public class FavorUtil {
    /**
     * Singleton pattern.
     TODO: Figure out singleton constructor
     */
    private static final FavorUtil SINGLE_INSTANCE = new FavorUtil();
    private FavorUtil(){
        return;
    }

    public static FavorUtil getSingleInstance() {
        return SINGLE_INSTANCE;
    }

    /**
     * Allows user to post a favor with a title, description and location
     * @param title
     * @param description
     * @param location
     TODO: post favor in DB linked to user
     */
    public void postFavor(String title,String description,String location){

        throw new NotImplementedException();
    }



}
