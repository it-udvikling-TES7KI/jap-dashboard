import {ProjectPreview} from "../../types/ProjectPreview";
import styles from "./ProjectCard.module.css"
import {Link} from "react-router-dom";
import question_mark from "../../assets/question_mark.svg";
import gitlab_icon from "../../assets/gitlab_icon.svg";
import harbor_icon from "../../assets/harbor_icon.svg";
import {FocusedArtifactReport} from "./FocusedArtifactReport.tsx";

export interface ProjectCardProps {
    project: ProjectPreview;
    focusedArtifactReport: FocusedArtifactReport;
}

export default function ProjectCard({project, focusedArtifactReport}: ProjectCardProps) {

   const focusedSeverity = determineFocusedSeverity();

   function determineFocusedSeverity() {
       switch (focusedArtifactReport) {
           case FocusedArtifactReport.LatestMasterCommit:
               return project.latestMasterCommitReport?.severity;
           case FocusedArtifactReport.LatestProdDeploy:
               return project.latestProdDeployReport?.severity;
           default:
               return undefined;
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
        <div className={`${styles.card} ${focusedSeverity ? getSeverityClass(focusedSeverity) : ""}`}>
            <Link
                className={styles.projectLink}
                to={`/project/${project.gitlabProject.name}`}>

                <div className={styles.projectName}>
                    {project.gitlabProject.projectGroupPath && (
                        <div>{project.gitlabProject.projectGroupPath} </div>
                    )}
                    <div className={styles.projectName}> {project.gitlabProject.name}</div>
                </div>
                {focusedSeverity ? (
                        <div>
                            {focusedSeverity ?? "Unknown"}
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
                        href={project.gitlabProject.gitlabLink}
                        target="_blank"
                        rel="noopener noreferrer">
                        <img src={gitlab_icon} alt="Gitlab Logo"/>
                    </a>
                </div>
                {project.latestMasterCommitReport?.repositoryLink && (
                    <div className={styles.iconContainer}>
                        <a
                            href={project.latestMasterCommitReport?.repositoryLink}
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