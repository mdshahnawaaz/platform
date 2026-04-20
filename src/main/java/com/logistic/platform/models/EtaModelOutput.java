package com.logistic.platform.models;

public record EtaModelOutput(
        Integer driverArrivalMinutes,
        Integer deliveryMinutes,
        Integer totalEtaMinutes,
        String confidence,
        String modelSource) {
}
