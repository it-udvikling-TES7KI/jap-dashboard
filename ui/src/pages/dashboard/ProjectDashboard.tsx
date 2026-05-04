import {useQuery} from "@tanstack/react-query";
import {fetchGitlabProjects} from "../gitlab/GitlabHook.ts";
import styles from "./ProjectDashboard.module.css";
import gitlab_icon from "../../assets/gitlab_icon.svg";
import {Link} from "react-router-dom";

export default function ProjectDashboard() {

    const {isError, error, isPending, data: gitLabProjects} = useQuery({queryKey: ['gitlabProjects'], queryFn: fetchGitlabProjects})

    if (isPending) {
        return <span>Loading...</span>
    }

    if (isError) {
        return <span>Error: {error.message}</span>
    }

    return (
        <div>
            <h1 className={styles.header}>Project Dashboard</h1>
            <div className={styles.page}>
                <div className={styles.grid}>
                    {gitLabProjects.map((project, index) => (
                        <div key={index}
                             className={styles.card}>
                            <Link
                                className={styles.projectLink}
                                to={`/project/${project.name}`}>
                                <div className={styles.projectName}>{project.name}</div>
                            </Link>
                            <div className={styles.externalLinks}>
                                <div className={styles.iconContainer}>
                                    <a
                                        href={project.web_url}
                                        target="_blank"
                                        rel="noopener noreferrer"
                                        className={styles.link}>
                                        <img src={gitlab_icon} alt="Gitlab Logo"/>
                                    </a>
                                </div>
                            </div>
                        </div>
                    ))}
                </div>
            </div>
        </div>
    );
}