import {useQuery} from "@tanstack/react-query";
import {fetchGitlabProjects} from "./ProjectsHook";
import styles from "./Overview.module.css";

export default function GitLabProjectOverview() {

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
                        <div key={index} className={styles.card}>
                            <a
                                href={project.web_url}
                                target="_blank"
                                rel="noopener noreferrer"
                                className={styles.link}
                            >
                                {project.name}
                            </a>
                        </div>
                    ))}
                </div>
            </div>
        </div>
    );
}