package atm;

import atm.dispenser.CashDispenser;
import atm.dispenser.DispenseChain;
import atm.dispenser.NoteDispenser100;
import atm.dispenser.NoteDispenser20;
import atm.dispenser.NoteDispenser50;
import atm.entities.BankService;
import atm.entities.Card;
import atm.enums.OperationType;
import atm.states.ATMState;
import atm.states.IdleState;
import lombok.Getter;
import lombok.Setter;

public class ATM {
  private static ATM INSTANCE;
  @Getter private final BankService bankService;
  private final CashDispenser cashDispenser;
  private ATMState currentState;
  @Getter @Setter private Card currentCard;

  private ATM() {
    this.currentState = new IdleState();
    this.bankService = new BankService();

    // Set up the dispenser chain
    DispenseChain c1 = new NoteDispenser100(10); // 10 x $100 notes
    DispenseChain c2 = new NoteDispenser50(20); // 20 x $50 notes
    DispenseChain c3 = new NoteDispenser20(30); // 30 x $20 notes
    c1.setNextChain(c2);
    c2.setNextChain(c3);
    this.cashDispenser = new CashDispenser(c1);
  }

  public static ATM getInstance() {
    if (INSTANCE == null) {
      INSTANCE = new ATM();
    }
    return INSTANCE;
  }

  public void changeState(ATMState newState) {
    this.currentState = newState;
  }

  public void insertCard(String cardNumber) {
    currentState.insertCard(this, cardNumber);
  }

  public void enterPin(String pin) {
    currentState.enterPin(this, pin);
  }

  public void selectOperation(OperationType op, int amount) {
    currentState.selectOperation(this, op, amount);
  }

  public void checkBalance() {
    double balance = bankService.getBalance(currentCard);
    System.out.printf("Your current account balance is: $%.2f%n", balance);
  }

  public void withdrawCash(int amount) {
    if (!cashDispenser.canDispenseCash(amount)) {
      throw new IllegalStateException("Insufficient cash available in the ATM.");
    }

    bankService.withdrawMoney(currentCard, amount);

    try {
      cashDispenser.dispenseCash(amount);
    } catch (Exception e) {
      bankService.depositMoney(currentCard, amount); // Deposit back if dispensing fails
    }
  }

  public void depositCash(int amount) {
    bankService.depositMoney(currentCard, amount);
  }
}
