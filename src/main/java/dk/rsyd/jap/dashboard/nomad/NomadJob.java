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
            findNomadLink(nomadJob.name()),
            logscaleLink
        );
    }

    private static String resolveGitSha(NomadClientDTOs.NomadJob nomadJob) {
        if (nomadJob.meta() != null && nomadJob.meta().gitSha() != null && !nomadJob.meta().gitSha().isBlank()) {
            return nomadJob.meta().gitSha();
        }

        String image = extractImage(nomadJob);
        return extractGitShaFromImage(image);
    }

    private static String extractImage(NomadClientDTOs.NomadJob nomadJob) {
        if (nomadJob.taskGroups() == null || nomadJob.taskGroups().isEmpty()) {
            return null;
        }

        var firstGroup = nomadJob.taskGroups().getFirst();
        if (firstGroup.tasks() == null || firstGroup.tasks().isEmpty()) {
            return null;
        }

        var firstTask = firstGroup.tasks().getFirst();
        if (firstTask.config() == null) {
            return null;
        }

        return firstTask.config().image();
    }

    private static String extractGitShaFromImage(String image) {
        if (image == null || image.isBlank()) {
            return null;
        }

        String normalized = image.trim();

        // Remove wrapping quotes if present
        if (normalized.length() >= 2 && normalized.startsWith("\"") && normalized.endsWith("\"")) {
            normalized = normalized.substring(1, normalized.length() - 1);
        }

        int colonIndex = normalized.lastIndexOf(':');
        if (colonIndex < 0 || colonIndex == normalized.length() - 1) {
            return null;
        }

        return normalized.substring(colonIndex + 1);
    }

    private static String findNomadLink(String name) {
        return "https://nomad.rsyd.net/ui/jobs/" + name + "@jap";
    }
}