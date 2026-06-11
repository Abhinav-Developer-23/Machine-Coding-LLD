package org.example.states;

import org.example.models.BookCopy;
import org.example.models.Member;
import org.example.services.TransactionService;

public class CheckedOutState implements ItemState {
    @Override
    public void checkout(BookCopy c, Member m) {
        System.out.println(c.getId() + " is already checked out.");
    }

    @Override
    public void returnItem(BookCopy copy) {
        TransactionService.getInstance().endLoan(copy);
        copy.setCurrentState(new AvailableState());
        System.out.println(copy.getId() + " returned.");
    }
}
