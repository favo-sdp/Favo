package ch.epfl.favo;
import android.app.Activity;
import android.app.Instrumentation;
import android.content.Intent;

import ch.epfl.favo.presenter.LoginActivity;
import ch.epfl.favo.presenter.MainActivity;
import ch.epfl.favo.presenter.StartupActivity;

import androidx.test.espresso.intent.Intents;
import androidx.test.espresso.intent.rule.IntentsTestRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.rule.ActivityTestRule;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;





import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;

import static androidx.test.espresso.intent.Intents.intended;
import static androidx.test.espresso.intent.matcher.IntentMatchers.hasComponent;
import static androidx.test.espresso.intent.matcher.IntentMatchers.toPackage;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

@RunWith(AndroidJUnit4.class)
public class StartUpActivityTest {

    @Rule
    public ActivityTestRule<StartupActivity> activityRule =
            new ActivityTestRule<>(StartupActivity.class);

    @Before
    public void setUp() {
        Intents.init();
    }

    @Test
    public void LoginShouldTakeToMainActivity(){
        activityRule.launchActivity(new Intent());
        //Check login screen is launched
        intended(hasComponent(LoginActivity.class.getName()));
        //Log in
        onView(withId(R.id.login_button)).perform(click());
        //Check main screen is launched
        intended(hasComponent(MainActivity.class.getName()));



    }
    @After
    public void cleanUp(){
        Intents.release();
    }

}
