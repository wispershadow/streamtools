package io.wispershadow.stream.service.core.schema.impl;

import io.wispershadow.stream.common.Converter;
import io.wispershadow.stream.sdk.schema.Schema;
import org.springframework.util.ReflectionUtils;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

public class PojoSchemaConverter<S> implements Converter<Class<S>, Schema> {
    private List<String> keyColumnNames;
    private List<List<String>> indexColumns;

    @Override
    public Schema convert(Class<S> source, Object conversionContext) {
        String schemaName = getSchemaName(source.getName());
        final List<Schema.Column> columns = new ArrayList<Schema.Column>();
        ReflectionUtils.doWithFields(source, new ReflectionUtils.FieldCallback() {
            public void doWith(Field field) throws IllegalArgumentException,
                    IllegalAccessException {
                columns.add(Schema.Column.of(field.getName(), field.getType()));
            }
        }, new ReflectionUtils.FieldFilter() {
            public boolean matches(Field field) {
                return !"class".equals(field.getName());
            }
        });
        Schema schema = new Schema(schemaName, columns);
        if (keyColumnNames != null) {
            schema.setKeyColumnNames(keyColumnNames);
        }
        return schema;
    }


    private static String getSchemaName(String fullClassName) {
        String[] arr = fullClassName.split("\\.");
        if (arr.length > 0) {
            return arr[arr.length - 1];
        }
        else {
            return fullClassName;
        }
    }
}
