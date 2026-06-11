package org.example.models;

import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

public abstract class LibraryItem {
    @Getter private final String id;
    @Getter private final String title;
    @Getter protected final List<BookCopy> copies = new ArrayList<>();

    public LibraryItem(String id, String title) {
        this.id = id;
        this.title = title;
    }

    public void addCopy(BookCopy copy) { this.copies.add(copy); }

    public BookCopy getAvailableCopy() {
        return copies.stream()
                .filter(BookCopy::isAvailable)
                .findFirst()
                .orElse(null);
    }

    public abstract String getAuthorOrPublisher();
    public long getAvailableCopyCount() {
        return copies.stream().filter(BookCopy::isAvailable).count();
    }
}
