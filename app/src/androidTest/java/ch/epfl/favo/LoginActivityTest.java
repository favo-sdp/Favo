package ch.epfl.favo;


import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.rule.ActivityTestRule;

import org.junit.After;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import ch.epfl.favo.presenter.LoginActivity;
import ch.epfl.favo.presenter.MainActivity;
import ch.epfl.favo.presenter.StartupActivity;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.closeSoftKeyboard;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

@RunWith(AndroidJUnit4.class)
public class LoginActivityTest {
    //Should be login activity but app doesn't launch if that's the case
    @Rule
    public final ActivityTestRule<LoginActivity> mActivityRule =
            new ActivityTestRule<>(LoginActivity.class);
    @Test
    public void appShouldNotShowLoginViewAfterLoggedIn() {
        onView(withId(R.id.user_email)).perform(typeText("blabla@epfl.ch")).perform(closeSoftKeyboard());
        onView(withId(R.id.user_password)).perform(typeText("valid_pw")).perform(closeSoftKeyboard());
        onView(withId(R.id.login_button)).perform(click());


        /**
         * TODO: implement test
         */
        //onView(withId(R.id.text_invalid_input)).check(matches(withText("Login Failed")));
        //onView(withId(R.id.custom_toast_container)).check(matches(withText("Hello from my unit test!")));
    }

}
