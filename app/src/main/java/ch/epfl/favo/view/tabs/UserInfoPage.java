package ch.epfl.favo.view.tabs;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseUser;

import java.util.Objects;

import ch.epfl.favo.R;
import ch.epfl.favo.auth.SignInActivity;
import ch.epfl.favo.util.CommonTools;
import ch.epfl.favo.util.DependencyFactory;

public class UserInfoPage extends Fragment {

  private View view;

  public UserInfoPage() {
    // Required empty public constructor
  }

  @Override
  public View onCreateView(
      LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    // Inflate the layout for this fragment
    view = inflater.inflate(R.layout.user_info, container, false);

    setupButtons();
    displayUserData();

    return view;
  }

  private void setupButtons() {
    Button likeButton = view.findViewById(R.id.like_button);
    likeButton.setOnClickListener(this::update);

    Button dislikeButton = view.findViewById(R.id.dislike_button);
    dislikeButton.setOnClickListener(this::update);
  }

  private void displayUserData(FirebaseUser user) {

    if (user.getPhotoUrl() != null) {
      Glide.with(this)
          .load(user.getPhotoUrl())
          .fitCenter()
          .into((ImageView) view.findViewById(R.id.user_profile_picture));
    }

    ((TextView) view.findViewById(R.id.user_name))
        .setText(
            TextUtils.isEmpty(user.getDisplayName())
                ? Objects.requireNonNull(user.getEmail()).split("@")[0]
                : user.getDisplayName());

    ((TextView) view.findViewById(R.id.user_email))
        .setText(TextUtils.isEmpty(user.getEmail()) ? "No email" : user.getEmail());
  }
}
