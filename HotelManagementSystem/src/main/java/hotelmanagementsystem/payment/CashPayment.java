package hotelmanagementsystem.payment;

public class CashPayment implements Payment {

  @Override
  public boolean processPayment(double amount) {
    // Process cash payment
    System.out.println("Processing cash payment of $" + amount);
    return true;
  }
}
