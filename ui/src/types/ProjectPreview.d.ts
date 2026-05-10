import {GitlabProject} from "./GitlabProject";

export interface ProjectPreview {
    gitlabProject: GitlabProject;
    artifactReport: ArtifactReport;
}

export interface ArtifactReport {
    harborLink: string;
    commitShortId: string;
    severity: string;
    critical: number;
    high: number;
    medium: number;
    low: number;
    total: number;
}

