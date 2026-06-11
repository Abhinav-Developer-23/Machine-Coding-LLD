package org.example.states;

import org.example.models.BookCopy;
import org.example.models.Member;

public interface ItemState {
    void checkout(BookCopy copy, Member member);
    void returnItem(BookCopy copy);
}
