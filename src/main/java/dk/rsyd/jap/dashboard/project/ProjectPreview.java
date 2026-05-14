package dk.rsyd.jap.dashboard.project;

import dk.rsyd.jap.dashboard.gitlab.GitlabProject;
import dk.rsyd.jap.dashboard.harbor.artifactReport.ArtifactReport;
import io.micronaut.serde.annotation.Serdeable;

@Serdeable
public record ProjectPreview(
    GitlabProject gitlabProject,
    ArtifactReport latestMasterCommitReport,
    ArtifactReport latestProdDeployReport
) {
}
