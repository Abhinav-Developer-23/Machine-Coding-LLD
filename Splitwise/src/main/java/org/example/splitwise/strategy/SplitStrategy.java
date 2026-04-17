package org.example.splitwise.strategy;

import java.util.List;
import org.example.splitwise.entities.Split;
import org.example.splitwise.entities.User;

public interface SplitStrategy {
  List<Split> calculateSplits(
      double totalAmount, User paidBy, List<User> participants, List<Double> splitValues);
}
