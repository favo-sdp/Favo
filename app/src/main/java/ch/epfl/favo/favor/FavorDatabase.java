package ch.epfl.favo.favor;

import ch.epfl.favo.common.NotImplementedException;

public class FavorDatabase {

  private static final FavorDatabase SINGLE_INSTANCE = new FavorDatabase();

  private FavorDatabase() {}

  public static FavorDatabase getSingleInstance() {
    return SINGLE_INSTANCE;
  }

  public void getFavorDetailsFromDB(String favorId)
      throws IllegalArgumentException, NotImplementedException {
    /*
    TODO: Fetch favor data after implementing
        callback interface
     */
    throw new NotImplementedException();
  }

  public void removeFavorFromDB(String favorId)
      throws IllegalArgumentException, NotImplementedException {
    /*
    TODO: Delete favor data after implementing
        callback interface
     */
    throw new NotImplementedException();
  }
}
