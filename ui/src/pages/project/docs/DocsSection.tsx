import styles from "../Project.module.css";
import ProjectSection from "../ProjectSection.tsx";
import {useQuery} from "@tanstack/react-query";
import {fetchProdNomadJobsByProjectName} from "../../../hooks/NomadHook.ts";

export interface DocSection {
    projectName: string;
}

export default function DocSection({projectName}: DocSection) {

    const {data: prodNomadJob} = useQuery({
        queryKey: ["prodNomadJob", projectName],
        queryFn: () => fetchProdNomadJobsByProjectName(projectName),
        enabled: !!projectName
    })

    return (
        <ProjectSection title={"Docs"}>
            {prodNomadJob?.docsURL ? (
                <div className={styles.docSection}>
                    <iframe
                        className={styles.iframe}
                        src={prodNomadJob.docsURL}
                    />
                </div>
            ) : (
                <span> No Prod Job could be found</span>
            )}
        </ProjectSection>
    )
}