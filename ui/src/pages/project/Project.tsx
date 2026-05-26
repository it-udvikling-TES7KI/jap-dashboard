import {useNavigate, useParams} from "react-router-dom";
import styles from "./Project.module.css";
import {NomadSection} from "./nomad/NomadSection.tsx";
import {ArtifactReportSection} from "./harbor/ArtifactReportSection.tsx";
import {useQuery} from "@tanstack/react-query";
import {fetchGitlabProjectById} from "../../hooks/GitlabHook.ts";
import DocSection from "./docs/DocsSection.tsx";
import back_arrow_icon from "../../assets/back_arrow.svg";

export default function Project() {
    const navigate = useNavigate();
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
            <div className={styles.header}>
                <div className={styles.iconContainer}
                     onClick={() => navigate(-1)}>
                    <img
                        src={back_arrow_icon}
                        alt="Back"
                        aria-label="Go back"
                    />
                </div>
                <h1 className={styles.projectName}>{gitlabProject?.name}</h1>
            </div>
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