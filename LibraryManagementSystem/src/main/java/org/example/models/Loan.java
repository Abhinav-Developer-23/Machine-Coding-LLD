package org.example.models;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.time.LocalDate;

@Getter
@RequiredArgsConstructor
public class Loan {
    private final BookCopy copy;
    private final Member member;
    private final LocalDate checkoutDate = LocalDate.now();
}
