package ch.epfl.favo.view.tabs.shop;

class ShopItem {
  final int resourceId;
  final int quantity;
  final int price;

  ShopItem(int resourceId, int quantity, int price) {
    this.resourceId = resourceId;
    this.quantity = quantity;
    this.price = price;
  }
}
