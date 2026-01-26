package org.example.repository;

import lombok.Getter;
import org.example.model.Table;

import java.util.HashMap;
import java.util.Map;

/**
 * Singleton repository for managing tables in the database.
 * Uses HashMap for O(1) table lookup operations.
 * Uses Double-Checked Locking for thread-safe lazy initialization.
 */
@Getter
public class TableRepository {
    
    private static volatile TableRepository instance;
    private final Map<String, Table> tables;

    private TableRepository() {
        this.tables = new HashMap<>();
    }

    public static TableRepository getInstance() {
        if (instance == null) {
            synchronized (TableRepository.class) {
                if (instance == null) {
                    instance = new TableRepository();
                }
            }
        }
        return instance;
    }

    public void createTable(Table table) {
        if (tables.containsKey(table.getName())) {
            throw new RuntimeException("Table already exists: " + table.getName());
        }
        tables.put(table.getName(), table);
    }

    public Table getTable(String tableName) {
        Table table = tables.get(tableName);
        if (table == null) {
            throw new RuntimeException("Table not found: " + tableName);
        }
        return table;
    }

    public void deleteTable(String tableName) {
        if (!tables.containsKey(tableName)) {
            throw new RuntimeException("Table not found: " + tableName);
        }
        tables.remove(tableName);
    }
}
