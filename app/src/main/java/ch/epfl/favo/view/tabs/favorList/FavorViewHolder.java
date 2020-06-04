package ch.epfl.favo.view.tabs.favorList;

import android.content.Context;
import android.os.Build;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseUser;

import ch.epfl.favo.R;
import ch.epfl.favo.favor.Favor;
import ch.epfl.favo.util.DependencyFactory;

class FavorViewHolder extends RecyclerView.ViewHolder {

  private TextView mTitleView;
  private TextView mRequesterView;
  private TextView mRewardView;
  private ImageView mRequesterIconView;

  private FirebaseUser currentUser;

  FavorViewHolder(@NonNull View itemView) {
    super(itemView);

    mTitleView = itemView.findViewById(R.id.item_title);
    mRequesterView = itemView.findViewById(R.id.item_requester);
    mRequesterIconView = itemView.findViewById(R.id.item_icon);
    mRewardView = itemView.findViewById(R.id.item_coins);
    currentUser = DependencyFactory.getCurrentFirebaseUser();
  }

  @RequiresApi(api = Build.VERSION_CODES.N)
  void bind(Context context, @NonNull Favor favor) {
    mTitleView.setText(favor.getTitle());
    mRewardView.setText(context.getString(R.string.favo_coins_item_placeholder, favor.getReward()));

    if (currentUser.getUid().equals(favor.getRequesterId())) {
      if (currentUser.getPhotoUrl() != null) {
        Glide.with(context).load(currentUser.getPhotoUrl()).into(mRequesterIconView);
      }
      mRequesterView.setText(
          context.getString(R.string.user_name_item_placeholder, currentUser.getDisplayName()));
    } else {
      DependencyFactory.getCurrentUserRepository()
          .findUser(favor.getRequesterId())
          .thenAccept(
              user -> {
                mRequesterView.setText(
                    context.getString(R.string.user_name_item_placeholder, user.getName()));
                if (user.getProfilePictureUrl() != null)
                  Glide.with(context).load(user.getProfilePictureUrl()).into(mRequesterIconView);
              });
    }
  }
}
