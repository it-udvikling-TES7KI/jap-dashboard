import styles from "./ProjectCard.module.css"
import {Link} from "react-router-dom";
import question_mark from "../../assets/question_mark.svg";
import gitlab_icon from "../../assets/gitlab_icon.svg";
import harbor_icon from "../../assets/harbor_icon.svg";
import {ArtifactReportFocus} from "../../types/ArtifactReportFocus.ts";
import {GitlabProject} from "../../types/GitlabProject";
import {useQuery} from "@tanstack/react-query";
import {fetchArtifactReportFromLatestMasterCommit, fetchArtifactReportFromLatestProdDeploy} from "../../hooks/HarborHook.ts";

export interface ProjectCardProps {
    gitProject: GitlabProject;
    artifactReportFocus: ArtifactReportFocus;
}

export default function ProjectCard({gitProject, artifactReportFocus}: ProjectCardProps) {

    const {data: artifactReport} = useQuery({
        queryKey: ['artifactReport', gitProject.id, artifactReportFocus],
        queryFn: determineArtifactRequest,
        enabled: !!gitProject.id,
    })

    function determineArtifactRequest() {
        switch (artifactReportFocus) {
            case ArtifactReportFocus.LatestMasterCommit:
                return fetchArtifactReportFromLatestMasterCommit(gitProject.id);
            case ArtifactReportFocus.LatestProdDeploy:
                return fetchArtifactReportFromLatestProdDeploy(gitProject.id)
        }
    }

    function getSeverityClass(severity: string) {
        switch (severity?.toLowerCase()) {
            case "critical":
                return styles.severityCritical;
            case "high":
                return styles.severityHigh;
            case "medium":
                return styles.severityMedium;
            case "low":
                return styles.severityLow;
            default:
                return styles.severityDefault;
        }
    }

    return (
        <div className={`${styles.card} ${artifactReport?.severity ? getSeverityClass(artifactReport.severity) : ""}`}>
            <Link
                className={styles.projectLink}
                to={`/project/${gitProject.id}`}>

                <div className={styles.projectName}>
                    {gitProject.projectGroupPath && (
                        <div>{gitProject.projectGroupPath} </div>
                    )}
                    <div className={styles.projectName}> {gitProject.name}</div>
                </div>
                {artifactReport?.severity ? (
                        <div>
                            {artifactReport.severity ?? "Unknown"}
                        </div>) :

                    (
                        <div className={styles.unknownSeverity}>
                            <img src={question_mark} alt="Unknown Severity"/>
                        </div>)
                }
            </Link>
            <div className={styles.externalLinks}>
                <div className={styles.iconContainer}>
                    <a
                        href={gitProject.gitlabLink}
                        target="_blank"
                        rel="noopener noreferrer">
                        <img src={gitlab_icon} alt="Gitlab Logo"/>
                    </a>
                </div>
                {artifactReport?.repositoryLink && (
                    <div className={styles.iconContainer}>
                        <a
                            href={artifactReport.repositoryLink}
                            target="_blank"
                            rel="noopener noreferrer">
                            <img
                                src={harbor_icon}
                                alt="Harbor Logo"/>
                        </a>
                    </div>
                )}
            </div>
        </div>
    )

}