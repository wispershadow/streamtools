package io.wispershadow.stream.sdk.schema;

import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class SchemaTest {
    @Test
    public void testCreateSchema() {
        List<Schema.Column> columns = new ArrayList<>();
        columns.add(Schema.Column.of("tid", "Long", false));
        columns.add(Schema.Column.of("accountId", String.class, false));
        columns.add(Schema.Column.of("blackList", "Boolean", false, Boolean.FALSE));
        columns.add(Schema.Column.of("tranDate", Date.class, false));
        List<String> keyColumnsErr = Arrays.asList(new String[] {"accountId", "transactionId"});
        List<String> keyColumns = Arrays.asList(new String[] {"tid"});
        List<Schema.Index> indicesErr = Arrays.asList(new Schema.Index[] {
                Schema.Index.of("blackList"), Schema.Index.of("tranDate", "acctId")
        });
        List<Schema.Index> indices = Arrays.asList(new Schema.Index[] {
                Schema.Index.of("blackList"), Schema.Index.of(Arrays.asList(new String[]{"tranDate", "accountId"}),
                Arrays.asList(new Boolean[]{Boolean.TRUE, Boolean.FALSE}))
        });
        try {
            Schema schema = new Schema("Transaction", columns, keyColumnsErr);
            Assert.fail();
        }
        catch (IllegalArgumentException iae) {
            Assert.assertTrue(iae.getMessage().contains("transactionId"));
        }

        try {
            Schema schema = new Schema("Transaction", columns, keyColumns, indicesErr);
            Assert.fail();
        }
        catch (IllegalArgumentException iae) {
            Assert.assertTrue(iae.getMessage().contains("acctId"));
        }

        Schema schema = new Schema("Transaction", columns);
        schema = new Schema("Transaction", columns, keyColumns, indices);
        Assert.assertEquals(schema.getColumns().size(), 4);
        Assert.assertEquals(schema.getKeyColumnNames(), Arrays.asList("tid"));
        validateColumns(schema.getKeyColumns(), Arrays.asList("tid|Long|false|null"));
        validateIndex(schema.getIndices(), Arrays.asList("blackList|true", "tranDate|accountId|true|false"));
        List<List<Schema.Column>> indexColumns = schema.getIndexColumns();
        Assert.assertEquals(indexColumns.size(), 2);
        validateColumns(indexColumns.get(0), Arrays.asList("blackList|Boolean|false|false"));
        validateColumns(indexColumns.get(1), Arrays.asList("tranDate|java.util.Date|false|null",
                "accountId|java.lang.String|false|null"));
    }

    private static void validateColumns(List<Schema.Column> columns, List<String> columnValues) {
        List<String> columnValuesToString = columns.stream().map( (c) -> {
            return String.join("|", c.getName(), c.getType(), String.valueOf(c.isNullable()),
                    Optional.ofNullable(c.getDefaultValue()).orElse("null").toString());
        } ).collect(Collectors.toList());
        Assert.assertEquals(columnValuesToString, columnValues);
    }

    private static void validateIndex(List<Schema.Index> indices, List<String> indexValues) {
        List<String> indexValuesToString = indices.stream().map( (i) -> {
            String result = String.join("|", i.getIndexColumnNames()) + "|";
            result += String.join("|",
                    i.getIndexColumnsAscending().stream().map(Object::toString).collect(Collectors.toList()));
            return result;
        }).collect(Collectors.toList());
        Assert.assertEquals(indexValuesToString, indexValues);
    }

}
