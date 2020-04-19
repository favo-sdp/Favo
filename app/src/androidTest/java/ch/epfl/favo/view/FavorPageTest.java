package ch.epfl.favo.view;

import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.rule.ActivityTestRule;
import androidx.test.rule.GrantPermissionRule;

import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;

import ch.epfl.favo.FakeFirebaseUser;
import ch.epfl.favo.FakeItemFactory;
import ch.epfl.favo.MainActivity;
import ch.epfl.favo.R;
import ch.epfl.favo.common.CollectionWrapper;
import ch.epfl.favo.favor.Favor;
import ch.epfl.favo.util.DependencyFactory;

import static androidx.test.espresso.Espresso.onData;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.assertion.ViewAssertions.doesNotExist;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withContentDescription;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withParent;
import static androidx.test.espresso.matcher.ViewMatchers.withSpinnerText;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static androidx.test.platform.app.InstrumentationRegistry.getInstrumentation;
import static ch.epfl.favo.TestConstants.EMAIL;
import static ch.epfl.favo.TestConstants.NAME;
import static ch.epfl.favo.TestConstants.PHOTO_URI;
import static ch.epfl.favo.TestConstants.PROVIDER;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.core.AllOf.allOf;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyMap;
import static org.mockito.ArgumentMatchers.anyString;

@RunWith(AndroidJUnit4.class)
public class FavorPageTest {
  private MockDatabaseWrapper mockDatabaseWrapper = new MockDatabaseWrapper();
  @Rule
  public final ActivityTestRule<MainActivity> mainActivityTestRule =
      new ActivityTestRule<MainActivity>(MainActivity.class) {
        @Override
        protected void beforeActivityLaunched() {
          DependencyFactory.setCurrentFirebaseUser(
              new FakeFirebaseUser(NAME, EMAIL, PHOTO_URI, PROVIDER));
          DependencyFactory.setCurrentGpsTracker(new MockGpsTracker());
          DependencyFactory.setCurrentCollectionWrapper(new MockDatabaseWrapper());
          mockDatabaseWrapper.setMockDocument(FakeItemFactory.getFavor());
          mockDatabaseWrapper.setThrowError(false);
        }
      };

  @Rule
  public GrantPermissionRule permissionRule =
      GrantPermissionRule.grant(android.Manifest.permission.ACCESS_FINE_LOCATION);


  private Favor testFavor;
  private CollectionWrapper<Favor> collectionWrapper;
  private FirebaseFirestore mockFirestore;
  private CollectionReference mockCollectionReference;
  private DocumentReference mockDocumentReference;
  private Task<DocumentSnapshot> documentSnapshotTask;
  private DocumentSnapshot mockDocumentSnapshot;
  private QuerySnapshot mockQuerySnapshot;

  @Before
  public void setUp() throws Exception {
    mockFirestore = Mockito.mock(FirebaseFirestore.class);
    mockCollectionReference = Mockito.mock(CollectionReference.class);
    mockDocumentReference = Mockito.mock(DocumentReference.class);
    testFavor = FakeItemFactory.getFavor();
    collectionWrapper = new CollectionWrapper<>("favors", Favor.class);

    // return collection refernece from firestore object
    Mockito.doReturn(mockCollectionReference).when(mockFirestore).collection(anyString());
    setupMockGetDocument();
    setupMockDocumentListRetrieval();
    DependencyFactory.setCurrentFirestore(mockFirestore);
  }

  /** Mock collectionreference->querysnapshotTask->querysnapshot */
  private void setupMockDocumentListRetrieval() {
    Task<QuerySnapshot> querySnapshotTask = Mockito.mock(Task.class);
    Mockito.doReturn(querySnapshotTask).when(mockCollectionReference).get();
    mockQuerySnapshot = Mockito.mock(QuerySnapshot.class);

    Mockito.doReturn(mockQuerySnapshot).when(querySnapshotTask).getResult();
    Mockito.doReturn(mockQuerySnapshot).when(querySnapshotTask).getResult(any());
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
  }


  @After
  public void tearDown() {
    DependencyFactory.setCurrentFirebaseUser(null);
    DependencyFactory.setCurrentGpsTracker(null);
    DependencyFactory.setCurrentCollectionWrapper(null);
  }

  @Test
  public void testFavorPage() {
    // click on favors tab
    onView(withId(R.id.nav_favorList)).check(matches(isDisplayed())).perform(click());
    getInstrumentation().waitForIdleSync();
  }

  @Test
  public void testFavorPageElements() {
    // click on favors tab
    onView(withId(R.id.nav_favorList)).check(matches(isDisplayed())).perform(click());
    getInstrumentation().waitForIdleSync();

    // check that tab 2 is indeed opened
    onView(allOf(withId(R.id.fragment_favors), withParent(withId(R.id.nav_host_fragment))))
        .check(matches(isDisplayed()));

    onView(withId(R.id.floatingActionButton)).check(matches(isDisplayed()));

    getInstrumentation().waitForIdleSync();

    // onView(withId(R.id.favor_list)).check(matches(isDisplayed()));

    onView(withId(R.id.spinner)).perform(click());
    onData(allOf(is(instanceOf(String.class)), is("Archived"))).perform(click());
    onView(withId(R.id.spinner)).check(matches(withSpinnerText(containsString("Archived"))));

    getInstrumentation().waitForIdleSync();

    onView(withId(R.id.spinner)).perform(click());
    onData(allOf(is(instanceOf(String.class)), is("Active"))).perform(click());
    onView(withId(R.id.spinner)).check(matches(withSpinnerText(containsString("Active"))));
  }

  @Test
  public void testTextDisplayedWhenListEmpty() {
    // click on favors tab
    onView(withId(R.id.nav_favorList)).check(matches(isDisplayed())).perform(click());
    getInstrumentation().waitForIdleSync();

    // check that tab 2 is indeed opened
    onView(allOf(withId(R.id.fragment_favors), withParent(withId(R.id.nav_host_fragment))))
        .check(matches(isDisplayed()));

    // check the tip text is displayed when active favor list is empty
    onView(withId(R.id.tip))
        .check(matches(isDisplayed()))
        .check(matches(withText(R.string.favor_no_active_favor)));

    // go to archived list
    onView(withId(R.id.spinner)).perform(click());
    onData(allOf(is(instanceOf(String.class)), is("Archived"))).perform(click());
    onView(withId(R.id.spinner)).check(matches(withSpinnerText(containsString("Archived"))));

    // check the tip text is displayed when archived favor list is empty
    onView(withId(R.id.tip))
        .check(matches(isDisplayed()))
        .check(matches(withText(R.string.favor_no_archived_favor)));
  }

  @Test
  public void testNewFavorButton() {
    // Click on favors tab
    onView(withId(R.id.nav_favorList)).check(matches(isDisplayed())).perform(click());
    getInstrumentation().waitForIdleSync();

    // check that tab 2 is indeed opened
    onView(allOf(withId(R.id.fragment_favors), withParent(withId(R.id.nav_host_fragment))))
        .check(matches(isDisplayed()));

    // check that the new favor button is displayed and click on it
    onView(withId(R.id.floatingActionButton)).check(matches(isDisplayed())).perform(click());
    getInstrumentation().waitForIdleSync();

    // check that the favor request fragment is displayed
    onView(withId(R.id.fragment_favor)).check(matches(isDisplayed()));
  }

  @Test
  public void testFavorRequestUpdatesListView() {
    // Click on favors tab
    onView(withId(R.id.nav_favorList)).check(matches(isDisplayed())).perform(click());
    getInstrumentation().waitForIdleSync();

    // Click on new favor tab
    onView(withId(R.id.floatingActionButton)).check(matches(isDisplayed())).perform(click());
    getInstrumentation().waitForIdleSync();

    // Fill in text views with fake favor
    Favor favor = FakeItemFactory.getFavor();

    onView(withId(R.id.title_request_view)).perform(typeText(favor.getTitle()));
    onView(withId(R.id.details)).perform(typeText(favor.getDescription()));

    // Click on request button
    onView(withId(R.id.request_button)).check(matches(isDisplayed())).perform(click());
    getInstrumentation().waitForIdleSync();

    // Click on back button
    onView(withContentDescription(R.string.abc_action_bar_up_description)).perform(click());
    getInstrumentation().waitForIdleSync();

    // check favor is displayed in active favor list view
    onView(withText(favor.getTitle())).check(matches(isDisplayed()));
  }


  @Test
  public void testFavorCancelUpdatesActiveAndArchivedListView() throws InterruptedException {
    // Click on favors tab
    onView(withId(R.id.nav_favorList)).check(matches(isDisplayed())).perform(click());
    getInstrumentation().waitForIdleSync();

    // Click on new favor tab
    onView(withId(R.id.floatingActionButton)).check(matches(isDisplayed())).perform(click());
    getInstrumentation().waitForIdleSync();

    // Fill in text views with fake favor
    Favor favor = FakeItemFactory.getFavor();

    onView(withId(R.id.title_request_view)).perform(typeText(favor.getTitle()));
    onView(withId(R.id.details)).perform(typeText(favor.getDescription()));

    // Click on request button
    onView(withId(R.id.request_button)).check(matches(isDisplayed())).perform(click());
    getInstrumentation().waitForIdleSync();
    Thread.sleep(4000); //wait for snackbar to hide

    // Click on cancel button
    onView(withId(R.id.cancel_favor_button)).check(matches(isDisplayed())).perform(click());
    getInstrumentation().waitForIdleSync();

    // Go back
    onView(withContentDescription(R.string.abc_action_bar_up_description)).perform(click());
    getInstrumentation().waitForIdleSync();

    // Check favor is not displayed in active list
    onView(withText(favor.getTitle())).check(doesNotExist());

    // go to archived list
    onView(withId(R.id.spinner)).perform(click());
    onData(allOf(is(instanceOf(String.class)), is("Archived"))).perform(click());
    getInstrumentation().waitForIdleSync();

    // check favor is displayed in archived favor list view
    onView(withText(favor.getTitle())).check(matches(isDisplayed()));
  }
}
