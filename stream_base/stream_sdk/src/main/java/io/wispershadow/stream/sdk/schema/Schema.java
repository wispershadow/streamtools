package io.wispershadow.stream.sdk.schema;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

public class Schema implements Serializable {
    private static final long serialVersionUID = 1L;
    private String name;
    private List<Column> columns;
    private List<String> keyColumnNames;
    private List<Index> indices;
    private Map<String, String> properties;

    public Schema() {
    }

    public Schema(String name, List<Column> columns) {
        this(name, columns, null, null);
    }

    public Schema(String name, List<Column> columns, List<String> keyColumnNames) {
        this(name, columns, keyColumnNames, null);
    }

    public Schema(String name, List<Column> columns, List<String> keyColumnNames, List<Index> indices) {
        validateKeyColumnNames(keyColumnNames, columns);
        validateIndexColumnNames(indices, columns);
        this.name = name;
        this.columns = columns;
        this.keyColumnNames = keyColumnNames;
        this.indices = indices;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<Column> getColumns() {
        return columns;
    }

    public void setColumns(List<Column> columns) {
        this.columns = columns;
    }

    public List<String> getKeyColumnNames() {
        return keyColumnNames;
    }

    public List<Column> getKeyColumns() {
        if (keyColumnNames == null || keyColumnNames.isEmpty()) {
            return Collections.emptyList();
        }
        return keyColumnNames.stream().map(
                (cn) -> {
                    return columns.stream().filter( (c) -> {
                        return c.getName().equals(cn);
                    }).findFirst().get();
                })
                .collect(Collectors.toList());
    }

    public List<Index> getIndices() {
        return indices;
    }

    public void setIndices(List<Index> indices) {
        this.indices = indices;
    }

    public List<List<Column>> getIndexColumns() {
        if (indices == null || indices.isEmpty()) {
            return Collections.emptyList();
        }
        return indices.stream().map(
                (i) -> {
                    return i.indexColumnNames.stream().map(
                            (cn) -> {
                                return columns.stream().filter( (c) -> {
                                    return c.getName().equals(cn);
                                }).findFirst().get();
                            }
                    ).collect(Collectors.toList());
                }
        ).collect(Collectors.toList());
    }

    public void setKeyColumnNames(List<String> keyColumnNames) {
        validateKeyColumnNames(keyColumnNames, columns);
        this.keyColumnNames = keyColumnNames;
    }

    public Map<String, String> getProperties() {
        return properties;
    }

    public void setProperties(Map<String, String> properties) {
        this.properties = properties;
    }

    private static void validateKeyColumnNames(List<String> keyColumnNames, List<Column> columns) {
        if (keyColumnNames == null || keyColumnNames.isEmpty()) {
            return;
        }
        for (String keyColumnName : keyColumnNames) {
            boolean found = columns.stream().anyMatch( (c) -> { return c.getName().equals(keyColumnName); });
            if (!found) {
                throw new IllegalArgumentException("Invalid key column with name: " + keyColumnName);
            }
        }
    }

    private static void validateIndexColumnNames(List<Index> indices, List<Column> columns) {
        if (indices == null || indices.isEmpty()) {
            return;
        }
        for (Index index : indices) {
            for (String indexColumnName: index.indexColumnNames) {
                boolean found = columns.stream().anyMatch( (c) -> { return c.getName().equals(indexColumnName); });
                if (!found) {
                    throw new IllegalArgumentException("Invalid index column with name: " + indexColumnName);
                }
            }
        }
    }

    @Override
    public String toString() {
        return "Schema{" +
                "name='" + name + '\'' +
                ", columns=" + columns +
                ", keyColumnNames=" + keyColumnNames +
                ", indices=" + indices +
                '}';
    }

    public static class Index implements Serializable {
        private static final long serialVersionUID = 1L;
        private List<String> indexColumnNames;
        private List<Boolean> indexColumnsAscending;

        public Index() {
        }

        public Index(List<String> indexColumnNames, List<Boolean> indexColumnsAscending) {
            this.indexColumnNames = indexColumnNames;
            this.indexColumnsAscending = indexColumnsAscending;
        }

        public static Index of(List<String> indexColumnNames) {
            if (indexColumnNames == null || indexColumnNames.isEmpty()) {
                throw new IllegalArgumentException("Index column names can not be empty");
            }
            List<Boolean> indexColumnsAscending = new ArrayList<Boolean>(indexColumnNames.size());
            for (int i = 0; i<indexColumnNames.size(); i++) {
                indexColumnsAscending.add(Boolean.TRUE);
            }
            return new Index(indexColumnNames, indexColumnsAscending);
        }

        public static Index of(String... indexColumNames) {
            return Index.of(Arrays.asList(indexColumNames));
        }

        public static Index of(List<String> indexColumnNames, List<Boolean> indexColumnsAscending) {
            if (indexColumnNames == null || indexColumnNames.isEmpty()) {
                throw new IllegalArgumentException("Index column names can not be empty");
            }
            if (indexColumnsAscending == null || indexColumnsAscending.isEmpty()) {
                throw new IllegalArgumentException("Index column ascendings can not be empty");
            }
            if (indexColumnNames.size() != indexColumnsAscending.size()) {
                throw new IllegalArgumentException("Unmatched number of columns: " + indexColumnNames.size()
                + " number of column ascending specification: " + indexColumnsAscending.size());
            }
            return new Index(indexColumnNames, indexColumnsAscending);
        }

        public static Index of (String[] indexColumnNames, Boolean[] indexColumnsAscending) {
            return Index.of(Arrays.asList(indexColumnNames), Arrays.asList(indexColumnsAscending));
        }

        public List<String> getIndexColumnNames() {
            return indexColumnNames;
        }

        public void setIndexColumnNames(List<String> indexColumnNames) {
            this.indexColumnNames = indexColumnNames;
        }

        public List<Boolean> getIndexColumnsAscending() {
            return indexColumnsAscending;
        }

        public void setIndexColumnsAscending(List<Boolean> indexColumnsAscending) {
            this.indexColumnsAscending = indexColumnsAscending;
        }

        @Override
        public String toString() {
            return "Index{" +
                    "indexColumnNames=" + indexColumnNames +
                    ", indexColumnsAscending=" + indexColumnsAscending +
                    '}';
        }
    }

    public static class Column implements Serializable {
        private static final long serialVersionUID = 1L;
        private String name;
        private String type;
        private Object defaultValue;
        private boolean nullable;

        public Column() {
        }

        private Column(String name, String type, Object defaultValue, boolean nullable) {
            this.name = Objects.requireNonNull(name, "Column name must not be null");
            this.type = Objects.requireNonNull(type, "Column type must not be null");
            this.defaultValue = defaultValue;
            this.nullable = nullable;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

        public Object getDefaultValue() {
            return defaultValue;
        }

        public void setDefaultValue(Object defaultValue) {
            this.defaultValue = defaultValue;
        }

        public boolean isNullable() {
            return nullable;
        }

        public void setNullable(boolean nullable) {
            this.nullable = nullable;
        }

        public static Column of(String name, String type) {
            return new Column(name, type, null, true);
        }

        public static Column of(String name, String type, boolean nullable) {
            return new Column(name, type, null, nullable);
        }

        public static Column of(String name, String type, boolean nullable, Object defaultValue) {
            return new Column(name, type, defaultValue, nullable);
        }

        public static <T> Column of(String name, Class<T> type) {
            return new Column(name, type.getName(), null, true);
        }

        public static <T> Column of(String name, Class<T> type, boolean nullable) {
            return new Column(name, type.getName(), null, nullable);
        }

        public static <T> Column of(String name, Class<T> type, boolean nullable, Object defaultValue) {
            return new Column(name, type.getName(), defaultValue, nullable);
        }


        @Override
        public String toString() {
            return "Column{" +
                    "name='" + name + '\'' +
                    ", type='" + type + '\'' +
                    ", defaultValue=" + defaultValue +
                    ", nullable=" + nullable +
                    '}';
        }
    }
}
