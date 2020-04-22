package ch.epfl.favo.common;

import android.location.Location;

import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import ch.epfl.favo.FakeItemFactory;
import ch.epfl.favo.favor.Favor;
import ch.epfl.favo.util.DependencyFactory;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyDouble;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyMap;
import static org.mockito.ArgumentMatchers.anyString;

public class DatabaseWrapperTest {
  private Favor testFavor;
  private List<Favor> expectedFavors;
  private CollectionWrapper<Favor> collectionWrapper;
  private FirebaseFirestore mockFirestore;
  private CollectionReference mockCollectionReference;
  private DocumentReference mockDocumentReference;
  private Task<DocumentSnapshot> documentSnapshotTask;
  private DocumentSnapshot mockDocumentSnapshot;
  private QuerySnapshot mockQuerySnapshot;

  @Before
  public void setUp() {
    mockFirestore = Mockito.mock(FirebaseFirestore.class);
    mockCollectionReference = Mockito.mock(CollectionReference.class);
    mockDocumentReference = Mockito.mock(DocumentReference.class);
    testFavor = FakeItemFactory.getFavor();
    collectionWrapper = new CollectionWrapper<>(DependencyFactory.getCurrentFavorCollection(), Favor.class);
    expectedFavors = FakeItemFactory.getFavorList();

    // return collection refernece from firestore object
    Mockito.doReturn(mockCollectionReference).when(mockFirestore).collection(anyString());
    DependencyFactory.setCurrentFirestore(mockFirestore);
    setupMockGetDocument();
    setupMockDocumentListRetrieval();
  }

  /** Mock collectionreference->querysnapshotTask->querysnapshot */
  private void setupMockDocumentListRetrieval() {
    // setup for testing getAllDocuments()
    Task<QuerySnapshot> querySnapshotTask = Mockito.mock(Task.class);
    Mockito.doReturn(querySnapshotTask).when(mockCollectionReference).get();
    //setup for testing getAllDocumentsLongitudeLatitudeBounded()
    Query query = Mockito.mock(Query.class);
    Mockito.doReturn(query).when(mockCollectionReference).whereGreaterThan(anyString(), anyDouble());
    Mockito.doReturn(query).when(query).whereLessThan(anyString(), anyDouble());
    Mockito.doReturn(query).when(query).limit(anyLong());
    Mockito.doReturn(querySnapshotTask).when(query).get();
    // setup task, below is common parts
    mockQuerySnapshot = Mockito.mock(QuerySnapshot.class);
    Mockito.doReturn(mockQuerySnapshot).when(querySnapshotTask).getResult();
    Mockito.doReturn(mockQuerySnapshot).when(querySnapshotTask).getResult(any());
    // return favor object from document
    Mockito.doReturn(expectedFavors).when(mockQuerySnapshot).toObjects(any());
  }

  /** Mock DocumentReference -> documentSnapshotTask->documentsnapshot */
  private void setupMockGetDocument() {
    // return document reference when collection.document()
    Mockito.doReturn(mockDocumentReference).when(mockCollectionReference).document(anyString());
    Mockito.doReturn(mockDocumentReference).when(mockCollectionReference).document();
    Task mockEmptyTask = Mockito.mock(Task.class);
    Mockito.doReturn(mockEmptyTask).when(mockDocumentReference).update(anyMap());
    // mock return task
    documentSnapshotTask = Mockito.mock(Task.class);
    // mock document returned by task
    mockDocumentSnapshot = Mockito.mock(DocumentSnapshot.class);
    // return mock document when task.getresult
    Mockito.doReturn(mockDocumentSnapshot).when(documentSnapshotTask).getResult();
    // return task when document reference is called on get
    Mockito.doReturn(documentSnapshotTask).when(mockDocumentReference).get(any());
    Mockito.doReturn(documentSnapshotTask).when(mockDocumentReference).get();
    // return favor object from document
    Mockito.doReturn(testFavor).when(mockDocumentSnapshot).toObject(any());
  }

  private<T> void setupCompletableFuture(T mockSnapshot){
    CompletableFuture<T> futureSnapshot = new CompletableFuture<>();
    futureSnapshot.complete(mockSnapshot);
    DependencyFactory.setCurrentCompletableFuture(futureSnapshot);
  }

  @After
  public void tearDown() {
    DependencyFactory.setCurrentCompletableFuture(null);
    DependencyFactory.setCurrentFirestore(null);
  }

  @Test
  public void addDocument() {
    collectionWrapper.addDocument(testFavor);
  }

  @Test
  public void removeDocument() {
    collectionWrapper.removeDocument("bla");
  }

  @Test
  public void updateDocument() {
    collectionWrapper.updateDocument("bu", testFavor.toMap());
  }

  @Test
  public void testGetDocumentReturnsExpectedDocument()
      throws ExecutionException, InterruptedException, TimeoutException {
    setupCompletableFuture(mockDocumentSnapshot);
    Mockito.doReturn(true).when(mockDocumentSnapshot).exists();
    CompletableFuture<Favor> actualFuture = collectionWrapper.getDocument("fish");
    Favor obtained = actualFuture.get(2, TimeUnit.SECONDS);
    assertEquals(testFavor,obtained);
  }

  @Test
  public void testGetDocumentReturnsExceptionIfNull() {
    // document snapshot doesn't exist (not returned)
    setupCompletableFuture(mockDocumentSnapshot);
    Mockito.doReturn(false).when(mockDocumentSnapshot).exists();
    CompletableFuture<Favor> actualFuture = collectionWrapper.getDocument("fish");
    assertEquals(true, actualFuture.isCompletedExceptionally());
  }

  @Test
  public void testGetAllDocumentsReturnsExpectedList()
      throws InterruptedException, ExecutionException {
    setupCompletableFuture(mockQuerySnapshot);
    CompletableFuture<List<Favor>> obtainedFuture = collectionWrapper.getAllDocuments();
    List<Favor> obtainedFavors = obtainedFuture.get();
    assertEquals(expectedFavors,obtainedFavors);
  }

  @Test
  public void testGetAllDocumentsLongitudeLatitudeBoundedExpectedList()
          throws InterruptedException, ExecutionException{
    setupCompletableFuture(mockQuerySnapshot);
    CompletableFuture<List<Favor>> obtainedFuture = collectionWrapper
            .getAllDocumentsLongitudeBounded(new Location("null"), 1.0);
    List<Favor> obtainedFavors = obtainedFuture.get();
    assertEquals(expectedFavors, obtainedFavors);
  }
}
