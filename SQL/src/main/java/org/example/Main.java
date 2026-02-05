package org.example;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.example.constraint.IntMinValueConstraint;
import org.example.constraint.StringMaxLengthConstraint;
import org.example.enums.ColumnType;
import org.example.model.Column;
import org.example.service.DatabaseService;
import org.example.service.DatabaseServiceImpl;

/** Demo class for In-Memory SQL Database */
public class Main {

  public static void main(String[] args) {
    System.out.println("=== In-Memory SQL Database Demo ===\n");

    DatabaseService db = new DatabaseServiceImpl();

    try {
      // 1. Create 'employees' table
      List<Column> columns =
          Arrays.asList(
              new Column(
                  "id", ColumnType.INT, true, List.of(new IntMinValueConstraint(1024))), // min 1024
              new Column(
                  "name",
                  ColumnType.STRING,
                  true,
                  List.of(new StringMaxLengthConstraint(20))), // max 20 chars
              new Column(
                  "department",
                  ColumnType.STRING,
                  false,
                  List.of(new StringMaxLengthConstraint(20))),
              new Column(
                  "salary",
                  ColumnType.INT,
                  false,
                  List.of(new IntMinValueConstraint(5000))) // custom min
              );
      db.createTable("employees", columns);

      // 2. Insert records
      Map<String, Object> emp1 = new HashMap<>();
      emp1.put("id", 1024);
      emp1.put("name", "John Doe");
      emp1.put("department", "Engineering");
      emp1.put("salary", 75000);
      db.insertRecord("employees", emp1);

      Map<String, Object> emp2 = new HashMap<>();
      emp2.put("id", 1025);
      emp2.put("name", "Jane Smith");
      emp2.put("department", "Marketing");
      emp2.put("salary", 65000);
      db.insertRecord("employees", emp2);

      Map<String, Object> emp3 = new HashMap<>();
      emp3.put("id", 1026);
      emp3.put("name", "Bob Wilson");
      emp3.put("department", "Engineering");
      emp3.put("salary", 80000);
      db.insertRecord("employees", emp3);

      Map<String, Object> emp4 = new HashMap<>();
      emp4.put("id", 1027);
      emp4.put("name", "Alice Brown");
      emp4.put("department", "Engineering");
      emp4.put("salary", 75000);
      db.insertRecord("employees", emp4);

      // 3. Print all records
      db.printAllRecords("employees");

      // 4. Filter by single field
      Map<String, Object> filter1 = new HashMap<>();
      filter1.put("department", "Engineering");
      db.printFilteredRecords("employees", filter1);

      // 5. Filter by multiple fields (AND logic)
      Map<String, Object> filter2 = new HashMap<>();
      filter2.put("department", "Engineering");
      filter2.put("salary", 75000);
      db.printFilteredRecords("employees", filter2);

      // 6. Test constraints
      System.out.println("\n--- Testing Constraints ---");

      // String too long (> 20 chars)
      try {
        Map<String, Object> longName = new HashMap<>();
        longName.put("id", 1028);
        longName.put("name", "This name is way too long");
        db.insertRecord("employees", longName);
      } catch (RuntimeException e) {
        System.out.println("Caught: " + e.getMessage());
      }

      // Int below min (< 1024)
      try {
        Map<String, Object> lowId = new HashMap<>();
        lowId.put("id", 100);
        lowId.put("name", "Test User");
        db.insertRecord("employees", lowId);
      } catch (RuntimeException e) {
        System.out.println("Caught: " + e.getMessage());
      }

      // Missing required field
      try {
        Map<String, Object> missingName = new HashMap<>();
        missingName.put("id", 1030);
        missingName.put("department", "Sales");
        db.insertRecord("employees", missingName);
      } catch (RuntimeException e) {
        System.out.println("Caught: " + e.getMessage());
      }

      // 7. Delete table
      db.createTable("temp_table", Arrays.asList(new Column("col1", ColumnType.STRING)));
      db.deleteTable("temp_table");

      System.out.println("\n=== Demo Completed ===");

    } catch (RuntimeException e) {
      System.err.println("Error: " + e.getMessage());
    }
  }
}
