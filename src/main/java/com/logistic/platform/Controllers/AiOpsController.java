package com.logistic.platform.Controllers;

import java.util.List;

import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.logistic.platform.models.DemandFeatureSnapshot;
import com.logistic.platform.models.DemandModelInput;
import com.logistic.platform.models.DemandModelOutput;
import com.logistic.platform.models.DemandTrainingSpec;
import com.logistic.platform.services.DemandDatasetService;
import com.logistic.platform.services.DemandFeatureSnapshotService;
import com.logistic.platform.services.DemandModelIntegrationService;

@RestController
@RequestMapping("/logistics/ai")
public class AiOpsController {

    private final DemandFeatureSnapshotService demandFeatureSnapshotService;
    private final DemandDatasetService demandDatasetService;
    private final DemandModelIntegrationService demandModelIntegrationService;

    public AiOpsController(
            DemandFeatureSnapshotService demandFeatureSnapshotService,
            DemandDatasetService demandDatasetService,
            DemandModelIntegrationService demandModelIntegrationService) {
        this.demandFeatureSnapshotService = demandFeatureSnapshotService;
        this.demandDatasetService = demandDatasetService;
        this.demandModelIntegrationService = demandModelIntegrationService;
    }

    @PostMapping("/feature-snapshots")
    public DemandFeatureSnapshot captureSnapshot(@RequestParam(defaultValue = "standard") String vehicleType) {
        return demandFeatureSnapshotService.captureSnapshot(vehicleType);
    }

    @PostMapping("/feature-snapshots/default")
    public List<DemandFeatureSnapshot> captureDefaultSnapshots() {
        return demandFeatureSnapshotService.captureDefaultSnapshots();
    }

    @PostMapping("/feature-snapshots/backfill-labels")
    public String backfillLabels() {
        int updated = demandFeatureSnapshotService.backfillLabels();
        return updated + " snapshot labels updated";
    }

    @GetMapping("/feature-snapshots")
    public List<DemandFeatureSnapshot> getRecentSnapshots() {
        return demandFeatureSnapshotService.getRecentSnapshots();
    }

    @GetMapping(value = "/feature-snapshots/export.csv", produces = "text/csv")
    public ResponseEntity<String> exportSnapshotsCsv(@RequestParam(defaultValue = "true") boolean labeledOnly) {
        String csv = demandDatasetService.exportSnapshotsAsCsv(labeledOnly);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=demand_feature_snapshots.csv")
                .contentType(new MediaType("text", "csv"))
                .body(csv);
    }

    @GetMapping("/feature-snapshots/schema")
    public DemandTrainingSpec getTrainingSpec() {
        return demandDatasetService.getTrainingSpec();
    }

    @GetMapping("/model-input")
    public DemandModelInput getLiveModelInput(@RequestParam(defaultValue = "standard") String vehicleType) {
        return demandModelIntegrationService.buildLiveInput(vehicleType);
    }

    @GetMapping("/model-score")
    public ResponseEntity<DemandModelOutput> scoreLiveDemand(@RequestParam(defaultValue = "standard") String vehicleType) {
        return demandModelIntegrationService.scoreLiveDemand(vehicleType)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.noContent().build());
    }
}
