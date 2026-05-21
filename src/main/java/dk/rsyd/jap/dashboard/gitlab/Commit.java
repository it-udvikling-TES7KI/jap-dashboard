package dk.rsyd.jap.dashboard.gitlab;

import io.micronaut.serde.annotation.Serdeable;

@Serdeable
public record Commit(
    String id,
    String shortId,
    String title,
    String gitlabLink
) {
    public static Commit fromDTO(GitlabClientDTOs.Commit commit) {
        return new Commit(commit.id(), commit.shortId(), commit.title(), commit.link());
    }

}
