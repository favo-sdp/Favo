package ch.epfl.favo.favor;

import android.location.Location;

import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.Query;

import java.util.concurrent.CompletableFuture;

/**
 * Favor Utility acts as a repository of favors. It allows us to communicate with Firestore and
 * manage the favors collection
 */
public interface IFavorUtil {
  /**
   * New Favor is posted in the database
   *
   * @param favor Favor to be posted.
   * @return A future that is completed if the favor is successfully posted
   * @throws RuntimeException
   */
  CompletableFuture<Void> requestFavor(Favor favor) throws RuntimeException;

  /**
   * Update favor
   *
   * @param favor
   * @return Future that is completed if favor is successfully posted
   */
  CompletableFuture<Void> updateFavor(Favor favor);

  /**
   * Used to find a Favor item in the database
   *
   * @param favorId
   * @return Future containing favor object from favors collection
   */
  CompletableFuture<Favor> retrieveFavor(String favorId);

  /**
   * Removes favor from database
   *
   * @param favorId
   * @return
   */
  CompletableFuture<Void> removeFavor(String favorId);

  /**
   * Queries firestore to get a list of active favors That have a longitude displacement less than
   * the radius
   *
   * @param loc Center of query
   * @param radiusInKm radius threshold in kms
   * @return query object that can be observed
   */
  Query getLongitudeBoundedFavorsAroundMe(Location loc, Double radiusInKm);

  /**
   * Gets firestore reference to the favor document
   *
   * @param id favor id
   * @return Document Reference that can be observed
   */
  DocumentReference getFavorReference(String id);

  /**
   * Updates the url of the favor document
   *
   * @param favor
   * @param url
   * @return
   */
  CompletableFuture<Void> updateFavorPhoto(Favor favor, String url);

  /**
   * Get all favors requested and accepted by user (archived and active)
   *
   * @param userId
   * @return Query object that can be observer
   */
  Query getAllUserFavors(String userId);
}
