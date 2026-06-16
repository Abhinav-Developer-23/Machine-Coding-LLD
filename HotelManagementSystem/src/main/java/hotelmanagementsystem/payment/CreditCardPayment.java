package hotelmanagementsystem.payment;

public class CreditCardPayment implements Payment {

  @Override
  public boolean processPayment(double amount) {
    // Process credit card payment
    System.out.println("Processing credit card payment of $" + amount);
    return true;
  }
}
