package org.example.models;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.example.enums.SeatStatus;
import org.example.enums.SeatType;

@Getter
@AllArgsConstructor
public class Seat {
    private final String id;
    private final int row;
    private final int col;
    private final SeatType type;

    @Setter
    private SeatStatus status;

    public Seat(String id, int row, int col, SeatType type) {
        this.id = id;
        this.row = row;
        this.col = col;
        this.type = type;
        this.status = SeatStatus.AVAILABLE;
    }
}
