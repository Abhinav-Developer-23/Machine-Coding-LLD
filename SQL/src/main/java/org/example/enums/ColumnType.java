package org.example.enums;

import lombok.Getter;

/**
 * Enum representing the supported column types in the database.
 * Currently supports STRING and INT types.
 */
@Getter
public enum ColumnType {
    STRING("String"),
    INT("Integer");

    private final String displayName;

    ColumnType(String displayName) {
        this.displayName = displayName;
    }

}
