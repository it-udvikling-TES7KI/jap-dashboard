import {useParams} from "react-router-dom";

import styles from "./Project.module.css";
import {NomadSection} from "./nomad/NomadSection.tsx";
import {ArtifactReportSection} from "./harbor/ArtifactReportSection.tsx";

export default function Project() {

    const {projectName} = useParams()
    
    if (!projectName) return <div>No project data found.</div>;

    return (
        <div className={styles.container}>
            <h1 className={styles.projectName}>{projectName}</h1>
            <NomadSection projectName={projectName}/>
            <ArtifactReportSection projectName={projectName}/>
        </div>
    );
}