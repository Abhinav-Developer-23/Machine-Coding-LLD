package org.example.strategies;

import org.example.enums.Symbol;
import org.example.models.Board;

public class RowWinningStrategy implements WinningStrategy {
    @Override
    public boolean checkWin(Board board, int row, int col, Symbol symbol) {
        int size = board.getSize();
        for (int c = 0; c < size; c++) {
            if (board.getCell(row, c).getSymbol() != symbol) {
                return false;
            }
        }
        return true;
    }
}
