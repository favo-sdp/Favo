package ch.epfl.favo.view.tabs.favorList;

import android.content.Context;
import android.os.Build;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import ch.epfl.favo.R;
import ch.epfl.favo.favor.Favor;
import ch.epfl.favo.user.User;
import ch.epfl.favo.user.UserUtil;
import ch.epfl.favo.viewmodel.IFavorViewModel;
import de.hdodenhof.circleimageview.CircleImageView;

import static ch.epfl.favo.util.CommonTools.getUserName;

class FavorViewHolder extends RecyclerView.ViewHolder {

  private TextView mTitleView;
  private TextView mRequesterView;
  private TextView mRewardView;
  private CircleImageView mRequesterIconView;

  private TextView mChatButton;

  private User currentUser;

  FavorViewHolder(@NonNull View itemView) {
    super(itemView);

    mTitleView = itemView.findViewById(R.id.item_title);
    mChatButton = itemView.findViewById(R.id.item_menu_chat_button);
    mRequesterView = itemView.findViewById(R.id.item_requester);
    mRequesterIconView = itemView.findViewById(R.id.item_icon);
    mRewardView = itemView.findViewById(R.id.item_coins);
  }

  @RequiresApi(api = Build.VERSION_CODES.N)
  void bind(Context context, @NonNull Favor favor, View parentView, IFavorViewModel viewModel) {
    currentUser = viewModel.getObservedUser().getValue();
    setItemDisplay(favor, context);
    if(favor.getIsArchived())
      mChatButton.setVisibility(View.GONE);
    else
      mChatButton.setOnClickListener(
          view -> {
            viewModel.setObservedFavor(favor.getId());
            Navigation.findNavController(parentView).navigate(R.id.action_nav_favorList_to_chatView);
          });
  }

  @RequiresApi(api = Build.VERSION_CODES.N)
  private void setItemDisplay(Favor favor, Context context) {
    mTitleView.setText(favor.getTitle());
    mRewardView.setText(context.getString(R.string.num_coins, (int)favor.getReward()));

    if (currentUser.getId().equals(favor.getRequesterId())) {
      mRequesterView.setText(getUserName(currentUser));
      if (currentUser.getProfilePictureUrl() != null) {
        Glide.with(context).load(currentUser.getProfilePictureUrl()).into(mRequesterIconView);
      }
    } else {
      UserUtil.getSingleInstance()
          .findUser(favor.getRequesterId())
          .thenAccept(
              user -> {
                mRequesterView.setText(getUserName(user));
                if (user.getProfilePictureUrl() != null) {
                  Glide.with(context)
                      .load(user.getProfilePictureUrl())
                      .into(mRequesterIconView);
                }
              });
    }
  }
}
