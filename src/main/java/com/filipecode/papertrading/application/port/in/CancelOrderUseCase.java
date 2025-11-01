package com.filipecode.papertrading.application.port.in;

public interface CancelOrderUseCase {
    void cancel(Long orderId);
}
