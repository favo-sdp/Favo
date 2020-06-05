package ch.epfl.favo.favor;

import android.annotation.SuppressLint;
import android.location.Location;

import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.Query;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

import ch.epfl.favo.database.ICollectionWrapper;
import ch.epfl.favo.gps.FavoLocation;
import ch.epfl.favo.util.DependencyFactory;

/**
 * This models the favor fragment_favor_published_view.
 */
@SuppressLint("NewApi")
public class FavorUtil implements IFavorUtil {
  private static final FavorUtil SINGLE_INSTANCE = new FavorUtil();

  private static ICollectionWrapper<Favor> collection =
      DependencyFactory.getCurrentCollectionWrapper(
          DependencyFactory.getCurrentFavorCollection(), Favor.class);

  private FavorUtil() {}

  public void updateCollectionWrapper(ICollectionWrapper collectionWrapper) {
    collection = collectionWrapper;
  }

  public static FavorUtil getSingleInstance() {
    collection =
        DependencyFactory.getCurrentCollectionWrapper(
            DependencyFactory.getCurrentFavorCollection(), Favor.class);
    return SINGLE_INSTANCE;
  }

  /**
   * Allows user to post a favor with a title, description and location.
   *
   * @param favor A favor object.
   * @throws RuntimeException Unable to post to DB.
   */
  @Override
  public CompletableFuture<Void> requestFavor(Favor favor) throws RuntimeException {
    return collection.addDocument(favor);
  }

  @Override
  public CompletableFuture<Void> updateFavor(Favor favor) {
    return collection.updateDocument(favor.getId(), favor.toMap());
  }

  /**
   * @param favorId the id of the favor to retrieve from DB.
   * @return CompletableFuture<Favor>
   */
  @Override
  public CompletableFuture<Favor> retrieveFavor(String favorId) {
    return collection.getDocument(favorId);
  }

  @Override
  public CompletableFuture<Void> removeFavor(String favorId) {
    return collection.removeDocument(favorId);
  }

  @Override
  public Query getLongitudeBoundedFavorsAroundMe(Location loc, Double radiusInKm) {
    double longDif =
        Math.toDegrees(
            radiusInKm / (FavoLocation.EARTH_RADIUS * Math.cos(Math.toRadians(loc.getLatitude()))));
    String longitudeField = "location.longitude";
    return collection
        .getReference()
        .whereEqualTo(Favor.IS_ARCHIVED, false)
        .whereGreaterThan(longitudeField, loc.getLongitude() - longDif)
        .whereLessThan(longitudeField, loc.getLongitude() + longDif)
        .limit(50);
  }

  @Override
  public DocumentReference getFavorReference(String id) {
    return collection.getDocumentQuery(id);
  }

  /**
   * Update the Favor photo url and update the database to match if not done already
   *
   * @param favor to get url updated
   * @param url of photo
   */
  @Override
  public CompletableFuture<Void> updateFavorPhoto(Favor favor, String url) {
    Map<String, Object> updates = new HashMap<>();
    updates.put(Favor.PICTURE_URL, url);
    favor.setPictureUrl(url);
    return collection.updateDocument(favor.getId(), updates);
  }

  /**
   * Returns all the favors for a given user (accepted/committed + requested)
   *
   * @param userId Id of the user
   */
  @Override
  public Query getAllUserFavors(String userId) {
    return collection.getReference().whereArrayContains(Favor.USER_IDS, userId);
  }
}
