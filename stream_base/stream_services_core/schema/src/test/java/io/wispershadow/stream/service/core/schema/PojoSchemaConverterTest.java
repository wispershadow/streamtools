package io.wispershadow.stream.service.core.schema;

import io.wispershadow.stream.sdk.schema.Schema;
import io.wispershadow.stream.service.core.schema.impl.PojoSchemaConverter;
import org.junit.Assert;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class PojoSchemaConverterTest {
    @Test
    public void testPojoSchemaConverter() {
        PojoSchemaConverter<ChildPojo> pojoPojoSchemaConverter = new PojoSchemaConverter<>();
        Schema schema = pojoPojoSchemaConverter.convert(ChildPojo.class);
        Assert.assertEquals(schema.getName(), "ChildPojo");
        List<Schema.Column> columns = schema.getColumns();
        validateColumns(columns, Arrays.asList(
                "blackList|boolean|true|null",
                "transactionDate|java.util.Date|true|null",
                "id|java.lang.Long|true|null",
                "accountNo|java.lang.String|true|null",
                "amount|java.math.BigDecimal|true|null"));
    }

    private static void validateColumns(List<Schema.Column> columns, List<String> columnValues) {
        List<String> columnValuesToString = columns.stream().map( (c) -> {
            return String.join("|", c.getName(), c.getType(), String.valueOf(c.isNullable()),
                    Optional.ofNullable(c.getDefaultValue()).orElse("null").toString());
        } ).collect(Collectors.toList());
        Assert.assertEquals(columnValuesToString, columnValues);
    }

}
