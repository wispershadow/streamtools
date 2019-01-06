package io.wispershadow.stream.service.ext.ignite;

import io.wispershadow.stream.sdk.schema.Schema;
import io.wispershadow.stream.service.ext.ignite.impl.QueryConfigSchemaConverter;
import org.apache.ignite.Ignite;
import org.apache.ignite.Ignition;
import org.apache.ignite.cache.QueryEntity;
import org.apache.ignite.configuration.CacheConfiguration;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class QueryConfigSchemaConverterTest {
    @BeforeClass
    public static void startIgniteServer() {
        EmbedIgniteBuilder.buildIgnite(30000, 40000, true);
    }

    @Test
    public void testConvertSchemaFromIgniteQueryConfig() {
        Ignition.setClientMode(true);
        Ignite clientIgnite = Ignition.start("ignite-client-test1.xml");
        CacheConfiguration[] cacheConfigurations = clientIgnite.configuration().getCacheConfiguration();
        boolean found = false;
        for (CacheConfiguration cacheConfiguration : cacheConfigurations) {
            if ("D_ACCOUNT_INFO".equals(cacheConfiguration.getName())) {
                found = true;
                QueryEntity queryEntity = (QueryEntity) cacheConfiguration.getQueryEntities().iterator().next();
                QueryConfigSchemaConverter queryConfigSchemaConverter = new QueryConfigSchemaConverter();
                Schema schema = queryConfigSchemaConverter.convert(queryEntity);
                Assert.assertTrue("AccountInfo".equalsIgnoreCase(schema.getName()));
                System.out.println(schema.getName());
                System.out.println(schema.getIndexColumns());
                System.out.println(schema.getKeyColumns());
            }
        }
        Assert.assertEquals(found, true);
    }

    private static void validateColumns(List<Schema.Column> columns, List<String> columnValues) {
        List<String> columnValuesToString = columns.stream().map( (c) -> {
            return String.join("|", c.getName(), c.getType(), String.valueOf(c.isNullable()),
                    Optional.ofNullable(c.getDefaultValue()).orElse("null").toString());
        } ).collect(Collectors.toList());
        Assert.assertEquals(columnValuesToString, columnValues);
    }
}
