package shoppingcart.entities;

public class CartItem {
  private final Product product;
  private int quantity;
  private final double priceAtAddition;

  public CartItem(Product product, int quantity, double priceAtAddition) {
    this.product = product;
    this.quantity = quantity;
    this.priceAtAddition = priceAtAddition;
  }

  public double getSubtotal() {
    return quantity * priceAtAddition;
  }

  public Product getProduct() {
    return product;
  }

  public int getQuantity() {
    return quantity;
  }

  public void setQuantity(int quantity) {
    this.quantity = quantity;
  }

  public double getPriceAtAddition() {
    return priceAtAddition;
  }
}
