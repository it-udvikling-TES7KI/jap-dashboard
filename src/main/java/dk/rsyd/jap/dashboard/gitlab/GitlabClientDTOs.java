package dk.rsyd.jap.dashboard.gitlab;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.micronaut.serde.annotation.Serdeable;


public class GitlabClientDTOs {

    @Serdeable
    public record GitlabProject(
        @JsonProperty("id") int id,
        @JsonProperty("name") String name,
        @JsonProperty("name_with_namespace") String nameWithNamespace,
        @JsonProperty("web_url") String gitlabLink) {
    }
}
