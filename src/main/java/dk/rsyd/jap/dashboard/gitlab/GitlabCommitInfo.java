package dk.rsyd.jap.dashboard.gitlab;

import io.micronaut.serde.annotation.Serdeable;

@Serdeable
public record GitlabCommitInfo(

    String latestMasterCommit,
    String latestProdDeployCommit

) {
}
