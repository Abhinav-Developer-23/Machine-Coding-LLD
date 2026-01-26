package org.example.constraint;

/**
 * Interface defining the contract for column constraints.
 * Follows the Strategy Pattern - different validation strategies can be implemented.
 */
public interface Constraint {
    
    /**
     * Validates the given value against this constraint.
     * 
     * @param value The value to validate
     * @return true if the value satisfies the constraint, false otherwise
     */
    boolean validate(Object value);
    
    /**
     * Gets a human-readable description of this constraint.
     * 
     * @return Description of the constraint
     */
    String getDescription();
}
