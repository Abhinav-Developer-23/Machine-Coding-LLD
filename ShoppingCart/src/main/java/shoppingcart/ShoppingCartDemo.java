package shoppingcart;

import java.util.Arrays;
import shoppingcart.discount.*;
import shoppingcart.entities.*;
import shoppingcart.enums.ProductCategory;
import shoppingcart.observer.CartEventLogger;
import shoppingcart.service.CouponManager;
import shoppingcart.service.ShoppingCartService;

public class ShoppingCartDemo {
  public static void main(String[] args) {
    ShoppingCartService cartService = ShoppingCartService.getInstance();

    // --- Setup products ---
    Product laptop =
        new Product("P001", "Laptop", 999.99, ProductCategory.ELECTRONICS, 2);
    Product phone =
        new Product("P002", "Smartphone", 699.99, ProductCategory.ELECTRONICS, 3);
    Product tShirt =
        new Product("P003", "T-Shirt", 29.99, ProductCategory.CLOTHING, 10);
    Product jeans =
        new Product("P004", "Jeans", 59.99, ProductCategory.CLOTHING, 5);
    Product novel =
        new Product("P005", "Java Design Patterns", 45.00, ProductCategory.BOOKS, 3);

    // --- Setup customer & cart ---
    Customer customer = new Customer("C001", "Alice", "alice@example.com");
    Cart cart = cartService.createCart(customer);
    cart.addObserver(new CartEventLogger());

    System.out.println("=== Adding Items ===");
    cart.addItem(laptop, 1);
    cart.addItem(phone, 2);
    cart.addItem(tShirt, 3);
    cart.addItem(jeans, 1);
    cart.addItem(novel, 1);

    System.out.println("\nSubtotal: $" + String.format("%.2f", cart.getSubtotal()));

    // --- Update quantity ---
    System.out.println("\n=== Updating T-Shirt Quantity to 5 ===");
    cart.updateItemQuantity("P003", 5);
    System.out.println("Subtotal: $" + String.format("%.2f", cart.getSubtotal()));

    // --- Save for later ---
    System.out.println("\n=== Saving Jeans for Later ===");
    cart.saveForLater("P004");
    System.out.println(
        "Items in cart: " + cart.getItems().size()
            + ", Saved items: " + cart.getSavedItems().size());

    // --- Move back to cart ---
    System.out.println("\n=== Moving Jeans Back to Cart ===");
    cart.moveToCart("P004");
    System.out.println("Items in cart: " + cart.getItems().size());

    // --- Apply percentage discount ---
    System.out.println("\n=== Applying 10% Discount ===");
    cart.applyDiscount(new PercentageDiscountStrategy(10));
    System.out.println("Total after 10% off: $" + String.format("%.2f", cart.getTotal()));

    // --- Apply flat discount ---
    System.out.println("\n=== Switching to $50 Flat Discount ===");
    cart.applyDiscount(new FlatAmountDiscountStrategy(50));
    System.out.println("Total after $50 off: $" + String.format("%.2f", cart.getTotal()));

    // --- Apply Buy 2 Get 1 Free on Clothing ---
    System.out.println("\n=== Switching to Buy 2 Get 1 Free (Clothing) ===");
    cart.applyDiscount(new BuyXGetYFreeStrategy(2, 1, ProductCategory.CLOTHING));
    System.out.println(
        "Total after B2G1 (Clothing): $" + String.format("%.2f", cart.getTotal()));

    // --- Apply composite discount ---
    System.out.println("\n=== Applying Composite: 5% Off + B2G1 Clothing ===");
    CompositeDiscountStrategy composite =
        new CompositeDiscountStrategy(
            Arrays.asList(
                new PercentageDiscountStrategy(5),
                new BuyXGetYFreeStrategy(2, 1, ProductCategory.CLOTHING)));
    cart.applyDiscount(composite);
    System.out.println("Total after composite: $" + String.format("%.2f", cart.getTotal()));

    // --- Coupon flow ---
    System.out.println("\n=== Coupon Redemption ===");
    CouponManager couponManager = new CouponManager();
    couponManager.registerCoupon(
        new Coupon(
            "SAVE20",
            new FlatAmountDiscountStrategy(20),
            System.currentTimeMillis() + 3600_000)); // valid for 1 hour
    DiscountStrategy couponDiscount = couponManager.redeemCoupon("SAVE20");
    cart.applyDiscount(couponDiscount);
    System.out.println(
        "Total after SAVE20 coupon: $" + String.format("%.2f", cart.getTotal()));

    // --- Remove item ---
    System.out.println("\n=== Removing Laptop ===");
    cart.removeItem("P001");
    System.out.println("Total: $" + String.format("%.2f", cart.getTotal()));

    // --- Checkout ---
    System.out.println("\n=== Checkout ===");
    cart.removeDiscount();
    System.out.println("Final total (no discount): $" + String.format("%.2f", cart.getTotal()));
    cart.checkout();

    // --- Verify cart is locked after checkout ---
    System.out.println("\n=== Attempting to Modify Checked-Out Cart ===");
    try {
      cart.addItem(novel, 1);
    } catch (Exception e) {
      System.out.println("Expected error: " + e.getMessage());
    }

    // --- Create a second cart for the same customer (since first is checked out) ---
    System.out.println("\n=== Creating New Cart for Same Customer ===");
    Cart cart2 = cartService.createCart(customer);
    System.out.println("New cart ID: " + cart2.getId());
    cart2.addItem(novel, 2);
    System.out.println("New cart subtotal: $" + String.format("%.2f", cart2.getSubtotal()));
  }
}
