package ch.epfl.favo.view.tabs.favorList;

import android.content.Context;
import android.view.MenuItem;
import android.view.View;
import android.widget.PopupMenu;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.concurrent.CompletableFuture;

import ch.epfl.favo.R;
import ch.epfl.favo.favor.Favor;

class FavorViewHolder extends RecyclerView.ViewHolder {

  private TextView mTitleView;
  private TextView mDescriptionView;
  private TextView buttonViewOption;

  FavorViewHolder(@NonNull View itemView) {
    super(itemView);

    mTitleView = itemView.findViewById(R.id.item_title);
    mDescriptionView = itemView.findViewById(R.id.item_desc);
    buttonViewOption = itemView.findViewById(R.id.item_menu_btn);
  }

  void bind(Context context, @NonNull Favor favor) {
    mTitleView.setText(favor.getTitle());
    mDescriptionView.setText(String.valueOf(favor.getDescription()));
    buttonViewOption.setOnClickListener(view -> {
      PopupMenu popup = new PopupMenu(context, buttonViewOption);
      popup.inflate(R.menu.item_menu);
      popup.setOnMenuItemClickListener(item -> {
        switch (item.getItemId()) {
          case R.id.item_menu_view:
            break;
          case R.id.item_menu_edit:
            break;
          case R.id.item_menu_cancel:
            break;
        }
        return false;
      });
      popup.show();
    });
  }
}
