package dk.rsyd.jap.dashboard.nomad;

import io.micronaut.serde.annotation.Serdeable;

@Serdeable
public record NomadJobDTO(
    String id,
    String name,
    String nomadLink,
    String serviceLink
) {
    public static NomadJobDTO from(NomadJob nomadJob) {
        return new NomadJobDTO(
            nomadJob.getId(),
            nomadJob.getName(),
            nomadJob.getNomadLink(),
            nomadJob.getServiceLink()
        );
    }
}
