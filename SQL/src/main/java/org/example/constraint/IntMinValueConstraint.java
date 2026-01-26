package org.example.constraint;

import lombok.Getter;

/**
 * Constraint that validates integer values are not below a minimum value.
 * Default minimum value is 1024.
 */
@Getter
public class IntMinValueConstraint implements Constraint {
    
    public static final int DEFAULT_MIN_VALUE = 1024;
    private final int minValue;

    public IntMinValueConstraint() {
        this.minValue = DEFAULT_MIN_VALUE;
    }

    public IntMinValueConstraint(int minValue) {
        this.minValue = minValue;
    }

    @Override
    public boolean validate(Object value) {
        if (value == null) {
            return true;
        }
        if (value instanceof Integer) {
            return (Integer) value >= minValue;
        }
        return true;
    }

    @Override
    public String getDescription() {
        return "Minimum value: " + minValue;
    }
}
