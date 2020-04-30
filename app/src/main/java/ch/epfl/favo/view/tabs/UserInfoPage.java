package ch.epfl.favo.view.tabs;

import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;

import java.util.Objects;

import ch.epfl.favo.MainActivity;
import ch.epfl.favo.R;
import ch.epfl.favo.user.User;
import ch.epfl.favo.user.UserUtil;
import ch.epfl.favo.util.CommonTools;
import ch.epfl.favo.util.DependencyFactory;
import ch.epfl.favo.util.FavorFragmentFactory;

public class UserInfoPage extends Fragment {

  private View view;
  private User currentUser;

  public UserInfoPage() {
    // Required empty public constructor
  }

  @RequiresApi(api = Build.VERSION_CODES.N)
  @Override
  public View onCreateView(
      LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    // Inflate the layout for this fragment
    view = inflater.inflate(R.layout.fragment_user_info, container, false);

    ((MainActivity) requireActivity()).hideBottomNavigation();
    setupButtons();

    displayUserData(new User());

    if (currentUser == null && getArguments() != null) {
      String userId = getArguments().getString(FavorFragmentFactory.USER_ARGS);
      UserUtil.getSingleInstance()
          .findUser(userId)
          .thenAccept(
              user -> {
                currentUser = user;
                displayUserData(user);
              });
    }

    return view;
  }

  @RequiresApi(api = Build.VERSION_CODES.N)
  private void setupButtons() {
    ImageView likeButton = view.findViewById(R.id.like_button);
    likeButton.setOnClickListener(
        v -> {
          currentUser.setLikes(currentUser.getLikes() + 1);
          UserUtil.getSingleInstance()
              .updateUser(currentUser)
              .thenAccept(user -> displayUserData(currentUser));

          likeButton.setImageResource(R.drawable.ic_like_colored_48dp);
          CommonTools.showSnackbar(getView(), getString(R.string.feedback_message));
        });

    ImageView dislikeButton = view.findViewById(R.id.dislike_button);
    dislikeButton.setOnClickListener(
        v -> {
          currentUser.setDislikes(currentUser.getDislikes() + 1);
          UserUtil.getSingleInstance()
              .updateUser(currentUser)
              .thenAccept(user -> displayUserData(currentUser));

          dislikeButton.setImageResource(R.drawable.ic_dislike_colored_48dp);
          CommonTools.showSnackbar(getView(), getString(R.string.feedback_message));
        });

    Button reportUserButton = view.findViewById(R.id.report_user);
    reportUserButton.setOnClickListener(
        v -> CommonTools.showSnackbar(getView(), getString(R.string.report_message)));
  }

  private void displayUserData(User user) {

    ((TextView) view.findViewById(R.id.display_name))
        .setText(
            TextUtils.isEmpty(user.getName())
                ? Objects.requireNonNull(DependencyFactory.getCurrentFirebaseUser().getEmail()).split("@")[0]
                : DependencyFactory.getCurrentFirebaseUser().getDisplayName());

    ((TextView) view.findViewById(R.id.display_email))
        .setText(TextUtils.isEmpty(DependencyFactory.getCurrentFirebaseUser().getEmail()) ? "No email" : DependencyFactory.getCurrentFirebaseUser().getEmail());

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
  }
}
