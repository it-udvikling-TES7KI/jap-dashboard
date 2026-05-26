import {useParams} from "react-router-dom";

import styles from "./Project.module.css";
import {NomadSection} from "./nomad/NomadSection.tsx";
import {ArtifactReportSection} from "./harbor/ArtifactReportSection.tsx";
import {useQuery} from "@tanstack/react-query";
import {fetchGitlabProjectById} from "../../hooks/GitlabHook.ts";
import DocSection from "./docs/DocsSection.tsx";

export default function Project() {

    const {projectId} = useParams<{ projectId: string }>();
    const projectIdNumber = Number(projectId);

    if (!projectId || Number.isNaN(projectIdNumber)) {
        return <div>Invalid project id.</div>;
    }

    const {error, data: gitlabProject} = useQuery({
        queryKey: ["gitlabProject", projectId],
        queryFn: async () => {
            if (projectId) return fetchGitlabProjectById(projectId);
        },
        enabled: !!projectId
    })

    if (error) {
        return <div>Error fetching project data: {error.message}</div>;
    }

    if (!gitlabProject) {
        return <div>No project data found.</div>;
    }

    return (
        <div className={styles.container}>
            <h1 className={styles.projectName}>{gitlabProject?.name}</h1>
            <div className={styles.sections}>
                <div className={styles.doubleSection}>
                    <DocSection projectName={gitlabProject.name}/>
                    <ArtifactReportSection projectId={projectIdNumber}/>
                </div>
                <NomadSection projectId={projectIdNumber} projectName={gitlabProject?.name}/>
            </div>
        </div>
    );
}