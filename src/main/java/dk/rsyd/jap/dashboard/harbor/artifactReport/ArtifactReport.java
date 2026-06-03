package dk.rsyd.jap.dashboard.harbor.artifactReport;

import dk.rsyd.jap.dashboard.gitlab.Commit;
import dk.rsyd.jap.dashboard.harbor.client.HarborClientDTOs;
import io.micronaut.serde.annotation.Serdeable;

@Serdeable
public record ArtifactReport(
    String repositoryURL,
    String commitShortId,
    String commitURL,
    String artifactURL,
    String severity,
    int critical,
    int high,
    int medium,
    int low,
    int total,
    int fixable
) {

    public static ArtifactReport of(String repositoryURL, Commit commit, String artifactURL, HarborClientDTOs.ScanReport scanReport) {

        var scanSummary = scanReport.summary();
        var vulnerabilityCounts = scanSummary.counts();

        return new ArtifactReport(
            repositoryURL,
            commit.shortId(),
            commit.gitlabURL(),
            artifactURL,
            scanReport.severity(),
            vulnerabilityCounts.critical(),
            vulnerabilityCounts.high(),
            vulnerabilityCounts.medium(),
            vulnerabilityCounts.low(),
            scanSummary.total(),
            scanSummary.fixable()
        );
    }

    public static ArtifactReport WithNoArtifactInfo(String repositoryURL, Commit commit) {
        return new ArtifactReport(
            repositoryURL,
            commit.shortId(),
            commit.gitlabURL(),
            null,
            null,
            0,
            0,
            0,
            0,
            0,
            0
        );
    }
}
