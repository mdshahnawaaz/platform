package com.logistic.platform.models;

import java.util.List;

public record DemandTrainingSpec(
        List<String> featureColumns,
        String targetColumn,
        String problemType,
        String predictionWindow,
        String notes) {
}
