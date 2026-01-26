package org.example.service;

import org.example.model.Column;
import org.example.model.Row;
import org.example.model.Table;

import java.util.List;
import java.util.Map;

/**
 * Interface for database operations.
 */
public interface DatabaseService {

    Table createTable(String tableName, List<Column> columns);

    void deleteTable(String tableName);

    Row insertRecord(String tableName, Map<String, Object> values);

    void printAllRecords(String tableName);

    void printFilteredRecords(String tableName, Map<String, Object> filters);
}
