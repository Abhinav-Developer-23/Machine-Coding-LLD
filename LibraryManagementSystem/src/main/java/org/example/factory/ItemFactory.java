package org.example.factory;

import org.example.enums.ItemType;
import org.example.models.Book;
import org.example.models.LibraryItem;
import org.example.models.Magazine;

public class ItemFactory {
    public static LibraryItem createItem(ItemType type, String id, String title, String author) {
        return switch (type) {
            case BOOK -> new Book(id, title, author);
            case MAGAZINE -> new Magazine(id, title, author); // Author might be publisher here
            default -> throw new IllegalArgumentException("Unknown item type.");
        };
    }
}
