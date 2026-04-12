package org.example.strategies;

import org.example.enums.Symbol;
import org.example.models.Board;

public interface WinningStrategy {
  boolean checkWin(Board board, int row, int col, Symbol symbol);
}
