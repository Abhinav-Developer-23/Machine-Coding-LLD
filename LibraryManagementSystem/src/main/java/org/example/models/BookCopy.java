package org.example.models;

import lombok.Getter;
import lombok.Setter;

import org.example.states.AvailableState;
import org.example.states.ItemState;

public class BookCopy {
    @Getter private final String id;
    @Getter private final LibraryItem item;
    @Setter private ItemState currentState;

    public BookCopy(String id, LibraryItem item) {
        this.id = id;
        this.item = item;
        this.currentState = new AvailableState();
        item.addCopy(this);
    }

    public void checkout(Member member) { currentState.checkout(this, member); }
    public void returnItem() { currentState.returnItem(this); }

    public boolean isAvailable() { return currentState instanceof AvailableState; }
}
