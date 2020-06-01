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
import ch.epfl.favo.user.UserUtil;
import ch.epfl.favo.util.DependencyFactory;
import ch.epfl.favo.viewmodel.IFavorViewModel;

import static ch.epfl.favo.util.CommonTools.getUserName;

class FavorViewHolder extends RecyclerView.ViewHolder {

  private String TAG = "FavorViewHolder";

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
    mChatButton.setOnClickListener(view -> {
      viewModel.setObservedFavor(favor.getId());
//      try {
//        Thread.sleep(3000);
//      } catch (InterruptedException e) {
//        e.printStackTrace();
//      }

      Navigation.findNavController(parentView)
              .navigate(R.id.action_nav_favorList_to_chatView);
    });

//    buttonViewOption.setOnClickListener(view -> {
//      popup = new PopupMenu(context, buttonViewOption);
//      popup.inflate(R.menu.item_menu_active);
//
//      // display "commit" option if the favor is requested by the current user.
//      boolean isRequestedByCurrentUser = currentUser.getUid().equals(favor.getRequesterId());
//      setMenuItemClickListener(context, favor, isRequestedByCurrentUser, parentView);
//      popup.show();
//    });
  }

//  @SuppressLint("Assert")
//  @RequiresApi(api = Build.VERSION_CODES.N)
//  private void setMenuItemClickListener(Context context, Favor favor, boolean isRequestedByCurrentUser, View parentView) {
//    // onClickListener for each of the options.
//    popup.setOnMenuItemClickListener(item -> {
//      switch (item.getItemId()) {
//        case R.id.item_menu_cancel:
//          CompletableFuture cancelFuture = favorViewModel.cancelFavor(favor, isRequestedByCurrentUser);
//          cancelFuture.thenAccept(o ->
//                  CommonTools.showSnackbar(parentView, resources.getString(R.string.favor_cancel_success_msg_listView)));
//          cancelFuture.exceptionally(ex -> {
//            CommonTools.showSnackbar(parentView, resources.getString(
//                    getSnackbarMessageForFailedRequest((CompletionException) ex)));
//            Log.e(TAG, Objects.requireNonNull(((Exception) ex).getMessage()));
//            return null;
//          });
//          break;
//        case R.id.item_menu_view:
//          Bundle favorBundle = new Bundle();
//          favorBundle.putString("FAVOR_ARGS", favor.getId());
//          Navigation.findNavController((Activity) context, R.id.nav_host_fragment)
//                  .navigate(R.id.action_nav_favorlist_to_favorPublishedView, favorBundle);
//          break;
//      }
//      return false;
//    });
//  }

  @RequiresApi(api = Build.VERSION_CODES.N)
  private void setItemDisplay(Favor favor, Context context) {
    mTitleView.setText(favor.getTitle());
    mRewardView.setText(favor.getReward() + " coins");

    if (currentUser.getUid().equals(favor.getRequesterId())) {
      if (currentUser.getPhotoUrl() != null) {
        Glide.with(context)
                .load(currentUser.getPhotoUrl())
                .into(mRequesterIconView);
      }
      mRequesterView.setText(currentUser.getDisplayName() + " - ");
    } else {
      UserUtil.getSingleInstance().findUser(favor.getRequesterId())
        .thenAccept(user -> mRequesterView.setText(getUserName(user)));
    }
  }
}
