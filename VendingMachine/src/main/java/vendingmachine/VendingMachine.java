package vendingmachine;

import lombok.Getter;
import lombok.Setter;
import vendingmachine.entities.Inventory;
import vendingmachine.entities.Item;
import vendingmachine.enums.Coin;
import vendingmachine.states.IdleState;
import vendingmachine.states.VendingMachineState;

public class VendingMachine {
  private static final VendingMachine INSTANCE = new VendingMachine();
  // Getters for states and inventory
  @Getter private final Inventory inventory = new Inventory();
  private VendingMachineState currentState;
  @Getter private int balance = 0;
  @Setter private String selectedItemCode;

  public VendingMachine() {
    currentState = new IdleState(this);
  }

  public static VendingMachine getInstance() {
    return INSTANCE;
  }

  public void insertCoin(Coin coin) {
    currentState.insertCoin(coin);
  }

  public Item addItem(String code, String name, int price, int quantity) {
    Item item = new Item(code, name, price);
    inventory.addItem(code, item, quantity);
    return item;
  }

  public void selectItem(String code) {
    currentState.selectItem(code);
  }

  public void dispense() {
    currentState.dispense();
  }

  public void dispenseItem() {
    Item item = inventory.getItem(selectedItemCode);
    if (balance >= item.getPrice()) {
      inventory.reduceStock(selectedItemCode);
      balance -= item.getPrice();
      System.out.println("Dispensed: " + item.getName());
      if (balance > 0) {
        System.out.println("Returning change: " + balance);
      }
    }
    reset();
    setState(new IdleState(this));
  }

  public void refundBalance() {
    System.out.println("Refunding: " + balance);
    balance = 0;
  }

  public void reset() {
    selectedItemCode = null;
    balance = 0;
  }

  public void addBalance(int value) {
    balance += value;
  }

  public Item getSelectedItem() {
    return inventory.getItem(selectedItemCode);
  }

  public void setState(VendingMachineState state) {
    this.currentState = state;
  }
}
