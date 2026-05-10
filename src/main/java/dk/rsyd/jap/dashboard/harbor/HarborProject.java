package dk.rsyd.jap.dashboard.harbor;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.micronaut.serde.annotation.Serdeable;

@Serdeable
public record HarborProject(

    @JsonProperty("project_id")
    int projectId,
    String name,
    String severity

) {
}
