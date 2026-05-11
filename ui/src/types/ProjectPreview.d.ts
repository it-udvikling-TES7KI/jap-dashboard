import {GitlabProject} from "./GitlabProject";
import {ArtifactReport} from "./ArtifactReport.d.ts";

export interface ProjectPreview {
    gitlabProject: GitlabProject;
    artifactReport: ArtifactReport;
}

