package ch.epfl.favo.view.tabs.favorList;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseUser;

import ch.epfl.favo.R;
import ch.epfl.favo.favor.Favor;
import ch.epfl.favo.util.DependencyFactory;

@SuppressLint("NewApi")
class FavorViewHolder extends RecyclerView.ViewHolder {

  private final TextView mTitleView;
  private final TextView mRequesterView;
  private final TextView mRewardView;
  private final ImageView mRequesterIconView;

  private final FirebaseUser currentUser;

  FavorViewHolder(@NonNull View itemView) {
    super(itemView);

    mTitleView = itemView.findViewById(R.id.item_title);
    mRequesterView = itemView.findViewById(R.id.item_requester);
    mRequesterIconView = itemView.findViewById(R.id.item_icon);
    mRewardView = itemView.findViewById(R.id.item_coins);
    currentUser = DependencyFactory.getCurrentFirebaseUser();
  }

  void bind(Context context, @NonNull Favor favor) {
    mTitleView.setText(favor.getTitle());
    mRewardView.setText(context.getString(R.string.favo_coins_item_placeholder, favor.getReward()));
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
