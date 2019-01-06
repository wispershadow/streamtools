package io.wispershadow.stream.service.ext.ignite.impl;

import java.util.HashMap;
import java.util.Map;

public class IgniteSqlTypeMapper {
    private static Map<String, String> mappings = new HashMap<String, String>();

    //see org.h2.value.VALUE and
    //org.apache.ignite.internal.processors.query.h2.opt.GridH2RowDescriptor
    static {
        mappings.put("java.lang.String", "varchar");
        mappings.put("java.math.BigDecimal", "decimal");
        mappings.put("java.lang.Float", "float");
        mappings.put("java.lang.Double", "double");
        mappings.put("java.lang.Long", "long");
        mappings.put("java.lang.Integer", "int");
        mappings.put("java.util.Date", "timestamp");
        mappings.put("java.lang.Boolean", "boolean");
    }

    public static String mapType(String javaTypeName) {
        return mappings.get(javaTypeName);
    }
}
