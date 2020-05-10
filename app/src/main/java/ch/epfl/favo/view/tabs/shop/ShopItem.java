package ch.epfl.favo.view.tabs.shop;

import java.util.Date;

class ShopItem {
  final int resourceId;
  final int quantity;
  final double price;
  final Date expiration;

  ShopItem(int resourceId, int quantity, double price, Date expiration) {
    this.resourceId = resourceId;
    this.quantity = quantity;
    this.price = price;
    this.expiration = expiration;
  }
}
