package dk.rsyd.jap.dashboard.gitlab;

import io.micronaut.serde.annotation.Serdeable;

import java.time.LocalDateTime;

@Serdeable
public record Commit(
    String id,
    String shortId,
    String title,
    String gitlabURL,
    String authorEmail,
    LocalDateTime createdAt
) {
    public static Commit fromDTO(GitlabClientDTOs.Commit commit) {
        return new Commit(commit.id(), commit.shortId(), commit.title(), commit.url(), commit.authorEmail(), commit.createdAt().toLocalDateTime());
    }

}
