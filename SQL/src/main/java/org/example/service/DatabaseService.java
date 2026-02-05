package org.example.service;

import java.util.List;
import java.util.Map;
import org.example.model.Column;
import org.example.model.Row;
import org.example.model.Table;

/** Interface for database operations. */
public interface DatabaseService {

  Table createTable(String tableName, List<Column> columns);

  void deleteTable(String tableName);

  Row insertRecord(String tableName, Map<String, Object> values);

  void printAllRecords(String tableName);

  void printFilteredRecords(String tableName, Map<String, Object> filters);
}
