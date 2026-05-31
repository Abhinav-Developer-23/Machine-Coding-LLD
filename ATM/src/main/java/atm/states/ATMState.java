package atm.states;

import atm.ATM;
import atm.enums.OperationType;

public interface ATMState {
  void insertCard(ATM atm, String cardNumber);

  void enterPin(ATM atm, String pin);

  void selectOperation(ATM atm, OperationType op, int amount);

  void ejectCard(ATM atm);
}
