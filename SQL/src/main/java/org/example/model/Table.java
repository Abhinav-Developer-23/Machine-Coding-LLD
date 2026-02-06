package org.example.model;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import lombok.Getter;

/** Represents a database table containing columns and rows. */
@Getter
public class Table {

  private final String name;
  private final Map<String, Column> columns;
  private final List<Row> rows;

  public Table(String name) {
    if (name == null || name.trim().isEmpty()) {
      throw new RuntimeException("Table name cannot be null or empty");
    }
    this.name = name;
    this.columns = new LinkedHashMap<>();
    this.rows = new ArrayList<>();
  }

  public Table(String name, List<Column> columns) {
    this(name);
    for (Column column : columns) {
      addColumn(column);
    }
  }

  public void addColumn(Column column) {
    if (columns.containsKey(column.getName())) {
      throw new RuntimeException(
          "Column '" + column.getName() + "' already exists in table '" + name + "'");
    }
    columns.put(column.getName(), column);
  }

  public Row insertRow(Map<String, Object> values) {
    for (String columnName : values.keySet()) {
      if (!columns.containsKey(columnName)) {
        throw new RuntimeException("Column '" + columnName + "' not found in table '" + name + "'");
      }
    }

    for (Column column : columns.values()) {
      Object value = values.get(column.getName());
      column.validateValue(value);
    }

    Row row = new Row(values);
    rows.add(row);
    return row;
  }

  /** Filters rows by multiple column-value conditions (AND logic). */
  public List<Row> filterRows(Map<String, Object> filters) {
    // Validate all filter columns exist
    for (String columnName : filters.keySet()) {
      if (!columns.containsKey(columnName)) {
        throw new RuntimeException("Column '" + columnName + "' not found in table '" + name + "'");
      }
    }

    List<Row> result = new ArrayList<>();
    for (Row row : rows) {
      boolean matches = true;
      for (var filter : filters.entrySet()) {
        String columnName = filter.getKey();
        Object filterValue = filter.getValue();
        Object rowValue = row.getValue(columnName);

        if (filterValue == null) {
          if (rowValue != null) {
            matches = false;
            break;
          }
        } else {
          if (!filterValue.equals(rowValue)) {
            matches = false;
            break;
          }
        }
      }
      if (matches) {
        result.add(row);
      }
    }
    return result;
  }

  public void printAllRecords() {
    System.out.println("\n=== Table: " + name + " ===");

    if (rows.isEmpty()) {
      System.out.println("(No records)");
      return;
    }

    for (Row row : rows) {
      System.out.println(row.toString());
    }
    System.out.println("Total records: " + rows.size());
  }

  @Override
  public String toString() {
    return "Table{name='" + name + "', columns=" + columns.size() + ", rows=" + rows.size() + "}";
  }
}
