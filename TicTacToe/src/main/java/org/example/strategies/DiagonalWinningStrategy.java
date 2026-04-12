package org.example.strategies;

import org.example.enums.Symbol;
import org.example.models.Board;

public class DiagonalWinningStrategy implements WinningStrategy {
    @Override
    public boolean checkWin(Board board, int row, int col, Symbol symbol) {
        int size = board.getSize();

        // Check main diagonal (top-left to bottom-right)
        boolean mainDiagonalWin = true;
        for (int i = 0; i < size; i++) {
            if (board.getCell(i, i).getSymbol() != symbol) {
                mainDiagonalWin = false;
                break;
            }
        }
        if (mainDiagonalWin) return true;

        // Check anti-diagonal (top-right to bottom-left)
        for (int i = 0; i < size; i++) {
            if (board.getCell(i, size - 1 - i).getSymbol() != symbol) {
                return false;
            }
        }
        return true;
    }
}
