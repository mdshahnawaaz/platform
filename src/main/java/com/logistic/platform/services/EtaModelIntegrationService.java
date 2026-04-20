package com.logistic.platform.services;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import com.logistic.platform.models.EtaModelInput;
import com.logistic.platform.models.EtaModelOutput;
import com.logistic.platform.models.EtaModelStatus;

@Service
public class EtaModelIntegrationService {

    private final RestClient restClient;
    private final AtomicReference<String> lastAttemptAt = new AtomicReference<>(null);
    private final AtomicReference<String> lastSuccessAt = new AtomicReference<>(null);
    private final AtomicReference<String> lastSuccessModelSource = new AtomicReference<>(null);
    private final AtomicReference<String> lastError = new AtomicReference<>(null);

    @Value("${ai.eta.model.enabled:false}")
    private boolean modelEnabled;

    @Value("${ai.eta.model.base-url:http://localhost:8001}")
    private String modelBaseUrl;

    public EtaModelIntegrationService(RestClient.Builder restClientBuilder) {
        this.restClient = restClientBuilder.build();
    }

    public Optional<EtaModelOutput> scoreEta(EtaModelInput input) {
        lastAttemptAt.set(LocalDateTime.now().toString());
        if (!modelEnabled) {
            lastError.set("ai.eta.model.enabled=false");
            return Optional.empty();
        }

        try {
            EtaModelOutput output = restClient.post()
                    .uri(modelBaseUrl + "/predict-eta")
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(input)
                    .retrieve()
                    .body(EtaModelOutput.class);
            if (output != null) {
                lastSuccessAt.set(LocalDateTime.now().toString());
                lastSuccessModelSource.set(output.modelSource());
                lastError.set(null);
            }
            return Optional.ofNullable(output);
        } catch (Exception exception) {
            lastError.set(exception.getClass().getSimpleName() + ": " + exception.getMessage());
            return Optional.empty();
        }
    }

    public EtaModelStatus getStatus() {
        return new EtaModelStatus(
                modelEnabled,
                modelBaseUrl,
                lastAttemptAt.get(),
                lastSuccessAt.get(),
                lastSuccessModelSource.get(),
                lastError.get());
    }
}
