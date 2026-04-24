package dk.rsyd.jap.dashboard.gitlab;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.micronaut.serde.annotation.Serdeable;

@Serdeable
public record GitlabProject(

    @JsonProperty("id")
    int id,

    @JsonProperty("name")
    String name,

    @JsonProperty("web_url")
    String gitlabLink) {
}
