package io.wispershadow.stream.sdk.context;

import java.util.Map;

public interface OperatorContext {
    public String getApplicationId();

    public String getOperatorId();

    public String getOperatorName();

    public Map<String, Object> getGlobalProperties();

    public <T> T getParameter(Class<T> paramClass);
}
