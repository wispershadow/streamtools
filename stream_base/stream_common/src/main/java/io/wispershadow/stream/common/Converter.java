package io.wispershadow.stream.common;

public interface Converter<S, T> {
    public T convert(S source, Object conversionContext);

    default public T convert(S source) {
        return convert(source, null);
    }
}
