import styles from "./NomadSection.module.css";
import nomad_icon from "../../../assets/nomad_icon.svg"
import {useQuery} from "@tanstack/react-query";
import {fetchNomadJobsByProjectName} from "../../../hooks/NomadHook.ts";
import ProjectSection from "../ProjectSection.tsx";

interface NomadSectionProps {
    projectName: string;

}

export function NomadSection({projectName}: NomadSectionProps) {

    const {isError, error, isPending, data: nomadJobs} = useQuery({
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
                <div className={styles.jobList}>
                    {nomadJobs?.map((job) => (
                        <div key={job.id} className={styles.jobCard}>
                            <div className={styles.cardHeader}>
                                <div className={styles.jobName}>{job.name}</div>
                                <div className={styles.iconContainer}>
                                    <a href={job.nomadLink} target="_blank" rel="noopener noreferrer">
                                        <img src={nomad_icon} alt={"Nomad Logo"}/>
                                    </a>
                                </div>
                            </div>
                            <div className={styles.links}>
                                {job.serviceLink ?
                                    <a href={job.serviceLink}
                                       target="_blank"
                                       rel="noopener noreferrer">
                                        {job.serviceLink}
                                    </a>
                                    :
                                    "no link to service was found"}
                            </div>
                        </div>
                    ))}
                </div>
            </div>
        </ProjectSection>
    )

}