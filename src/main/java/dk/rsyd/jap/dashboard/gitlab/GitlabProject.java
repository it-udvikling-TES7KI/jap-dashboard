package dk.rsyd.jap.dashboard.gitlab;

import io.micronaut.serde.annotation.Serdeable;

@Serdeable
public record GitlabProject(
    int id,
    String name,
    String projectGroupPath,
    String gitlabURL) {


    public static GitlabProject fromJapNameSpace(GitlabClientDTOs.GitlabProject dto) {
    String fullNamespace = dto.nameWithNamespace();
    // Remove everything before and including " / jap / "
    String afterJap = fullNamespace.replaceAll("(?i)^.*?\\s*/\\s*jap\\s*/\\s*", "");
    // Remove the trailing /
    String trailRemoved = afterJap.replaceAll("\\s*/\\s*$", "");
    // Remove only the last occurrence of the project name - in case projectgroup shares the name
    String projectGroup = trailRemoved.replaceFirst("\\s*/?\\s*" + java.util.regex.Pattern.quote(dto.name()) + "\\s*$", "");
    return new GitlabProject(dto.id(), dto.name(), projectGroup, dto.gitlabUrl());
}
}
