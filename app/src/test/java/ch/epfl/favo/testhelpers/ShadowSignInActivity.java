// package ch.epfl.favo.testhelpers;
//
// import org.robolectric.annotation.Implementation;
// import org.robolectric.annotation.Implements;
// import org.robolectric.annotation.RealObject;
// import org.robolectric.shadows.ShadowActivity;
//
// import ch.epfl.favo.SignInActivity;
//
// @Implements(SignInActivity.class)
// public class ShadowSignInActivity extends ShadowActivity {
//    @RealObject
//    private SignInActivity myActivity;
//
//    @Implementation
//    public void setForMockito(FirebaseUserAPI api) {
//        myActivity.fuAPI = api;
//    }
// }
