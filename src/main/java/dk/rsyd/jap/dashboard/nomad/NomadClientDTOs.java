package dk.rsyd.jap.dashboard.nomad;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.micronaut.serde.annotation.Serdeable;

import java.util.List;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public record NomadClientDTOs() {

    @Serdeable
    public record NomadJobSimple(

        @JsonProperty("ID")
        String id,

        @JsonProperty("Name")
        String name,

        @JsonProperty("Meta")
        Meta meta,

        @JsonProperty("Status")
        String status
    ) {
    }

    @Serdeable
    public record NomadJob(

        @JsonProperty("ID")
        String id,

        @JsonProperty("Name")
        String name,

        @JsonProperty("Meta")
        Meta meta,

        @JsonProperty("Status")
        String status,

        @JsonProperty("TaskGroups")
        List<TaskGroup> taskGroups
    ) {
    }

    @Serdeable
    public record Meta(
        @JsonProperty("GIT-SHA")
        String gitSha
    ) {
    }

    @Serdeable
    public record TaskGroup(
        @JsonProperty("Name")
        String name,

        @JsonProperty("Tasks")
        List<Task> tasks
    ) {
    }

    @Serdeable
    public record Task(
        @JsonProperty("Services")
        List<Service> services,

        @JsonProperty("Config")
        Config config
    ) {
    }

    @Serdeable
    public record Config(
        String image
    ){
    }

    @Serdeable
    public record Service(
        @JsonProperty("Tags")
        List<String> tags
    ) {
        //todo revisit
        private static final Pattern TRAEFIK_HOST_RULE_PATTERN =
            Pattern.compile("^traefik\\.http\\.routers\\.[^.]+\\.rule=Host\\(`([^`]+)`\\)$");

        public Optional<String> extractTraefikHost() {
            if (tags == null || tags.isEmpty()) {
                return Optional.empty();
            }

            for (String tag : tags) {
                if (tag == null) {
                    continue;
                }

                Matcher matcher = TRAEFIK_HOST_RULE_PATTERN.matcher(tag);
                if (matcher.matches()) {
                    return Optional.of(matcher.group(1));
                }
            }

            return Optional.empty();
        }
    }
}
