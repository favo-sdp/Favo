package ch.epfl.favo.view.tabs.favorList;

import android.content.Context;
import android.os.Build;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseUser;

import ch.epfl.favo.R;
import ch.epfl.favo.favor.Favor;
import ch.epfl.favo.util.DependencyFactory;
import ch.epfl.favo.viewmodel.IFavorViewModel;

import static ch.epfl.favo.util.CommonTools.getUserName;

@SuppressLint("NewApi")
class FavorViewHolder extends RecyclerView.ViewHolder {

  private TextView mTitleView;
  private TextView mRequesterView;
  private TextView mRewardView;
  private ImageView mRequesterIconView;
  private TextView mChatButton;

  private FirebaseUser currentUser;

  FavorViewHolder(@NonNull View itemView) {
    super(itemView);

    mTitleView = itemView.findViewById(R.id.item_title);
    mChatButton = itemView.findViewById(R.id.item_menu_chat_button);
    mRequesterView = itemView.findViewById(R.id.item_requester);
    mRequesterIconView = itemView.findViewById(R.id.item_icon);
    mRewardView = itemView.findViewById(R.id.item_coins);

    currentUser = DependencyFactory.getCurrentFirebaseUser();
  }

  @RequiresApi(api = Build.VERSION_CODES.N)
  void bind(Context context, @NonNull Favor favor, View parentView, IFavorViewModel viewModel) {
    setItemDisplay(favor, context);
    mChatButton.setOnClickListener(
        view -> {
          viewModel.setObservedFavor(favor.getId());
          Navigation.findNavController(parentView).navigate(R.id.action_nav_favorList_to_chatView);
        });
  }

  private void setItemDisplay(Favor favor, Context context) {
    mTitleView.setText(favor.getTitle());
    mRewardView.setText(context.getString(R.string.favo_coins_item_placeholder, favor.getReward()));
    DependencyFactory.getCurrentUserRepository()
        .findUser(favor.getRequesterId())
        .thenAccept(
            user -> {
              mRequesterView.setText(getUserName(user));
              if (user.getPictureUrl() != null)
                Glide.with(context).load(user.getPictureUrl()).into(mRequesterIconView);
            });
  }

  /* Code for edit:
     favor.setStatusIdToInt(FavorStatus.EDIT);
     Bundle favorBundle = new Bundle();
     favorBundle.putParcelable(CommonTools.FAVOR_VALUE_ARGS, favor);
     favorBundle.putString(
             CommonTools.FAVOR_SOURCE, context.getResources().getString(R.string.favor_source_publishedFavor));
     findNavController((Activity) context, R.id.nav_host_fragment)
             .navigate(R.id.action_global_favorEditingView, favorBundle);
  */

}
