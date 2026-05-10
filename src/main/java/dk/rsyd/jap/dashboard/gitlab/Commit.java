package dk.rsyd.jap.dashboard.gitlab;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.micronaut.serde.annotation.Serdeable;

import java.time.OffsetDateTime;

@Serdeable
public record Commit(
    String id,
    @JsonProperty("short_id")
    String shortId,
    @JsonProperty("created_at")
    OffsetDateTime createdAt
) {

}
