package ch.epfl.favo.view.tabs.favorList;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseUser;

import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;

import ch.epfl.favo.R;
import ch.epfl.favo.favor.Favor;
import ch.epfl.favo.util.CommonTools;
import ch.epfl.favo.util.DependencyFactory;
import ch.epfl.favo.viewmodel.IFavorViewModel;

import static ch.epfl.favo.util.CommonTools.getSnackbarMessageForFailedRequest;
import static ch.epfl.favo.util.CommonTools.getUserName;

@SuppressLint("NewApi")
class FavorViewHolder extends RecyclerView.ViewHolder {

  private String TAG = "FavorViewHolder";

  private TextView mTitleView;
  private TextView mRequesterView;
  private TextView mRewardView;
  private ImageView mRequesterIconView;
  private TextView buttonViewOption;
  private IFavorViewModel favorViewModel;

  private PopupMenu popup;

  private FirebaseUser currentUser;
  private Resources resources;

  FavorViewHolder(@NonNull View itemView) {
    super(itemView);

    mTitleView = itemView.findViewById(R.id.item_title);
    buttonViewOption = itemView.findViewById(R.id.item_menu_btn);
    mRequesterView = itemView.findViewById(R.id.item_requester);
    mRequesterIconView = itemView.findViewById(R.id.item_icon);
    mRewardView = itemView.findViewById(R.id.item_coins);

    currentUser = DependencyFactory.getCurrentFirebaseUser();
  }

  @RequiresApi(api = Build.VERSION_CODES.N)
  void bind(Context context, @NonNull Favor favor, View parentView) {
    resources = context.getResources();
    favorViewModel =
        (IFavorViewModel)
            new ViewModelProvider((FragmentActivity) context)
                .get(DependencyFactory.getCurrentViewModelClass());

    setItemDisplay(favor, context);

    buttonViewOption.setOnClickListener(
        view -> {
          popup = new PopupMenu(context, buttonViewOption);
          popup.inflate(R.menu.item_menu_active);

          // display "commit" option if the favor is requested by the current user.
          boolean isRequestedByCurrentUser = currentUser.getUid().equals(favor.getRequesterId());
          setMenuItemClickListener(context, favor, isRequestedByCurrentUser, parentView);
          popup.show();
        });
  }

  @SuppressLint("Assert")
  @RequiresApi(api = Build.VERSION_CODES.N)
  private void setMenuItemClickListener(
      Context context, Favor favor, boolean isRequestedByCurrentUser, View parentView) {
    // onClickListener for each of the options.
    popup.setOnMenuItemClickListener(
        item -> {
          switch (item.getItemId()) {
            case R.id.item_menu_cancel:
              CompletableFuture cancelFuture =
                  favorViewModel.cancelFavor(favor, isRequestedByCurrentUser);
              cancelFuture.thenAccept(
                  o ->
                      CommonTools.showSnackbar(
                          parentView,
                          resources.getString(R.string.favor_cancel_success_msg_listView)));
              cancelFuture.exceptionally(
                  ex -> {
                    CommonTools.showSnackbar(
                        parentView,
                        resources.getString(
                            getSnackbarMessageForFailedRequest((CompletionException) ex)));
                    Log.e(TAG, Objects.requireNonNull(((Exception) ex).getMessage()));
                    return null;
                  });
              break;
            case R.id.item_menu_view:
              Bundle favorBundle = new Bundle();
              favorBundle.putString("FAVOR_ARGS", favor.getId());
              Navigation.findNavController((Activity) context, R.id.nav_host_fragment)
                  .navigate(R.id.action_nav_favorlist_to_favorPublishedView, favorBundle);
              break;
          }
          return false;
        });
  }

  private void setItemDisplay(Favor favor, Context context) {
    mTitleView.setText(favor.getTitle());
    mRewardView.setText(favor.getReward() + " coins");
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
