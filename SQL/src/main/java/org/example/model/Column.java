package org.example.model;

import lombok.Getter;
import org.example.constraint.Constraint;
import org.example.enums.ColumnType;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents a column definition in a table.
 * Uses Builder Pattern for flexible object construction.
 */
@Getter
public class Column {
    
    private final String name;
    private final ColumnType type;
    private final boolean required;
    private final List<Constraint> constraints;

    private Column(Builder builder) {
        this.name = builder.name;
        this.type = builder.type;
        this.required = builder.required;
        this.constraints = new ArrayList<>(builder.constraints);
    }

    /**
     * Validates a value against this column's type and constraints.
     */
    public void validateValue(Object value) {
        if (required && value == null) {
            throw new RuntimeException("Column '" + name + "' is required and cannot be null");
        }
        
        if (value == null) {
            return;
        }
        
        validateType(value);
        
        for (Constraint constraint : constraints) {
            if (!constraint.validate(value)) {
                throw new RuntimeException("Column '" + name + "' constraint violated: " 
                        + constraint.getDescription() + ". Value: " + value);
            }
        }
    }

    private void validateType(Object value) {
        switch (type) {
            case STRING:
                if (!(value instanceof String)) {
                    throw new RuntimeException("Column '" + name + "' expects String type, got: " 
                            + value.getClass().getSimpleName());
                }
                break;
            case INT:
                if (!(value instanceof Integer)) {
                    throw new RuntimeException("Column '" + name + "' expects Integer type, got: " 
                            + value.getClass().getSimpleName());
                }
                break;
            default:
                throw new RuntimeException("Unsupported column type: " + type);
        }
    }

    @Override
    public String toString() {
        return "Column{name='" + name + "', type=" + type + ", required=" + required + "}";
    }

    /**
     * Builder class for creating Column instances.
     */
    @Getter
    public static class Builder {
        private String name;
        private ColumnType type;
        private boolean required = false;
        private List<Constraint> constraints = new ArrayList<>();

        public Builder(String name, ColumnType type) {
            if (name == null || name.trim().isEmpty()) {
                throw new RuntimeException("Column name cannot be null or empty");
            }
            if (type == null) {
                throw new RuntimeException("Column type cannot be null");
            }
            this.name = name;
            this.type = type;
        }

        public Builder required() {
            this.required = true;
            return this;
        }

        public Builder addConstraint(Constraint constraint) {
            if (constraint != null) {
                this.constraints.add(constraint);
            }
            return this;
        }

        public Column build() {
            return new Column(this);
        }
    }
}
