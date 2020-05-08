package ch.epfl.favo.view.tabs.shop;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import ch.epfl.favo.R;

class ShopItemViewHolder extends RecyclerView.ViewHolder {

  final ImageView imageView;
  final TextView quantityView;
  final TextView priceView;
  final TextView expirationView;

  ShopItemViewHolder(@NonNull View view) {
    super(view);
    imageView = view.findViewById(R.id.item_image);
    quantityView = view.findViewById(R.id.item_quantity);
    priceView = view.findViewById(R.id.item_price);
    expirationView = view.findViewById(R.id.item_expiration);
  }
}
