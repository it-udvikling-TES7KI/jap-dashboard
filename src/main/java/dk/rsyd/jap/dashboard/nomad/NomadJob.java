package dk.rsyd.jap.dashboard.nomad;

import io.micronaut.serde.annotation.Serdeable;

@Serdeable
public record NomadJob(
    String id,
    String name,
    String gitCommit,
    String serviceLink,
    String nomadStatus,
    String healthStatus,
    String nomadLink,
    String logscaleLink
) {

    public static NomadJob fromDTO(NomadClientDTOs.NomadJob nomadJob, String serviceLink, String healthStatus, String logscaleLink) {
        return new NomadJob(
            nomadJob.id(),
            nomadJob.name(),
            nomadJob.meta().GitSha(),
            serviceLink,
            nomadJob.status(),
            healthStatus,
            findNomadLink(nomadJob.name()),
            logscaleLink
        );
    }

    private static String findNomadLink(String name) {
        return "https://nomad.rsyd.net/ui/jobs/" + name + "@jap";
    }
}