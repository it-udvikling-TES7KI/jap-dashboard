package dk.rsyd.jap.dashboard.harbor.artifactReport;

import io.micronaut.serde.annotation.Serdeable;

@Serdeable
public record ArtifactReport(
    String harborLink,
    String commitShortId,
    String severity,
    int critical,
    int high,
    int medium,
    int low,
    int total
) {
}
