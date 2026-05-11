package dk.rsyd.jap.dashboard.harbor.artifactReport;

import dk.rsyd.jap.dashboard.harbor.client.HarborClientDTOs;
import io.micronaut.serde.annotation.Serdeable;

@Serdeable
public record ArtifactReport(
    String repositoryLink,
    String artifactLink,
    String commitShortId,
    String severity,
    int critical,
    int high,
    int medium,
    int low,
    int total,
    int fixable
) {

    public static ArtifactReport of(String projectName, String commitShortId, HarborClientDTOs.ScanReport scanReport, String digest){
        // TODO: move to config?
        String repositoryLink = "https://harbor.rsyd.net/harbor/projects/5/repositories/" + projectName.toLowerCase().replace(" ", "-");
        String artifactLink = repositoryLink + "/artifacts-tab/artifacts/" + digest;

        var scanSummary = scanReport.summary();
        var vulnerabilityCounts = scanSummary.counts();

        return new ArtifactReport(
            repositoryLink,
            artifactLink,
            commitShortId,
            scanReport.severity(),
            vulnerabilityCounts.critical(),
            vulnerabilityCounts.high(),
            vulnerabilityCounts.medium(),
            vulnerabilityCounts.low(),
            scanSummary.total(),
            scanSummary.fixable()
        );
    }
}
