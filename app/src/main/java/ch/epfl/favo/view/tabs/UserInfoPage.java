package ch.epfl.favo.view.tabs;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;

import java.util.Objects;

import ch.epfl.favo.MainActivity;
import ch.epfl.favo.R;
import ch.epfl.favo.user.User;
import ch.epfl.favo.util.CommonTools;
import ch.epfl.favo.util.DependencyFactory;

@SuppressLint("NewApi")
public class UserInfoPage extends Fragment {

  private View view;
  private User currentUser;

  public UserInfoPage() {
    // Required empty public constructor
  }

  @Override
  public View onCreateView(
      LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    // Inflate the layout for this fragment
    view = inflater.inflate(R.layout.fragment_user_info, container, false);

    ((MainActivity) requireActivity()).hideBottomNavigation();
    setupButtons();

    displayUserData(new User(null, "", "", null, null, null));

    if (currentUser == null && getArguments() != null) {
      String userId = getArguments().getString(CommonTools.USER_ARGS);
      DependencyFactory.getCurrentUserRepository()
          .findUser(userId)
          .thenAccept(
              user -> {
                currentUser = user;
                displayUserData(user);
              });
    }

    return view;
  }

  private void setupButtons() {
    // TODO: decide what to do with reported users
    Button reportUserButton = view.findViewById(R.id.report_user);
    reportUserButton.setOnClickListener(
        v -> CommonTools.showSnackbar(getView(), getString(R.string.report_user_message)));
  }

  private void displayUserData(User user) {

    ((TextView) view.findViewById(R.id.display_name))
        .setText(CommonTools.getUserName(user));

    ((TextView) view.findViewById(R.id.display_email))
        .setText(TextUtils.isEmpty(user.getEmail()) ? "Email" : user.getEmail());

    ((TextView) view.findViewById(R.id.user_info_favorsCreated))
        .setText(getString(R.string.favors_created_format, user.getRequestedFavors()));
    ((TextView) view.findViewById(R.id.user_info_favorsAccepted))
        .setText(getString(R.string.favors_accepted_format, user.getAcceptedFavors()));
    ((TextView) view.findViewById(R.id.user_info_favorsCompleted))
        .setText(getString(R.string.favors_completed_format, user.getCompletedFavors()));
    ((TextView) view.findViewById(R.id.user_info_likes))
        .setText(getString(R.string.likes_format, user.getLikes()));
    ((TextView) view.findViewById(R.id.user_info_dislikes))
        .setText(getString(R.string.dislikes_format, user.getDislikes()));
    ImageView profilePicture = view.findViewById(R.id.user_info_profile_picture);
    if (user.getProfilePictureUrl() != null)
      Glide.with(this).load(user.getProfilePictureUrl()).fitCenter().into(profilePicture);
  }
}
