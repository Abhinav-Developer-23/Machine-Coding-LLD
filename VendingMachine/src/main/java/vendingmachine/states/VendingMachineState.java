package vendingmachine.states;

import vendingmachine.VendingMachine;
import vendingmachine.enums.Coin;

public abstract class VendingMachineState {
  protected VendingMachine machine;

  public VendingMachineState(VendingMachine machine) {
    this.machine = machine;
  }

  public abstract void insertCoin(Coin coin);

  public abstract void selectItem(String code);

  public abstract void dispense();

  public abstract void refund();
}
