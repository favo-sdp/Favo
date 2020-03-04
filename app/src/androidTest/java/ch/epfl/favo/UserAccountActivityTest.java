// package ch.epfl.favo;
//
// import androidx.test.ext.junit.runners.AndroidJUnit4;
// import androidx.test.rule.ActivityTestRule;
//
// import com.firebase.ui.auth.AuthUI;
// import com.firebase.ui.auth.data.model.User;
// import com.google.firebase.auth.FirebaseAuth;
//
// import org.junit.Before;
// import org.junit.Rule;
// import org.junit.Test;
// import org.junit.runner.RunWith;
//
// import java.util.Objects;
//
// import ch.epfl.favo.testhelpers.TestHelper;
//
// import static androidx.test.espresso.Espresso.onView;
// import static androidx.test.espresso.assertion.ViewAssertions.matches;
// import static androidx.test.espresso.matcher.ViewMatchers.withId;
// import static androidx.test.espresso.matcher.ViewMatchers.withText;
// import static org.mockito.Mockito.when;
//
// @RunWith(AndroidJUnit4.class)
// public class UserAccountActivityTest {
//
//    private AuthUI mAuthUi;
//
//    @Rule
//    public ActivityTestRule<UserAccountActivity> mNoteDetailActivityTestRule =
//            new ActivityTestRule<>(UserAccountActivity.class, true /* Initial touch mode  */,
//                    false /* Lazily launch activity */);
//
//    @Before
//    public void setUp() {
//        TestHelper.initialize();
//        mAuthUi = AuthUI.getInstance(TestHelper.MOCK_APP);
//        FirebaseAuth.getInstance(TestHelper.MOCK_APP);
//
// when(FirebaseAuth.getInstance().getCurrentUser()).thenReturn(TestHelper.getMockFirebaseUser());
//    }
//
////    @Rule
////    public ActivityTestRule<UserAccountActivity> activityRule = new
// ActivityTestRule<UserAccountActivity>(UserAccountActivity.class) {
////        @Override
////        protected void beforeActivityLaunched() {
////
////        }
////    };
//
//    @Test
//    public void testCanGreetUsers() {
//
//        //UserAccountActivity user = new UserAccountActivity();
//
//
////
////        UserAccountActivity user = new UserAccountActivity();
////        //user.showUserData(TestHelper.getMockFirebaseUser(), TestHelper.CONTEXT);
////
////        user.showUserData(TestHelper.getMockFirebaseUser());
//        //onView(withId(R.id.user_name)).check(matches(withText("Ciao")));
//    }
// }
