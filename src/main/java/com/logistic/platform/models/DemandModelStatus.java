package com.logistic.platform.models;

public record DemandModelStatus(
        boolean enabled,
        String baseUrl,
        String lastAttemptAt,
        String lastSuccessAt,
        String lastSuccessModelSource,
        String lastError) {
}
