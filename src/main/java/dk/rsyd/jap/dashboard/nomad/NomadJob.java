package dk.rsyd.jap.dashboard.nomad;

import io.micronaut.serde.annotation.Serdeable;

@Serdeable
public record NomadJob(
    String id,
    String name,
    String gitCommit,
    String serviceURL,
    String nomadStatus,
    String healthStatus,
    String healthURL,
    String nomadURL,
    String logscaleURL,
    String docsURL
) {

    public static NomadJob fromDTO(
        NomadClientDTOs.NomadJob nomadJob,
        String serviceURL,
        String healthStatus,
        String logscaleURL
    ) {
        String gitSha = resolveGitSha(nomadJob);

        return new NomadJob(
            nomadJob.id(),
            nomadJob.name(),
            gitSha,
            serviceURL,
            nomadJob.status(),
            healthStatus,
            findHealthURL(serviceURL),
            findNomadURL(nomadJob.name()),
            logscaleURL,
            findDocsURL(serviceURL)
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

    private static String findNomadURL(String name) {
        return "https://nomad.rsyd.net/ui/jobs/" + name + "@jap";
    }

    private static String findHealthURL(String serviceURL) {
        return serviceURL + "/health";
    }

    private static String findDocsURL(String serviceURL) {
        return serviceURL + "/docs";
    }
}