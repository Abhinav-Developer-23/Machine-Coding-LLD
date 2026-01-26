package org.example.service;

import lombok.Getter;
import org.example.model.Column;
import org.example.model.Row;
import org.example.model.Table;
import org.example.repository.TableRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Implementation of DatabaseService interface.
 */
@Getter
public class DatabaseServiceImpl implements DatabaseService {

    private final TableRepository tableRepository;

    public DatabaseServiceImpl() {
        this.tableRepository = TableRepository.getInstance();
    }

    public DatabaseServiceImpl(TableRepository tableRepository) {
        this.tableRepository = tableRepository;
    }

    @Override
    public Table createTable(String tableName, List<Column> columns) {
        Table table = new Table(tableName, columns);
        tableRepository.createTable(table);
        System.out.println("Table '" + tableName + "' created with " + columns.size() + " columns.");
        return table;
    }

    @Override
    public void deleteTable(String tableName) {
        tableRepository.deleteTable(tableName);
        System.out.println("Table '" + tableName + "' deleted.");
    }

    @Override
    public Row insertRecord(String tableName, Map<String, Object> values) {
        Table table = tableRepository.getTable(tableName);
        Row row = table.insertRow(values);
        System.out.println("Record inserted into table '" + tableName + "'.");
        return row;
    }

    @Override
    public void printAllRecords(String tableName) {
        Table table = tableRepository.getTable(tableName);
        table.printAllRecords();
    }

    @Override
    public void printFilteredRecords(String tableName, Map<String, Object> filters) {
        Table table = tableRepository.getTable(tableName);
        List<Row> filteredRows = table.filterRows(filters);
        
        System.out.println("\n=== Filtered Records from Table: " + tableName + " ===");
        System.out.println("Filters: " + filters);
        
        if (filteredRows.isEmpty()) {
            System.out.println("(No matching records)");
            return;
        }
        
        printHeader(table);
        printSeparator(table);
        
        for (Row row : filteredRows) {
            System.out.println(row.toFormattedString(new ArrayList<>(table.getColumns().values())));
        }
        
        printSeparator(table);
        System.out.println("Matching records: " + filteredRows.size());
    }

    private void printHeader(Table table) {
        StringBuilder sb = new StringBuilder();
        sb.append("| ");
        for (Column column : table.getColumns().values()) {
            sb.append(String.format("%-20s | ", column.getName() + " (" + column.getType() + ")"));
        }
        System.out.println(sb.toString());
    }

    private void printSeparator(Table table) {
        StringBuilder sb = new StringBuilder();
        sb.append("+");
        for (int i = 0; i < table.getColumns().size(); i++) {
            sb.append("-".repeat(22));
            sb.append("+");
        }
        System.out.println(sb.toString());
    }
}
