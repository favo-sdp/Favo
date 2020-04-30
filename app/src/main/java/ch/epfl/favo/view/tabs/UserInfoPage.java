package ch.epfl.favo.view.tabs;

import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;

import java.util.Objects;

import ch.epfl.favo.MainActivity;
import ch.epfl.favo.R;
import ch.epfl.favo.user.User;
import ch.epfl.favo.user.UserUtil;
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

    if (currentUser == null && getArguments() != null) {
      String userId = getArguments().getString(FavorFragmentFactory.USER_ARGS);
      UserUtil.getSingleInstance()
          .findUser(userId)
          .thenAccept(
              user -> {
                currentUser = user;
                displayUserData();
              });
    }

    return view;
  }

  @RequiresApi(api = Build.VERSION_CODES.N)
  private void setupButtons() {
    Button likeButton = view.findViewById(R.id.like_button);
    likeButton.setOnClickListener(
        v -> {
          currentUser.setLikes(currentUser.getLikes() + 1);
          UserUtil.getSingleInstance().updateUser(currentUser);
        });

    Button dislikeButton = view.findViewById(R.id.dislike_button);
    dislikeButton.setOnClickListener(
        v -> {
          currentUser.setDislikes(currentUser.getDislikes() + 1);
          UserUtil.getSingleInstance()
              .updateUser(currentUser)
              .thenAccept(user -> displayUserData());
        });
  }

  private void displayUserData() {

    ((TextView) view.findViewById(R.id.display_name))
        .setText(
            TextUtils.isEmpty(currentUser.getName())
                ? Objects.requireNonNull(currentUser.getEmail()).split("@")[0]
                : currentUser.getName());

    ((TextView) view.findViewById(R.id.display_email))
        .setText(TextUtils.isEmpty(currentUser.getEmail()) ? "No email" : currentUser.getEmail());

    ((TextView) view.findViewById(R.id.user_info_favorsCreated))
        .setText("Favors created: " + currentUser.getRequestedFavors());
    ((TextView) view.findViewById(R.id.user_info_favorsAccepted))
        .setText("Favors accepted: " + currentUser.getAcceptedFavors());
    ((TextView) view.findViewById(R.id.user_info_favorsCompleted))
        .setText("Favors completed: " + currentUser.getCompletedFavors());
    ((TextView) view.findViewById(R.id.user_info_likes))
        .setText("Positive\nfeedbacks: " + currentUser.getLikes());
    ((TextView) view.findViewById(R.id.user_info_dislikes))
        .setText("Negative\nfeedbacks: " + currentUser.getDislikes());
  }
}
