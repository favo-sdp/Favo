package ch.epfl.favo.view.tabs.favorList;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
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
import java.util.function.Function;

import ch.epfl.favo.R;
import ch.epfl.favo.exception.IllegalAcceptException;
import ch.epfl.favo.exception.IllegalRequestException;
import ch.epfl.favo.favor.Favor;
import ch.epfl.favo.favor.FavorStatus;
import ch.epfl.favo.user.UserUtil;
import ch.epfl.favo.util.CommonTools;
import ch.epfl.favo.util.DependencyFactory;
import ch.epfl.favo.viewmodel.IFavorViewModel;

import static androidx.navigation.Navigation.findNavController;

class FavorViewHolder extends RecyclerView.ViewHolder {

  private String TAG = "FavorViewHolder";

  private TextView mTitleView;
  private TextView mRequesterView;
  private TextView mRewardView;
  private ImageView mRequesterIconView;
  private TextView buttonViewOption;
  private IFavorViewModel favorViewModel;

  FavorViewHolder(@NonNull View itemView) {
    super(itemView);

    mTitleView = itemView.findViewById(R.id.item_title);
    buttonViewOption = itemView.findViewById(R.id.item_menu_btn);
    mRequesterView = itemView.findViewById(R.id.item_requester);
    mRequesterIconView = itemView.findViewById(R.id.item_icon);
    mRewardView = itemView.findViewById(R.id.item_coins);
  }

  @SuppressLint({"SetTextI18n", "Assert"})
  @RequiresApi(api = Build.VERSION_CODES.N)
  void bind(Context context, @NonNull Favor favor, View parentView) {
    favorViewModel = (IFavorViewModel)
            new ViewModelProvider((FragmentActivity) context)
                    .get(DependencyFactory.getCurrentViewModelClass());

    mTitleView.setText(favor.getTitle());
    mRewardView.setText(favor.getReward() + " coins");

    FirebaseUser currentUser = DependencyFactory.getCurrentFirebaseUser();
    boolean currentUserIsRequester = favor.getRequesterId().equals(currentUser.getUid());
    if (currentUserIsRequester) {
      if (currentUser.getPhotoUrl() != null) {
        Glide.with(context)
                .load(currentUser.getPhotoUrl())
                .into(mRequesterIconView);
      }
      mRequesterView.setText(currentUser.getDisplayName() + " - ");
    } else {
      UserUtil.getSingleInstance().findUser(favor.getRequesterId())
              .thenAccept(user -> {
                String name = user.getName();
                if (name == null || name.equals(""))
                  mRequesterView.setText(CommonTools.emailToName(user.getEmail()) + " - ");
                else
                  mRequesterView.setText(name + " - ");
              });
    }


//    mDescriptionView.setText(String.valueOf(favor.getDescription()));
    buttonViewOption.setOnClickListener(view -> {
      PopupMenu popup = new PopupMenu(context, buttonViewOption);
      popup.inflate(R.menu.item_menu_active);
      if (currentUserIsRequester)
        popup.getMenu().findItem(R.id.item_menu_commit).setEnabled(false);
      popup.setOnMenuItemClickListener(item -> {
                switch (item.getItemId()) {
                  case R.id.item_menu_cancel:
                    CompletableFuture cancelFuture = favorViewModel.cancelFavor(favor, favor.getStatusId() == FavorStatus.REQUESTED.toInt());
                    cancelFuture.thenAccept(o -> CommonTools.showSnackbar(parentView, context.getResources().getString(R.string.favor_cancel_success_msg_listView)));
                    cancelFuture.exceptionally(onFailedResult(context, parentView));
                  case R.id.item_menu_edit:
//                    Bundle favorBundle = new Bundle();
//                    favorBundle.putString("FAVOR_ARGS", favor.getId());
//                    Navigation.findNavController(view)
//                            .navigate(R.id.action_nav_favorlist_to_favorPublishedView, favorBundle);
                    favor.setStatusIdToInt(FavorStatus.EDIT);
                    Bundle favorBundle = new Bundle();
                    favorBundle.putParcelable(CommonTools.FAVOR_VALUE_ARGS, favor);
                    favorBundle.putString(
                            CommonTools.FAVOR_SOURCE, context.getResources().getString(R.string.favor_source_publishedFavor));
                    findNavController((Activity) context, R.id.nav_host_fragment)
                            .navigate(R.id.action_global_favorEditingView, favorBundle);
                  case R.id.item_menu_commit:
                    assert !currentUserIsRequester;
                    favorViewModel.commitFavor(favor, false)
                            .whenComplete(
                            (aVoid, throwable) -> {
                              if (throwable != null) handleException(throwable, parentView, context.getResources());
                              else CommonTools.showSnackbar(parentView, context.getResources().getString(R.string.favor_respond_success_msg));
                            });
                }
                return false;
              });
      popup.show();
    });
  }

  @RequiresApi(api = Build.VERSION_CODES.N)
  private Function onFailedResult(Context context, View currentView) {
    return (exception) -> {
      if (((CompletionException) exception).getCause() instanceof IllegalRequestException)
        CommonTools.showSnackbar(currentView, context.getResources().getString(R.string.illegal_request_error));
      else CommonTools.showSnackbar(currentView, context.getResources().getString(R.string.update_favor_error));
      Log.e(TAG, Objects.requireNonNull(((Exception) exception).getMessage()));
      return null;
    };
  }

  private void handleException(Throwable throwable, View parentView, Resources resoures) {
    Throwable cause =
            (throwable.getCause() == null) ? new Exception(throwable) : throwable.getCause();
    if (cause instanceof IllegalRequestException) {
      CommonTools.showSnackbar(parentView, resoures.getString(R.string.illegal_request_error));
    } else if (cause instanceof IllegalAcceptException) {
      CommonTools.showSnackbar(parentView, resoures.getString(R.string.illegal_accept_error));
    } else {
      CommonTools.showSnackbar(parentView, resoures.getString(R.string.update_favor_error));
    }
    if (throwable.getMessage() != null) Log.e(TAG, throwable.getMessage());
  }
}
