package dk.rsyd.jap.dashboard.gitlab;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.micronaut.serde.annotation.Serdeable;

import java.time.OffsetDateTime;


public class GitlabClientDTOs {

    @Serdeable
    public record GitlabProject(
        @JsonProperty("id") int id,
        @JsonProperty("name") String name,
        @JsonProperty("name_with_namespace") String nameWithNamespace,
        @JsonProperty("web_url") String gitlabLink) {
    }

    @Serdeable
    public record Commit(
        String id,
        @JsonProperty("short_id") String shortId,
        @JsonProperty("created_at") OffsetDateTime createdAt,
        @JsonProperty("web_url") String link) {
    }

    @Serdeable
    public record Job(
        String id,
        String stage,
        String name,
        String ref,
        Commit commit) {
    }
}
