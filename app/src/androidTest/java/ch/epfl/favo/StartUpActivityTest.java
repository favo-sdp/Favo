package ch.epfl.favo;
import ch.epfl.favo.presenter.StartupActivity;

import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.rule.ActivityTestRule;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;





import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;

import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

@RunWith(AndroidJUnit4.class)
public class StartUpActivityTest {

    @Rule
    public ActivityTestRule<StartupActivity> mActivity =
           new ActivityTestRule<>(StartupActivity.class);

    @Test
    public void LoginShouldTakeToMainActivity(){

        mActivity.getActivityResult();
        //login successfully.
        onView(withId(R.id.login_button)).perform(click());
        //Check main activity is launched.
        onView(withId(R.id.text1)).check(matches(withText("1")));


    }

}
