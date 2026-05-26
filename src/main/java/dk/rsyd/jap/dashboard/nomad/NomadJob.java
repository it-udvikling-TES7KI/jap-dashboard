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
    String healthURL,
    String nomadLink,
    String logscaleLink,
    String docsURL
) {

    public static NomadJob fromDTO(
        NomadClientDTOs.NomadJob nomadJob,
        String serviceLink,
        String healthStatus,
        String logscaleLink
    ) {
        String gitSha = resolveGitSha(nomadJob);

        return new NomadJob(
            nomadJob.id(),
            nomadJob.name(),
            gitSha,
            serviceLink,
            nomadJob.status(),
            healthStatus,
            findHealthURL(serviceLink),
            findNomadLink(nomadJob.name()),
            logscaleLink,
            findDocsURL(serviceLink)
        );
    }

    private static String resolveGitSha(NomadClientDTOs.NomadJob nomadJob) {
        if (nomadJob.meta() != null
            && nomadJob.meta().gitSha() != null
            && !nomadJob.meta().gitSha().isBlank()) {
            return nomadJob.meta().gitSha();
        }

        //When deploy is done from tag, the sha is a bit more complex to find
        String image = extractImage(nomadJob);
        return extractGitShaFromImage(image);
    }

    private static String extractImage(NomadClientDTOs.NomadJob nomadJob) {
        if (nomadJob.taskGroups() == null || nomadJob.taskGroups().isEmpty()) return null;
        var group = nomadJob.taskGroups().getFirst();

        if (group.tasks() == null || group.tasks().isEmpty()) return null;
        var task = group.tasks().getFirst();

        return task.config() != null ? task.config().image() : null;
    }

    private static String extractGitShaFromImage(String image) {
        if (image == null || image.isBlank()) return null;

        String normalized = image.trim().replace("\"", "");
        int colonIndex = normalized.lastIndexOf(':');

        return colonIndex >= 0 && colonIndex < normalized.length() - 1
            ? normalized.substring(colonIndex + 1)
            : null;
    }

    private static String findNomadLink(String name) {
        return "https://nomad.rsyd.net/ui/jobs/" + name + "@jap";
    }

    private static String findHealthURL(String serviceLink) {
        return serviceLink + "/health";
    }

    private static String findDocsURL(String serviceLink) {
        return serviceLink + "/docs";
    }
}