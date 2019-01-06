package io.wispershadow.stream.service.ext.ignite.impl;

import org.apache.ignite.Ignite;
import org.apache.ignite.configuration.CacheConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import java.text.MessageFormat;
import java.util.Set;
import java.util.stream.Collectors;

public class IgniteUtils {
    private static final Logger logger = LoggerFactory.getLogger(IgniteUtils.class);
    private static final String IGNITE_THIN_DRIVER_NAME = "org.apache.ignite.IgniteJdbcThinDriver";
    private static final String IGNITE_JDBC_URL_TEMPLATE = "jdbc:ignite:thin://{0}";

    /**
     * build ignite server connection string.
     * @param ignite  ignite
     * @return connection string
     * @throws Exception
     */
    public static String buildIgniteServerConnectionString(Ignite ignite) throws Exception {
        Set<String> hosts = ignite.cluster().nodes().stream().filter((n) -> { return !n.isClient(); })
                .map((n) -> { return n.hostNames().iterator().next(); }).collect(Collectors.toSet());
        String conUrl = MessageFormat.format(IGNITE_JDBC_URL_TEMPLATE, String.join(",", hosts));
        logger.debug("Ignite jdbc connection string is : {}", conUrl);
        return conUrl;
    }

    /**
     * build a cache config from template file.
     * @param templateBeanId template bean id
     * @param templateFile  tempalte file
     * @param keyType  key type
     * @param valueType value type
     * @param <K>  key generic
     * @param <V> value generic
     * @return  cache configuration
     */
    public static <K, V> CacheConfiguration<K, V> buildWithTemplate(String templateBeanId,
                                                                    String templateFile, Class<K> keyType,
                                                                    Class<V> valueType) {
        ClassPathXmlApplicationContext applicationContext =
                new ClassPathXmlApplicationContext(new String[]{templateFile});
        applicationContext.setClassLoader(Thread.currentThread().getContextClassLoader());
        CacheConfiguration igniteCacheConfig = (CacheConfiguration) applicationContext.getBean(templateBeanId);
        return igniteCacheConfig;
    }

}
