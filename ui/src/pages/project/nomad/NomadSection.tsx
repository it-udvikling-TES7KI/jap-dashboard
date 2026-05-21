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
                            <a href={job.nomadLink} target="_blank" rel="noopener noreferrer" className={styles.cardHeader}>
                                <div className={styles.jobName}>{job.name}</div>
                                <div className={styles.iconContainer}>
                                    <img src={nomad_icon} alt={"Nomad Logo"}/>
                                </div>
                                <div className={styles.jobStatus}>
                                    Nomad Status: {job.nomadStatus || "unknown"}
                                </div>
                            </a>
                            <a
                                href={job.serviceLink}
                                target="_blank"
                                rel="noopener noreferrer"
                                className={styles.cardContent}>
                                <span className={styles.serviceLink}> {job.serviceLink} </span>
                                <div className={styles.jobStatus}>
                                    Health Check: {job.healthStatus || "unknown"}
                                </div>
                            </a>
                        </div>
                    ))}
                </div>
            </div>
        </ProjectSection>
    )

}