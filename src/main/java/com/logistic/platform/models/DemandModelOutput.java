package com.logistic.platform.models;

import java.math.BigDecimal;

public record DemandModelOutput(
        double predictedBookingsNext30Minutes,
        BigDecimal recommendedDemandFactor,
        String modelSource) {
}
