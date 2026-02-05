package org.example.model;

import java.util.ArrayList;
import java.util.List;
import lombok.Getter;
import org.example.constraint.Constraint;
import org.example.enums.ColumnType;

/** Represents a column definition in a table. */
@Getter
public class Column {

  private final String name;
  private final ColumnType type;
  private final boolean required;
  private final List<Constraint> constraints;

  public Column(String name, ColumnType type) {
    this(name, type, false, List.of());
  }

  public Column(String name, ColumnType type, boolean required, List<Constraint> constraints) {
    this.name = name;
    this.type = type;
    this.required = required;
    this.constraints = constraints == null ? new ArrayList<>() : new ArrayList<>(constraints);
  }

  /** Validates a value against this column's type and constraints. */
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
        throw new RuntimeException("Column '" + name + "' constraint violated. Value: " + value);
      }
    }
  }

  private void validateType(Object value) {
    switch (type) {
      case STRING:
        if (!(value instanceof String)) {
          throw new RuntimeException(
              "Column '"
                  + name
                  + "' expects String type, got: "
                  + value.getClass().getSimpleName());
        }
        break;
      case INT:
        if (!(value instanceof Integer)) {
          throw new RuntimeException(
              "Column '"
                  + name
                  + "' expects Integer type, got: "
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
}
