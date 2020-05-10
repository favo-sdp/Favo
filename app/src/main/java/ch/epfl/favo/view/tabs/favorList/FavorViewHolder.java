package ch.epfl.favo.view.tabs.favorList;

import android.content.Context;
import android.os.Build;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.PopupMenu;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.RecyclerView;
import androidx.fragment.app.Fragment;

import com.firebase.ui.firestore.paging.FirestorePagingAdapter;
import com.google.android.material.snackbar.Snackbar;

import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.function.Function;

import ch.epfl.favo.R;
import ch.epfl.favo.exception.IllegalRequestException;
import ch.epfl.favo.favor.Favor;
import ch.epfl.favo.util.CommonTools;
import ch.epfl.favo.util.DependencyFactory;
import ch.epfl.favo.viewmodel.FavorViewModel;
import ch.epfl.favo.viewmodel.FavorViewModel.*;
import ch.epfl.favo.viewmodel.IFavorViewModel;

class FavorViewHolder extends RecyclerView.ViewHolder {

  private String TAG = "FavorViewHolder";

  private TextView mTitleView;
  private TextView mDescriptionView;
  private TextView buttonViewOption;
  private IFavorViewModel favorViewModel;

  FavorViewHolder(@NonNull View itemView) {
    super(itemView);

    mTitleView = itemView.findViewById(R.id.item_title);
    mDescriptionView = itemView.findViewById(R.id.item_desc);
    buttonViewOption = itemView.findViewById(R.id.item_menu_btn);
  }

  @RequiresApi(api = Build.VERSION_CODES.N)
  void bind(Context context, @NonNull Favor favor, View parentView) {
    mTitleView.setText(favor.getTitle());
    mDescriptionView.setText(String.valueOf(favor.getDescription()));
    buttonViewOption.setOnClickListener(view -> {
      PopupMenu popup = new PopupMenu(context, buttonViewOption);
      popup.inflate(R.menu.item_menu);
      popup.setOnMenuItemClickListener(item -> {
                switch (item.getItemId()) {
                  case R.id.item_menu_cancel:
                    favorViewModel = (IFavorViewModel)
                            new ViewModelProvider((FragmentActivity) context)
                              .get(DependencyFactory.getCurrentViewModelClass());
                    CompletableFuture cancelFuture = favorViewModel.cancelFavor((Favor) favor.clone(), true);
                    cancelFuture.thenAccept(o -> CommonTools.showSnackbar(parentView, context.getResources().getString(R.string.favor_cancel_success_msg)));
                    cancelFuture.exceptionally(onFailedResult(context, parentView));
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
}
