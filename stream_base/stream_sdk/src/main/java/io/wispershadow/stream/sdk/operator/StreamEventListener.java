package io.wispershadow.stream.sdk.operator;

import io.wispershadow.stream.sdk.context.OperatorContext;

public interface StreamEventListener {
    public void onInitialize();

    public void onClose();
}
