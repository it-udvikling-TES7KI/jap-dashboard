import styles from "./NomadSection.module.css";
import nomad_icon from "../../../assets/nomad_icon.svg"
import logscale_icon from "../../../assets/logscale_icon.svg"
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
                                    Nomad Status: <strong> {job.nomadStatus || "unknown"} </strong>
                                </div>
                            </a>
                            <a
                                href={job.serviceLink}
                                target="_blank"
                                rel="noopener noreferrer"
                                className={styles.serviceLinkContainer}>
                                <span className={styles.serviceLink}> {job.serviceLink} </span>
                                <div className={styles.jobStatus}>
                                    Health Check: <strong> {job.healthStatus || "unknown"} </strong>
                                </div>
                            </a>
                            <div
                                className={styles.externalLinks}>
                                <a
                                    href={job.logscaleLink}
                                    target="_blank"
                                    rel="noopener noreferrer"
                                    className={styles.iconContainer}>
                                    <img src={logscale_icon} alt={"LogScale logo"}/>
                                </a>
                            </div>
                        </div>
                    ))}
                </div>
            </div>
        </ProjectSection>
    )

}