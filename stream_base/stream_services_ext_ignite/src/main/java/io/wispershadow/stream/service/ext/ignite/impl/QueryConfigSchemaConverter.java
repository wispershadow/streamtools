package io.wispershadow.stream.service.ext.ignite.impl;

import io.wispershadow.stream.common.Converter;
import io.wispershadow.stream.sdk.schema.Schema;
import org.apache.ignite.cache.QueryEntity;
import org.apache.ignite.cache.QueryIndex;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;

public class QueryConfigSchemaConverter implements Converter<QueryEntity, Schema> {
    private static final Logger logger = LoggerFactory.getLogger(QueryConfigSchemaConverter.class);

    @Override
    public Schema convert(QueryEntity source, Object conversionContext) {
        logger.debug("Start building sql table from query entity: {}", source);
        final List<Schema.Column> columns = new ArrayList<>();
        source.getFields().forEach((k, v) -> {
            try {
                columns.add(Schema.Column.of(k, v));
            }
            catch (Exception ex) {
                logger.error("Error building column: " + k, ex);
            }
        });
        List<String> keyColumnNames = new ArrayList<String>();
        String keyFieldName = source.getKeyFieldName();
        if (keyFieldName != null) {
            keyColumnNames.add(keyFieldName);
        }
        else {
            Set<String> keyFields = source.getKeyFields();
            if (keyFields != null && keyFields.size() > 0) {
                keyColumnNames.addAll(keyFields);
            }
        }
        List<Schema.Index> schemaIndices = new ArrayList<>();
        Collection<QueryIndex> queryIndices = source.getIndexes();
        if (queryIndices != null && queryIndices.size() > 0) {
            queryIndices.forEach((q) -> {
                List<String> indexColNames = new ArrayList<String>();
                List<Boolean> indexColAscs = new ArrayList<Boolean>();
                q.getFields().forEach((k, v) -> {
                    indexColNames.add(k);
                    indexColAscs.add(v);
                });
                Schema.Index index = Schema.Index.of(indexColNames, indexColAscs);
                schemaIndices.add(index);
            });
        }
        String tableName = source.getTableName();
        return new Schema(tableName, columns, keyColumnNames, schemaIndices);
    }
}
