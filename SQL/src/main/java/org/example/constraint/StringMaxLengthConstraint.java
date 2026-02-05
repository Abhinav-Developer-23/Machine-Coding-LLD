package org.example.constraint;

import lombok.Getter;

/**
 * Constraint that validates string values do not exceed a maximum length. Default maximum length is
 * 20 characters.
 */
@Getter
public class StringMaxLengthConstraint implements Constraint {

  private final int maxLength;

  public StringMaxLengthConstraint(int maxLength) {
    if (maxLength <= 0) {
      throw new RuntimeException("Max length must be positive");
    }
    this.maxLength = maxLength;
  }

  @Override
  public boolean validate(Object value) {
    if (value == null) {
      return true;
    }
    if (value instanceof String) {
      return ((String) value).length() <= maxLength;
    }
    return true;
  }
}
