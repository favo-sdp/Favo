package ch.epfl.favo;

import android.content.Intent;

import androidx.test.espresso.intent.Intents;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.rule.ActivityTestRule;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import ch.epfl.favo.presenter.LoginActivity;
import ch.epfl.favo.presenter.MainActivity;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.intent.Intents.intended;
import static androidx.test.espresso.intent.matcher.IntentMatchers.hasComponent;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

@RunWith(AndroidJUnit4.class)
public class LoginActivityTest {
    @Rule
    public final ActivityTestRule<LoginActivity> activityRule =
            new ActivityTestRule<>(LoginActivity.class);
    @Before
    public void init(){
        Intents.init();
    }
    @Test
    public void logInLeadsToMainActivity(){
        activityRule.launchActivity(new Intent());
        //login successfully
        onView(withId(R.id.login_button)).perform(click());
        //check we are in main activity
        intended(hasComponent(MainActivity.class.getName()));
    }
    @After
    public void cleanUp(){
        Intents.release();
    }
}
