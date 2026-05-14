import {useQuery} from "@tanstack/react-query";
import styles from "./ProjectDashboard.module.css";
import {fetchProjectPreviews} from "../../hooks/ProjectHook.ts";
import ProjectCard from "./ProjectCard.tsx";
import {useState} from "react";
import {FocusedArtifactReport} from "./FocusedArtifactReport.tsx";

export default function ProjectDashboard() {
    const [activeTab, setActiveTab] = useState(FocusedArtifactReport.LatestMasterCommit);
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
                <nav className={styles.tabs}>
                    <div className={`${styles.tab} ${styles.tabHeader}`}>Focused Artifact Report:</div>
                    <div onClick={() => setActiveTab(FocusedArtifactReport.LatestMasterCommit)}
                         className={`${styles.tab} ${activeTab === FocusedArtifactReport.LatestMasterCommit ? styles.activeTab : ""}`}>
                        Latest Master Commit
                    </div>
                    <div onClick={() => setActiveTab(FocusedArtifactReport.LatestProdDeploy)}
                         className={`${styles.tab} ${activeTab === FocusedArtifactReport.LatestProdDeploy ? styles.activeTab : ""}`}>
                        Latest Prod Deploy
                    </div>
                </nav>

            <div className={styles.page}>
                <div className={styles.grid}>
                    {gitLabProjects.map((project, index) => (
                        <ProjectCard project={project} focusedArtifactReport={activeTab} key={index}/>
                    ))}
                </div>
            </div>
        </div>
    );
}