import styles from "./NomadSection.module.css";
import NomadJobCard from "./NomadJobCard";
import {useQuery} from "@tanstack/react-query";
import {fetchNomadJobsByProjectName} from "../../../hooks/NomadHook.ts";
import ProjectSection from "../ProjectSection.tsx";

interface NomadSectionProps {
    projectId: number;
    projectName: string;
}

export function NomadSection({projectId, projectName}: NomadSectionProps) {

    const {isError, error, isPending, data: nomadJobs = []} = useQuery({
        queryKey: ['nomadJobs', projectName],
        queryFn: () => {
            if (projectName) return fetchNomadJobsByProjectName(projectName);
        },
        enabled: !!projectName
    });

    if (isPending) {
        return <span>Loading...</span>
    }

    if (isError) {
        return <span>Error: {error.message}</span>
    }


    return (
        <ProjectSection title={"Nomad Jobs"}>
            <div className={styles.nomadSection}>
                {nomadJobs.length > 0 ?
                    (<div className={styles.jobList}>
                        {nomadJobs?.map((job) => (
                            <NomadJobCard key={job.id} projectId={projectId} nomadJob={job}/>
                        ))}
                    </div>) :
                    (<span>No Nomad jobs found for this project.</span>)
                }
            </div>
        </ProjectSection>
    )

}