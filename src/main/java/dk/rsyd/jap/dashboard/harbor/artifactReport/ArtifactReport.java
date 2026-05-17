package dk.rsyd.jap.dashboard.harbor.artifactReport;

import dk.rsyd.jap.dashboard.gitlab.GitlabClientDTOs;
import dk.rsyd.jap.dashboard.harbor.client.HarborClientDTOs;
import io.micronaut.serde.annotation.Serdeable;

//todo split or refactor
@Serdeable
public record ArtifactReport(
    String repositoryLink,
    String commitShortId,
    String commitLink,
    String artifactLink,
    String severity,
    int critical,
    int high,
    int medium,
    int low,
    int total,
    int fixable
) {

    private static String getRepositoryLink(String projectName) {
        // TODO: move to config?
        return "https://harbor.rsyd.net/harbor/projects/5/repositories/" + projectName.toLowerCase().replace(" ", "-");
    }

    public static ArtifactReport of(String projectName, GitlabClientDTOs.Commit commit, HarborClientDTOs.ScanReport scanReport, String digest) {
        String repositoryLink = getRepositoryLink(projectName);
        // TODO: move to config?
        String artifactLink = repositoryLink + "/artifacts-tab/artifacts/" + digest;

        var scanSummary = scanReport.summary();
        var vulnerabilityCounts = scanSummary.counts();

        return new ArtifactReport(
            repositoryLink,
            commit.shortId(),
            commit.link(),
            artifactLink,
            scanReport.severity(),
            vulnerabilityCounts.critical(),
            vulnerabilityCounts.high(),
            vulnerabilityCounts.medium(),
            vulnerabilityCounts.low(),
            scanSummary.total(),
            scanSummary.fixable()
        );
    }

    public static ArtifactReport WithNoArtifactInfo(String projectName, GitlabClientDTOs.Commit commit) {
        return new ArtifactReport(
            getRepositoryLink(projectName),
            commit.shortId(),
            commit.link(),
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
