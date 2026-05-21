package dk.rsyd.jap.dashboard.nomad;

import io.micronaut.serde.annotation.Serdeable;

@Serdeable
public record NomadJob(
    String id,
    String name,
    String serviceLink,
    String nomadStatus,
    String healthStatus,
    String nomadLink
) {

    public NomadJob {
        if (nomadLink == null) {
            nomadLink = findNomadLink(name);
        }
    }

    public static NomadJob fromDTO(NomadClientDTOs.NomadJob nomadJob, String serviceLink, String healthStatus) {
        return new NomadJob(
            nomadJob.id(),
            nomadJob.name(),
            serviceLink,
            nomadJob.status(),
            healthStatus,
            findNomadLink(nomadJob.name())
        );
    }

    private static String findNomadLink(String name) {
        return "https://nomad.rsyd.net/ui/jobs/" + name + "@jap";
    }
}