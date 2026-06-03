package dk.rsyd.jap.dashboard.gitlab;

import io.micronaut.serde.annotation.Serdeable;

import static java.util.regex.Pattern.quote;

@Serdeable
public record GitlabProject(
    int id,
    String name,
    String projectGroupPath,
    String gitlabURL) {


    public static GitlabProject fromJapNameSpace(GitlabClientDTOs.GitlabProject dto) {
        String projectGroup = findProjectGroup(dto.nameWithNamespace(), dto.name());

        return new GitlabProject(dto.id(), dto.name(), projectGroup, dto.gitlabUrl());
    }

    private static String findProjectGroup(String fullNamespace, String name) {
        // Remove everything before and including " / jap / "
        String afterJap = fullNamespace.replaceAll("(?i)^.*?\\s*/\\s*jap\\s*/\\s*", "");
        // Remove the trailing /
        String trailRemoved = afterJap.replaceAll("\\s*/\\s*$", "");
        // Remove only the last occurrence of the project name - in case projectgroup shares the name
        return trailRemoved.replaceFirst("\\s*/?\\s*" + quote(name) + "\\s*$", "");
    }

}
