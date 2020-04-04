package ch.epfl.favo.common;

import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Future;
import java.util.concurrent.FutureTask;

import ch.epfl.favo.FakeItemFactory;
import ch.epfl.favo.favor.Favor;
import ch.epfl.favo.favor.FavorUtil;
import ch.epfl.favo.util.DependencyFactory;
import ch.epfl.favo.util.TaskToFutureAdapter;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doAnswer;

public class DatabaseWrapperTest {
  private Favor testFavor;
  @Before
  public void setUp() throws Exception {
    FirebaseFirestore mockFirestore = Mockito.mock(FirebaseFirestore.class);
    CollectionReference mockCollection = Mockito.mock(CollectionReference.class);
    DocumentReference mockDocumentReference = Mockito.mock(DocumentReference.class);
    Mockito.doReturn(mockCollection).when(mockFirestore).collection(anyString());
    Mockito.doReturn(mockDocumentReference).when(mockCollection).document(anyString());
    Mockito.doReturn(mockDocumentReference).when(mockCollection).document();
    Task<QuerySnapshot> querySnapshotTask = Mockito.mock(Task.class);
    Mockito.doReturn(querySnapshotTask).when(mockCollection).get();
    QuerySnapshot mockQuerySnapshot = Mockito.mock(QuerySnapshot.class);
    Mockito.doReturn(FakeItemFactory.getFavorList()).when(mockQuerySnapshot).toObjects(any());
    Mockito.doReturn(mockQuerySnapshot).when(querySnapshotTask).getResult();
    Task<DocumentSnapshot> newTask = Mockito.mock(Task.class);
    DocumentSnapshot mockResult = Mockito.mock(DocumentSnapshot.class);
    Mockito.doReturn(mockResult).when(newTask).getResult();
    Mockito.doReturn(testFavor).when(mockResult).toObject(any());
    Mockito.doReturn(newTask).when(mockDocumentReference).get(any());
    Mockito.doReturn(newTask).when(mockDocumentReference).get();
    DependencyFactory.setCurrentFirestore(mockFirestore);
    testFavor = FakeItemFactory.getFavor();

  }


  @After
  public void tearDown() throws Exception {

    DependencyFactory.setCurrentFirestore(null);
  }




  @Test
  public void addDocument() {

    DatabaseWrapper.addDocument(testFavor,"test");
  }

  @Test
  public void removeDocument() {
    DatabaseWrapper.removeDocument("bu","ba");
  }

  @Test
  public void updateDocument() {
    DatabaseWrapper.updateDocument("bu",testFavor.toMap(),"ba");
  }

  @Test
  public void getDocument() {
    DatabaseWrapper.getDocument("ba",Favor.class,"ba");
  }

  @Test
  public void getAllDocuments() {
    DatabaseWrapper.getAllDocuments(Favor.class,"be");
  }
}