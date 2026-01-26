package org.example.model;

import lombok.Getter;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Represents a single row (record) in a table.
 */
@Getter
public class Row {
    
    private final String id;
    private final Map<String, Object> values;

    public Row() {
        this.id = UUID.randomUUID().toString();
        this.values = new HashMap<>();
    }

    public Row(Map<String, Object> values) {
        this.id = UUID.randomUUID().toString();
        this.values = new HashMap<>(values);
    }

    public Object getValue(String columnName) {
        return values.get(columnName);
    }

    public void setValue(String columnName, Object value) {
        values.put(columnName, value);
    }

    @Override
    public String toString() {
        return "Row{id='" + id + "', values=" + values + "}";
    }

    public String toFormattedString(List<Column> columns) {
        StringBuilder sb = new StringBuilder();
        sb.append("| ");
        for (Column column : columns) {
            Object value = values.get(column.getName());
            String displayValue = value == null ? "NULL" : value.toString();
            sb.append(String.format("%-20s | ", displayValue));
        }
        return sb.toString();
    }
}
