package ch.epfl.favo.view.tabs.favorList;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import ch.epfl.favo.R;
import ch.epfl.favo.favor.Favor;

class FavorViewHolder extends RecyclerView.ViewHolder {

  private TextView mTitleView;
  private TextView mDescriptionView;

  FavorViewHolder(@NonNull View itemView) {
    super(itemView);

    mTitleView = itemView.findViewById(R.id.item_title);
    mDescriptionView = itemView.findViewById(R.id.item_desc);
  }

  void bind(@NonNull Favor favor) {
    mTitleView.setText(favor.getTitle());
    mDescriptionView.setText(String.valueOf(favor.getDescription()));
  }
}
