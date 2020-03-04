 package ch.epfl.favo;

 import android.net.Uri;
 import android.os.Parcel;

 import androidx.annotation.NonNull;
 import androidx.annotation.Nullable;
 import androidx.test.espresso.intent.Intents;
 import androidx.test.ext.junit.runners.AndroidJUnit4;
 import androidx.test.rule.ActivityTestRule;

 import com.google.android.gms.internal.firebase_auth.zzff;
 import com.google.firebase.FirebaseApp;
 import com.google.firebase.auth.FirebaseUser;
 import com.google.firebase.auth.FirebaseUserMetadata;
 import com.google.firebase.auth.UserInfo;
 import com.google.firebase.auth.zzy;
 import com.google.firebase.auth.zzz;

 import org.junit.Rule;
 import org.junit.Test;
 import org.junit.runner.RunWith;

 import java.util.List;

 import static androidx.test.espresso.Espresso.onView;
 import static androidx.test.espresso.action.ViewActions.click;
 import static androidx.test.espresso.assertion.ViewAssertions.matches;
 import static androidx.test.espresso.intent.Intents.intended;
 import static androidx.test.espresso.intent.matcher.IntentMatchers.hasComponent;
 import static androidx.test.espresso.matcher.ViewMatchers.withId;
 import static androidx.test.espresso.matcher.ViewMatchers.withText;
 import static org.hamcrest.core.StringEndsWith.endsWith;

 @RunWith(AndroidJUnit4.class)
 public class UserAccountActivityTest {

     private static final String EMAIL = "test@example.com";
     private static final String NAME = "Test Testerson";
     private static final String PROVIDER = "test provider";
     private static final Uri PHOTO_URI = Uri.parse("http://example.com/profile.png");

     @Rule
     public final ActivityTestRule<SignInActivity> mActivityRule =
             new ActivityTestRule<SignInActivity>(SignInActivity.class){
                 @Override
                 protected void beforeActivityLaunched() {
                 DependencyFactory.setCurrentFirebaseUser(new FirebaseUser() {
                     @NonNull
                     @Override
                     public String getUid() {
                         return null;
                     }

                     @NonNull
                     @Override
                     public String getProviderId() {
                         return PROVIDER;
                     }

                     @Override
                     public boolean isAnonymous() {
                         return false;
                     }

                     @Nullable
                     @Override
                     public List<String> zza() {
                         return null;
                     }

                     @NonNull
                     @Override
                     public List<? extends UserInfo> getProviderData() {
                         return null;
                     }

                     @NonNull
                     @Override
                     public FirebaseUser zza(@NonNull List<? extends UserInfo> list) {
                         return null;
                     }

                     @Override
                     public FirebaseUser zzb() {
                         return null;
                     }

                     @NonNull
                     @Override
                     public FirebaseApp zzc() {
                         return null;
                     }

                     @Nullable
                     @Override
                     public String getDisplayName() {
                         return NAME;
                     }

                     @Nullable
                     @Override
                     public Uri getPhotoUrl() {
                         return PHOTO_URI;
                     }

                     @Nullable
                     @Override
                     public String getEmail() {
                         return EMAIL;
                     }

                     @Nullable
                     @Override
                     public String getPhoneNumber() {
                         return null;
                     }

                     @Nullable
                     @Override
                     public String zzd() {
                         return null;
                     }

                     @NonNull
                     @Override
                     public zzff zze() {
                         return null;
                     }

                     @Override
                     public void zza(@NonNull zzff zzff) {

                     }

                     @NonNull
                     @Override
                     public String zzf() {
                         return null;
                     }

                     @NonNull
                     @Override
                     public String zzg() {
                         return null;
                     }

                     @Nullable
                     @Override
                     public FirebaseUserMetadata getMetadata() {
                         return null;
                     }

                     @NonNull
                     @Override
                     public zzz zzh() {
                         return null;
                     }

                     @Override
                     public void zzb(List<zzy> list) {

                     }

                     @Override
                     public void writeToParcel(Parcel dest, int flags) {

                     }

                     @Override
                     public boolean isEmailVerified() {
                         return false;
                     }
                 });
                 }
             };

     @Test
     public void testUserAlreadyLoggedIn_displayUserData() {
         onView(withId(R.id.user_name)).check(matches(withText(NAME)));
         onView(withId(R.id.user_email)).check(matches(withText(EMAIL)));
         onView(withId(R.id.user_providers)).check(matches(withText(endsWith(PROVIDER))));
     }

     @Test
     public void testUserAlreadyLoggedIn_signOut() {
         Intents.init();
         DependencyFactory.setCurrentFirebaseUser(null);
         onView(withId(R.id.sign_out)).perform(click());
         intended(hasComponent(SignInActivity.class.getName()));
     }

     @Test
     public void testUserAlreadyLoggedIn_deleteAccount() {
//         Intents.init();
//         DependencyFactory.setCurrentFirebaseUser(null);
//         onView(withId(R.id.delete_account)).perform(click());
//         onView(withId(android.R.id.button1)).perform(click());
//         intended(hasComponent(SignInActivity.class.getName()));
     }

 }
