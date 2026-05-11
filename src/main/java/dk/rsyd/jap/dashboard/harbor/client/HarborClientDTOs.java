package dk.rsyd.jap.dashboard.harbor.client;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.micronaut.serde.annotation.Serdeable;

import java.util.List;
import java.util.Map;

public class HarborClientDTOs {

    @Serdeable
    public record Artifact(
        @JsonProperty("push_time") String pushTime,
        List<Tag> tags,
        String digest,
        // Vi bruger et Map her, fordi nøglen under scan_overview ofte er en lang streng (mime-type)
        @JsonProperty("scan_overview") Map<String, ScanReport> scanOverview
    ) {
    }

    @Serdeable
    public record Tag(String name) {
    }

    @Serdeable
    public record ScanReport(
        ScanSummary summary,
        String severity
    ) {
    }

    @Serdeable
    public record ScanSummary(
        @JsonProperty("summary") VulnerabilityCounts counts,
        int fixable,
        int total
    ) {
    }

    @Serdeable
    public record VulnerabilityCounts(
        @JsonProperty(value = "Critical", defaultValue = "0") int critical,
        @JsonProperty(value = "High", defaultValue = "0") int high,
        @JsonProperty(value = "Medium", defaultValue = "0") int medium,
        @JsonProperty(value = "Low", defaultValue = "0") int low
    ) {
    }

}
