import {useQuery} from "@tanstack/react-query";
import styles from "./ProjectDashboard.module.css";
import gitlab_icon from "../../assets/gitlab_icon.svg";
import question_mark from "../../assets/question_mark.svg";
import harbor_icon from "../../assets/harbor_icon.svg";
import {Link} from "react-router-dom";
import {fetchProjectPreviews} from "../../hooks/ProjectHook.ts";

export default function ProjectDashboard() {

    const {isError, error, isPending, data: gitLabProjects} = useQuery({queryKey: ['projectPreviews'], queryFn: fetchProjectPreviews})

    if (isPending) {
        return <span>Loading...</span>
    }

    if (isError) {
        return <span>Error: {error.message}</span>
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
        <div>
            <h1 className={styles.header}>Project Dashboard</h1>
            <div className={styles.page}>
                <div className={styles.grid}>
                    {gitLabProjects.map((project, index) => (
                        <div key={index}
                             className={`${styles.card} ${getSeverityClass(project.artifactReport?.severity)}`}>
                            <Link
                                className={styles.projectLink}
                                to={`/project/${project.gitlabProject.name}`}>

                                {project.gitlabProject.projectGroupPath ? (
                                        <div className={styles.projectName}>
                                            <div>{project.gitlabProject.projectGroupPath} </div>
                                            <div>{project.gitlabProject.name}</div>
                                        </div>
                                    ) :
                                    (
                                        <div className={styles.projectName}> {project.gitlabProject.name}</div>
                                    )
                                }
                                {project.artifactReport?.severity ? (
                                        <div>
                                            {project.artifactReport?.severity ?? "Unknown"}
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
                                {project.artifactReport?.harborLink && (
                                    <div className={styles.iconContainer}>
                                        <a
                                            href={project.artifactReport?.harborLink}
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
                    ))}
                </div>
            </div>
        </div>
    );
}