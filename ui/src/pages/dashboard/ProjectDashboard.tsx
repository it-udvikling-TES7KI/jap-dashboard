import {useQuery} from "@tanstack/react-query";
import styles from "./ProjectDashboard.module.css";
import {fetchProjectPreviews} from "../../hooks/ProjectHook.ts";
import ProjectCard from "./ProjectCard.tsx";

export default function ProjectDashboard() {

    const {isError, error, isPending, data: gitLabProjects} = useQuery({queryKey: ['projectPreviews'], queryFn: fetchProjectPreviews})

    if (isPending) {
        return <span>Loading...</span>
    }

    if (isError) {
        return <span>Error: {error.message}</span>
    }

    return (
        <div>
            <h1 className={styles.header}>Project Dashboard</h1>
            <nav></nav>
            <div className={styles.page}>
                <div className={styles.grid}>
                    {gitLabProjects.map((project, index) => (
                        <ProjectCard project={project} key={index}/>
                    ))}
                </div>
            </div>
        </div>
    );
}